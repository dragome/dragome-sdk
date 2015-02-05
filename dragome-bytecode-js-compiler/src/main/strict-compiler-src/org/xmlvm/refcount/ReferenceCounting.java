/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.refcount;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.xmlvm.refcount.optimizations.DeferredNullingOptimization;
import org.xmlvm.refcount.optimizations.RefCountOptimization;
import org.xmlvm.refcount.optimizations.RegisterSizeAndNullingOptimization;

/**
 * Overview:
 * 
 * This class uses reference counting to simulate the effects of a JVM garbage
 * collector. It operates on the output of the DEX XMLVM process which is a XML
 * based representation of a compiled java class that has been transformed from
 * a stack machine to a register machine. This class manages object lifespan
 * using a reference counting approach. For example, when an object is created
 * it is given a reference count of 1. During the lifespan of the object the
 * reference count is incremented as other objects store away pointers to the
 * object in question. Eventually, the reference count is set to zero and the
 * memory for the object is released.
 * 
 * This class is interesting because manages these reference counts without any
 * client programmer intervention to simulate the effects of a garbage. This is
 * done by following a few simple rules: 1) Each pointer to an object always has
 * a 'retain' associated with it. For example, if a register variable points
 * toward an object then the following pattern would occur: Register r1 =
 * object1; [r1 retain];
 * 
 * When the pointer's end of life occurred, we would release the object
 * reference [r1 release]; r1 = null;
 * 
 * The same concept holds for other classes of pointer including, arrays, static
 * references to objects, as well as instance references. In general the
 * following invariant always holds true:
 * 
 * If there is a pointer to an object, than that pointer has associated with it
 * an increment for the objects reference count. If that pointer is overwritten
 * for any reason, that reference is released.
 * 
 * Based on this invariant, we know that when we can no longer point to an
 * object all of its reference count increments will be gone and hence it will
 * be freed by the runtime.
 * 
 * Usage: For a <dex:method/> represented as a jdom.Element, call Process.
 */
public class ReferenceCounting
{
	Namespace dex= InstructionProcessor.dex;
	Namespace vm= InstructionProcessor.vm;
	String tmpRegNameSuffix= "tmp";

	/**
	 * The entry point to this class. This function takes a method element and
	 * processes it, adding instructions to release and retain objects as
	 * needed. For the command set that it adds, see the InstructionProcessor
	 * class.
	 */
	@SuppressWarnings("unchecked")
	public void process(Element method) throws DataConversionException, ReferenceCountingException
	{

		Attribute isAbstract= method.getAttribute("isAbstract");
		Attribute isNative= method.getAttribute("isNative");
		// abstract and native methods do not require processing
		if (isAbstract != null && isAbstract.getBooleanValue())
		{
			return;
		}

		if (isNative != null && isNative.getBooleanValue())
		{
			return;
		}

		Element codeElement= method.getChild("code", dex);

		int numReg= codeElement.getAttribute("register-size").getIntValue();
		processRecStart(numReg, (List<Element>) codeElement.getChildren(), codeElement);
	}

	/**
	 * Set the expected frees that we must do before any optimizations have
	 * removed them.
	 */
	private void setWillFree(Map<Element, InstructionActions> beenTo) throws ReferenceCountingException, DataConversionException
	{
		{
			for (Map.Entry<Element, InstructionActions> e : beenTo.entrySet())
			{
				RegisterSet objectRegs= e.getValue().getObjectRegs();

				if (!e.getValue().getConflict().isEmpty())
				{
					throw new ReferenceCountingException("Ambigious register contents possible: Conflict: " + e.getValue().getConflict());
				}

				InstructionUseInfo useInfo= e.getValue().useInfo;
				RegisterSet toFree;
				if (e.getKey().getName().startsWith("return"))
				{
					// we want to free everything except what this instruction
					// uses.
					toFree= objectRegs.andNot(useInfo.usedReg());
				}
				else
				{
					// we free any register reference that is overwritten by
					// this
					// instruction
					toFree= objectRegs.and(useInfo.allWrites());
				}
				useInfo.willFree= toFree;
				useInfo.willNull= toFree.clone();

			}
		}
	}

	/**
	 * This is the last step in the release/retain markup process. It processes
	 * all of the DEX instructions that we have traversed in a method, looking
	 * at how they have been marked up. Based on how they have been marked up it
	 * adds required release/retains or other commands to the body of the method
	 * being processed.
	 * 
	 * Returns whether this method needs to have a temp register defined.
	 */
	private boolean processReleaseRetain(Map<Element, InstructionActions> beenTo) throws ReferenceCountingException, DataConversionException
	{
		boolean needsTmpReg= false;

		for (Map.Entry<Element, InstructionActions> e : beenTo.entrySet())
		{

			if (!e.getValue().getConflict().isEmpty())
			{
				throw new ReferenceCountingException("Ambigious register contents possible: Conflict: " + e.getValue().getConflict());
			}

			InstructionUseInfo useInfo= e.getValue().useInfo;

			// if we are writing into an object, we may need to free.
			// All objects in registers are held with a reference, so we will
			// need to release.
			List<Element> toAddBefore= new ArrayList<Element>();
			List<Element> toAddAfter= new ArrayList<Element>();
			// Release last -- because other wise we can get into odd situations
			// where we don't to a required retain before the release.
			List<Element> toReleaseLast= new ArrayList<Element>();
			RegisterSet toFree;
			toFree= useInfo.willFree;
			// for the registers we want to free
			for (int oneReg : toFree)
			{
				// If we use the object in the instruction as an argument and
				// overwrite it, we must be careful to preserve it until after
				// the call is done.
				// Example of true case
				// tmp = f1;
				// f1 = func(f1);
				// [release tmp];
				// Example of false case:
				// f1 = func(f1);
				// [release f1]
				if (!useInfo.usesAsObj().and(useInfo.allWrites()).isEmpty())
				{
					if (useInfo.freeTmpAfter)
					{
						throw new ReferenceCountingException("Conflict, tmp register used twice.");
					}

					Element tmpR= new Element(InstructionProcessor.cmd_tmp_equals_r, vm);
					tmpR.setAttribute("reg", oneReg + "");
					toAddBefore.add(tmpR);
					needsTmpReg= true;

					Element releaseTmp= new Element(InstructionProcessor.cmd_release, vm);
					releaseTmp.setAttribute("reg", tmpRegNameSuffix);
					toReleaseLast.add(releaseTmp);

					Element nullTmp= new Element(InstructionProcessor.cmd_set_null, vm);
					nullTmp.setAttribute("num", tmpRegNameSuffix);
					toReleaseLast.add(nullTmp);

				}
				else
				{
					// No need to use tmp
					Element release= new Element(InstructionProcessor.cmd_release, vm);
					release.setAttribute("reg", oneReg + "");
					toAddBefore.add(release);

					if (useInfo.willNull.has(oneReg))
					{
						Element nullTmp= new Element(InstructionProcessor.cmd_set_null, vm);
						nullTmp.setAttribute("num", oneReg + "");
						toAddBefore.add(nullTmp);

					}
				}
			}

			if (useInfo.putRelease != null)
			{
				if (!useInfo.usesAsObj().and(useInfo.allWrites()).isEmpty())
				{
					needsTmpReg= true;
					throw new ReferenceCountingException("We do not handle the case where a release is " + "made in a x = foo(x) situation because it " + " hasn't showed up so far");
				}
				else
				{
					toAddBefore.add(useInfo.putRelease);
				}
			}

			// Add any necessary retains.
			for (int oneReg : useInfo.requiresRetain)
			{
				Element retain= new Element(InstructionProcessor.cmd_retain, vm);
				retain.setAttribute("reg", oneReg + "");
				toAddAfter.add(retain);
			}

			// This handles the case where xmlvm2objc.xsl has set the temp reg
			// to a value because a function call was made, but the result was
			// not used by the program.
			if (useInfo.freeTmpAfter)
			{

				Element releaseTmp= new Element(InstructionProcessor.cmd_release, vm);
				releaseTmp.setAttribute("reg", tmpRegNameSuffix);
				toAddAfter.add(releaseTmp);
				needsTmpReg= true;

			}
			toAddAfter.addAll(toReleaseLast);

			// At this point toAddBefore and toAddAfter have been filled with
			// whatever instructions we need to add before and after this
			// specific element. The helper function adds them.
			addBeforeAndAfter(e.getKey(), toAddBefore, toAddAfter);

		}
		return needsTmpReg;
	}

	/**
	 * This is here because the jdom XML API is dumb enough that it cannot
	 * easily find the element before element X, or the element after element X.
	 * 
	 * This function adds some elements before and after a particular element.
	 * 
	 * TODO: if we believe prevElement map and nextElement map are correct, then
	 * we can use them instead to make this run faster.
	 */
	@SuppressWarnings("unchecked")
	void addBeforeAndAfter(Element toAddTo, List<Element> toAddBefore, List<Element> toAddAfter) throws ReferenceCountingException
	{
		Element parent= toAddTo.getParentElement();
		List<Object> con= parent.getContent();

		// go through the parents elements looking for this element
		for (int x= 0; x < con.size(); x++)
		{
			if (con.get(x).equals(toAddTo))
			{
				// order here matters so we don't screw up the index for the
				// before add.
				parent.addContent(x + 1, toAddAfter);
				parent.addContent(x, toAddBefore);

				return;
			}
		}
		throw new ReferenceCountingException("Impossible");
	}

	/**
	 * label id to label element. Used for construction of code paths.
	 */
	Map<Integer, Element> labels= new HashMap<Integer, Element>();
	/**
	 * What is the next and previous element for a particular element ?
	 */
	Map<Element, Element> nextElement= new HashMap<Element, Element>();
	Map<Element, Element> prevElement= new HashMap<Element, Element>();

	/**
	 * Represents a single run of the reference counter. We have this because we
	 * currently use a two pass implementation and don't want any interactions
	 * between the passes.
	 * 
	 * TODO: We could prevent having to do a whole lot of work in the second
	 * pass if we care to.
	 */
	class RunState
	{
		public List<CodePath> allCodePaths= new ArrayList<CodePath>();
		/*
		 * What we label the next code path as
		 */
		public int codePathId= 0;
		/*
		 * List of all the elements that we have visited in the method.
		 */
		public Map<Element, InstructionActions> beenTo= new HashMap<Element, InstructionActions>();
		/*
		 * What are the conflicted registers on this run?
		 */
		public RegisterSet allConflict= RegisterSet.none();
		/*
		 * Used so we don't have to use stack recursion, which apparently causes
		 * big problems in the JVM.
		 */
		LinkedList<OneRecusiveCall> callsToDo= new LinkedList<OneRecusiveCall>();
	}

	/*
	 * Our current run context. TODO: pass this down the stack instead of having
	 * it be an instance variable.
	 */
	RunState curRun;

	/**
	 * This adds any labels it finds to our labels map. It also populates our
	 * previous and next element hashes.
	 */
	private void addToNextPrevElement(List<Element> toProcess) throws DataConversionException
	{
		Element prev= null;
		for (int k= 0; k < toProcess.size(); k++)
		{
			Element cur= toProcess.get(k);

			if (cur.getName().equals("label"))
			{
				labels.put(cur.getAttribute("id").getIntValue(), cur);
			}

			if (prev != null)
			{
				nextElement.put(prev, cur);
				prevElement.put(cur, prev);
			}
			prev= cur;
		}
	}

	@SuppressWarnings("unchecked")
	private void processRecStart(int numReg, List<Element> toProcess, Element codeElement) throws DataConversionException, ReferenceCountingException
	{
		addToNextPrevElement(toProcess);
		for (Element x : toProcess)
		{
			if (x.getName().equals("try-catch"))
			{
				// Try catches are special: we must descend into them to
				// mark up their code.
				addToNextPrevElement(x.getChild("try", dex).getChildren());
				for (Element catchE : (List<Element>) x.getChildren("catch", dex))
				{
					addToNextPrevElement(catchE.getChildren());
				}
			}

		}

		doMarkup(toProcess);

		curRun.allConflict= RegisterSet.none();
		for (Entry<Element, InstructionActions> x : curRun.beenTo.entrySet())
		{
			curRun.allConflict.orEq(x.getValue().getConflict());
		}

		// the method element.
		Element parent= toProcess.get(0).getParentElement();

		// We found some code paths that end up with a register that has an
		// object OR a primitive at a point where we think we need to do a
		// object release. This is bad, so we must split the register so that
		// the primitive is always separate from the object.
		if (!curRun.allConflict.isEmpty())
		{
			int newRegSize= splitConflictedRegisters(numReg, curRun.allConflict, curRun.beenTo);
			parent.getAttribute("register-size").setValue(newRegSize + "");

			doMarkup(toProcess);

		}

		refLog("Conflict is: " + curRun.allConflict);

		setWillFree(curRun.beenTo);

		// Start going through optimizations before generating change
		RefCountOptimization.ReturnValue ret= new RegisterSizeAndNullingOptimization().Process(curRun.allCodePaths, curRun.beenTo, codeElement);

		new DeferredNullingOptimization().Process(curRun.allCodePaths, curRun.beenTo, codeElement);

		// TODO fix this optimization
		// new ExcessRetainsOptimization().Process(curRun.allCodePaths,
		// curRun.beenTo, codeElement);
		toProcess.addAll(0, ret.functionInit);

		addExTempReg(toProcess);

		clearReleaseRetainOnSyntheticMembers(curRun, codeElement);
		// Now we want to follow the paths to find unambiguous ones so that we
		// can determine
		// where to do release/retain to prevent ambiguity.
		// we do this by tracking which branch we are on by explicitly
		// constructing paths through the code during
		// our normal traversal.
		boolean usesTemp= processReleaseRetain(curRun.beenTo);
		if (usesTemp)
		{
			Element setupTmp= new Element(InstructionProcessor.cmd_define_register, InstructionProcessor.vm);
			setupTmp.setAttribute("vartype", InstructionProcessor.cmd_define_register_attr_temp);
			toProcess.add(0, setupTmp);
		}

	}

	/*
	 * Synthetics help create cycles so we don't do releases or retains on them.
	 */
	@SuppressWarnings("unchecked")
	private void clearReleaseRetainOnSyntheticMembers(RunState curRun, Element codeElement) throws DataConversionException
	{
		// Find the synthetic members of the class;
		Element classElement= codeElement.getParentElement().getParentElement();

		HashSet<String> hashSet= new HashSet<String>();

		for (Element elem : (List<Element>) classElement.getChildren())
		{
			if (elem.getName().equals("field") && elem.getAttribute("isSynthetic") != null && elem.getAttributeValue("isSynthetic").equals("true") && elem.getAttributeValue("name").startsWith("this$"))
			{
				hashSet.add(elem.getAttributeValue("name"));
			}
		}

		for (Map.Entry<Element, InstructionActions> e : curRun.beenTo.entrySet())
		{

			String instructionElementName= e.getKey().getName();
			if ((instructionElementName.equals("iput-object") || instructionElementName.equals("iput")) && e.getKey().getAttribute("member-name") != null && hashSet.contains(e.getKey().getAttributeValue("member-name")))
			{
				InstructionUseInfo useInfo= e.getValue().useInfo;
				// We don't want to release what was in there because it was not
				// retained
				useInfo.putRelease= null;
				useInfo.requiresRetain= RegisterSet.none();
			}
		}
	}

	/**
	 * Adds definition for exception register if needed.
	 */
	private void addExTempReg(List<Element> toProcess)
	{
		boolean useEx= false;
		boolean useTmp= false;
		for (Element e : curRun.beenTo.keySet())
		{
			if (e.getName().equals("throw") || e.getName().equals("try-catch"))
			{
				useEx= true;
			}

			if (useEx && useTmp)
			{
				break; // early quit
			}
		}
		if (useEx)
		{
			Element setupEx= new Element(InstructionProcessor.cmd_define_register, InstructionProcessor.vm);
			setupEx.setAttribute("vartype", InstructionProcessor.cmd_define_register_attr_exception);
			toProcess.add(0, setupEx);
		}
		for (Element e : curRun.beenTo.keySet())
		{
			if (e.getName().startsWith("return"))
			{
				e.setAttribute("catchesException", useEx + "");
			}
		}
	}

	/**
	 * Determines conflicts and retain/release for the method.
	 */
	private void doMarkup(List<Element> toProcess) throws DataConversionException
	{
		// create a new run of the processor, prime the recursion, and then
		// run it until its complete.
		curRun= new RunState();
		processRecAdd(RegisterSet.none(), RegisterSet.none(), toProcess.get(0), createNewCodePath(null));
		processWhileCallsToDo();
		// Debug print for state at this stage.
		//printInstSeq(toProcess);
	}

	/*
	 * Purely for debug, shows the instructions as well as our what we have
	 * calculated about them.
	 */
	@SuppressWarnings("unchecked")
	private void printInstSeq(List<Element> toProcess)
	{
		refLog("All " + toProcess.size() + " instructions been to " + curRun.beenTo.size());
		for (Element x : toProcess)
		{
			if (curRun.beenTo.containsKey(x))
			{
				String startStr= curRun.beenTo.get(x).useInfo + "";
				if (x.getName().equals("label"))
				{
					refLog(startStr + " ID = " + x.getAttributeValue("id"));
				}
				else
				{
					refLog(startStr + "");
				}

				if (x.getName().equals("try-catch"))
				{
					printInstSeq(x.getChild("try", dex).getChildren());
				}
			}
		}
	}

	/**
	 * In certain cases, DEX will create a code path where we think we need to
	 * do a release of an object on a particular register which may or may not
	 * hold an object depending on the particular path through the code taken at
	 * runtime. There are several ways to approach this issue, the most simple
	 * is to split a conflicted register into two new registers. Conceptually,
	 * this is done by defining a function that maps RX to RY or RZ depending on
	 * whether RX is known to hold an object or a primitive.
	 * 
	 * The following code implements this mapping, with the slight optimization
	 * that instead of mapping RX to RY and RZ, it maps it to RX and RY. This is
	 * because it keeps the sequence of registers intact with no holes, and
	 * because it allows us to deal with function parameters more easily.
	 * 
	 * We return the total number of registers required for this method.
	 */
	int splitConflictedRegisters(int numReg, RegisterSet allConflict, Map<Element, InstructionActions> beenTo) throws DataConversionException, ReferenceCountingException
	{

		for (int reg : allConflict)
		{
			int newReg= numReg++;

			// When dealing with a passed parameter that has a conflict, we want
			// to make sure to not change the register that the parameter is
			// originally inserted into.
			int regObj= reg;
			int regNonObj= newReg;

			for (Element varE : beenTo.keySet())
			{
				if (varE.getName().equals("var"))
				{
					InstructionUseInfo varUi= this.curRun.beenTo.get(varE).useInfo;
					if (varUi.isWrite)
					{
						if (!varUi.writesObj().isEmpty())
						{
							regObj= reg;
							regNonObj= newReg;
							break;
						}
						else if (!varUi.writesNonObj().isEmpty())
						{
							regObj= newReg;
							regNonObj= reg;
						}
						else
						{
							throw new ReferenceCountingException("impossible");
						}
					}
				}
			}

			refLog(reg + " -> o:" + regObj + ":" + regNonObj);

			// Go through all the instructions in the method, replacing any
			// that use the conflicted value with the new register or the old
			// register depending on whether the instruction expects the
			// register to contain an object or a primitive.
			for (Map.Entry<Element, InstructionActions> beenToKv : beenTo.entrySet())
			{
				InstructionUseInfo ui= beenToKv.getValue().useInfo;

				for (Map.Entry<Attribute, Boolean> kv : ui.typeIsObj.entrySet())
				{
					if (kv.getKey().getIntValue() == reg)
					{
						if (kv.getValue())
						{
							// its an object
							kv.getKey().setValue(regObj + "");
						}
						else
						{
							kv.getKey().setValue(regNonObj + "");
						}
					}
				}
			}
		}
		return numReg;
	}

	/**
	 * Given a parent code path, create a child.
	 */
	private CodePath createNewCodePath(CodePath curPath)
	{
		CodePath c;
		c= new CodePath(curRun.codePathId++, curPath);
		curRun.allCodePaths.add(c);
		if (curPath != null)
		{
			curPath.subPaths.add(c);
		}
		return c;
	}

	/**
	 * Class representing collected parameters for one execution of the body of
	 * ProcessWhileCallsToDo
	 */
	class OneRecusiveCall
	{
		RegisterSet regHoldingObject;
		RegisterSet regNotHoldingObject;
		Element currentElement;
		CodePath codePath;
	}

	/**
	 * Helper to add to the list of recursive calls to do.
	 */
	private void processRecAdd(RegisterSet regHoldingObject, RegisterSet regNotHoldingObject, Element currentElement, CodePath codePath) throws DataConversionException
	{
		OneRecusiveCall oneCall= new OneRecusiveCall();
		oneCall.regHoldingObject= regHoldingObject;
		oneCall.regNotHoldingObject= regNotHoldingObject;
		oneCall.currentElement= currentElement;
		oneCall.codePath= codePath;
		this.curRun.callsToDo.add(oneCall);
	}

	/**
	 * This function creates a representation of the different execution paths
	 * through the method. At the same time, it gathers information on how
	 * particular instructions are making use of registers. This is an
	 * implementation of a recursive function, however when implemented as a
	 * directly recursive function (without the calls to do) the JVM blows up
	 * for lack of stack space. Because modifying stack space available to a
	 * thread in the JVM (even if you create a new thread) is a pain, we just
	 * switched to using a heap list for the recursive stack.
	 * 
	 * In most cases this is *not* tail recursion, so don't try and make a loop
	 * out of it.
	 */
	@SuppressWarnings("unchecked")
	private void processWhileCallsToDo() throws DataConversionException
	{
		int maxSize= 0;
		while (this.curRun.callsToDo.size() != 0)
		{
			maxSize= Math.max(maxSize, this.curRun.callsToDo.size());
			OneRecusiveCall thisTime= this.curRun.callsToDo.removeFirst();

			// Arguments to the recursive function.
			RegisterSet regHoldingObject= thisTime.regHoldingObject;
			RegisterSet regNotHoldingObject= thisTime.regNotHoldingObject;
			Element currentElement= thisTime.currentElement;
			CodePath codePath= thisTime.codePath;

			if (currentElement == null)
			{
				continue; // base case
			}

			InstructionActions actions= beenHereBefore(this.curRun.beenTo, regHoldingObject, regNotHoldingObject, currentElement, codePath);
			if (actions == null)
			{
				continue; // base case
			}
			InstructionUseInfo useInfo= actions.useInfo;

			Element nextInstruction;

			if (currentElement.getName().startsWith("goto"))
			{
				nextInstruction= labels.get(currentElement.getAttribute("target").getIntValue());
			}
			else
			{
				nextInstruction= nextElement.get(currentElement);
				if (nextInstruction == null && currentElement.getParentElement().getName().equals("try"))
				{
					// Exited the try, move to the element after the
					// try terminates.
					nextInstruction= nextElement.get(currentElement.getParentElement().getParentElement());
				}
			}

			// the ones we came in with
			RegisterSet ourObjUse= regHoldingObject.clone();
			// plus the ones that we write obj to
			ourObjUse.orEq(useInfo.writesObj());
			// minus the ones we write non obj into
			ourObjUse.andEqNot(useInfo.writesNonObj());

			RegisterSet ourNonObjUse= regNotHoldingObject.clone();
			ourNonObjUse.orEq(useInfo.writesNonObj());
			ourNonObjUse.andEqNot(useInfo.writesObj());

			if (currentElement.getName().startsWith("return"))
			{
				continue;
			}

			if (currentElement.getName().equals("try-catch"))
			{
				processRecAdd(ourObjUse, ourNonObjUse, (Element) currentElement.getChild("try", dex).getChildren().get(0), createNewCodePath(codePath));

				for (Element caught : (List<Element>) currentElement.getChildren("catch", dex))
				{
					Element nextInst= labels.get(caught.getAttribute("target").getIntValue());

					processRecAdd(ourObjUse, ourNonObjUse, nextInst, createNewCodePath(codePath));
				}

			}
			else if (currentElement.getName().equals("packed-switch") || currentElement.getName().equals("sparse-switch"))
			{
				processRecAdd(ourObjUse, ourNonObjUse, nextInstruction, createNewCodePath(codePath));

				for (Element target : (List<Element>) currentElement.getChildren("case", dex))
				{
					processRecAdd(ourObjUse, ourNonObjUse, labels.get(target.getAttribute("label").getIntValue()), createNewCodePath(codePath));
				}
			}
			else if (currentElement.getName().startsWith("if"))
			{
				processRecAdd(ourObjUse, ourNonObjUse, nextInstruction, createNewCodePath(codePath));
				processRecAdd(ourObjUse, ourNonObjUse, labels.get(currentElement.getAttribute("target").getIntValue()), createNewCodePath(codePath));
			}
			else if (currentElement.getName().equals("label"))
			{
				// It will be useful in the future to have labels treated as
				// creating a new code path.
				processRecAdd(ourObjUse, ourNonObjUse, nextInstruction, createNewCodePath(codePath));

			}
			else
			{
				// straight line code.
				processRecAdd(ourObjUse, ourNonObjUse, nextInstruction, codePath);
			}
		}
		refLog("Max recusrive depth " + maxSize);
	}

	/**
	 * This thing is used to determine whether or not our recursion keeps going
	 * It terminates the recursion if we have been to this instruction with the
	 * exact same state before. We return information collected about the
	 * current instruction to the caller.
	 */
	private static InstructionActions beenHereBefore(Map<Element, InstructionActions> beenTo, RegisterSet regHoldingObject, RegisterSet regNotHoldingObject, Element currentElement, CodePath c) throws DataConversionException
	{
		InstructionActions toRet;

		if (beenTo.containsKey(currentElement))
		{
			// Visited here on another code branch
			toRet= beenTo.get(currentElement);
		}
		else
		{
			// Haven't been here yet.
			toRet= new InstructionActions();
			toRet.useInfo= processElement(currentElement);
			beenTo.put(currentElement, toRet);

		}

		// Labels are not code, and thus do not belong in code paths.
		if (!currentElement.getName().equals("label"))
		{
			c.path.add(new OnePathInstructionRegisterContents(currentElement, regHoldingObject, regNotHoldingObject));
		}

		boolean enteredNotHolding= false;
		// figure out if we have been here before with the same
		// enteredNotHolding state
		for (RegisterSet m : toRet.enteredNot)
		{
			if (m.equals(regNotHoldingObject))
			{
				enteredNotHolding= true;
				break;
			}
		}

		boolean enteredHolding= false;
		// figure out if we have been here before with the same enteredHolding
		// state.
		for (RegisterSet m : toRet.enteredHoldingObj)
		{
			if (m.equals(regHoldingObject))
			{
				enteredHolding= true;
				break;
			}
		}

		if (enteredNotHolding && enteredHolding)
		{
			// We were here before with the exact same state: time to terminate
			// the search along this path.
			return null;
		}
		// Add info about the state we were in when we got to here along this
		// code path.
		if (!enteredHolding)
		{
			toRet.enteredHoldingObj.add(regHoldingObject);
		}
		if (!enteredNotHolding)
		{
			toRet.enteredNot.add(regNotHoldingObject);
		}
		return toRet;
	}

	/**
	 * This function creates a InstructionUseInfo based on the current element
	 * TODO: if anyone really cares this can be made faster by not using
	 * reflection.
	 */
	private static InstructionUseInfo processElement(Element element) throws DataConversionException
	{
		InstructionUseInfo use= new InstructionUseInfo(element);
		// If we find the instruction using the generic handler, return
		// immediately.
		if (InstructionProcessor.processGeneric(element, use))
		{
			return use;
		}
		else
		{
			// Otherwise, we need to hit the correct processor function:
			String todo= "process_" + element.getName().replace("-", "_");
			Method method;
			try
			{
				method= InstructionProcessor.class.getMethod(todo, Element.class, InstructionUseInfo.class);
				method.invoke(null, element, use);
			}
			catch (Exception ex)
			{
				throw new DataConversionException(ex.getMessage(), "When attempting to: " + todo);
			}
		}
		return use;
	}

	private static void refLog(String message)
	{
		// Log.debug("ref", message);
	}

}
