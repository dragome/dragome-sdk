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

import java.util.List;
import java.util.regex.Pattern;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Attribute;

/**
 * The sole job of this class is to fill in a InstructionUseInfo after being fed
 * a particular instruction. This includes determining how the instruction uses
 * objects. See InstructionUseInfo for more information.
 * 
 * In general, anything in this class must always set InstructionUseInfo.isWrite
 * correctly. Forgetting to set it will cause bugs later on.
 */
public class InstructionProcessor
{
	/**
	 * Causes an increment for the reference count of the object in "reg"
	 * Example: <vm:reg-retain reg="1" />
	 */
	public final static String cmd_release= "reg-release";
	/**
	 * Causes a decrement for the reference count of the object in "reg"
	 * Example: <vm:reg-retain reg="0" />
	 */
	public final static String cmd_retain= "reg-retain";
	/**
	 * Causes a decrement for the reference count of an instance pointer in a
	 * class. The supplied attributes are identical to that of any other
	 * instance referencing instruction. Example: <vm:i-release kind="field"
	 * class-type="android.app.Activity" member-type="android.app.Activity"
	 * member-name="parent" vx="1" vx-type="android.app.Activity" vy="0"
	 * vy-type="android.app.Activity" />
	 */
	public final static String cmd_i_release= "i-release";
	/**
	 * Causes a decrement for the reference count of an static pointer in a
	 * class. The supplied attributes are identical to that of any other static
	 * referencing instruction. Example: <vm:s-release kind="field"
	 * class-type="android.content.Context"
	 * member-type="android.os.PowerManager" member-name="powerManager" vx="0"
	 * vx-type="null" />
	 */
	public final static String cmd_s_release= "s-release";
	/**
	 * Causes a decrement for the reference count of an object in an array. The
	 * supplied attributes are that which are sufficient to access the array in
	 * the xsl transform. Example: <vm:a-release vx="4"
	 * vx-type="android.content.DialogInterface$OnClickListener" vy="0"
	 * vy-type="android.content.DialogInterface$OnClickListener[]" vz="1"
	 * vz-type="int" />
	 */
	public final static String cmd_a_release= "a-release";
	/**
	 * Sets the temp register equal to the value of another register. Example:
	 * <vm:tmp-equals-r reg="0" />
	 */
	public final static String cmd_tmp_equals_r= "tmp-equals-r";
	/**
	 * Define a register, give it a name. Three types allowed: 'register' stores
	 * a variable, 'temp' a holder register that is used by XMLVM (rather than
	 * dex) to store things. 'exception' stores a reference to an exception
	 */
	public final static String cmd_define_register= "define-register";

	/**
	 * @see cmd_define_register
	 */
	public final static String cmd_define_register_attr_register= "register";
	/**
	 * @see cmd_define_register
	 */
	public final static String cmd_define_register_attr_temp= "temp";

	/**
	 * @see cmd_define_register
	 */
	public final static String cmd_define_register_attr_exception= "exception";
	/**
	 * Set a register to null
	 */
	public final static String cmd_set_null= "set-null";
	/**
	 * init locals from arguments
	 */
	public final static String cmd_move_argument= "move-argument";
	/**
	 * Insert a comment in the generated code
	 */
	public final static String cmd_comment= "comment";

	public final static Namespace dex= Namespace.getNamespace("dex", "http://xmlvm.org/dex");
	public final static Namespace vm= Namespace.getNamespace("vm", "http://xmlvm.org");

	/*
	 * Most of the time, the destination register in dex is VX, this is a helper
	 * function to get the register index for VX.
	 */
	static RegisterSet getDestReg(Element x) throws DataConversionException
	{
		return getDestReg(x, "vx");
	}

	/*
	 * Get the register index for an instruction given the name of the element
	 * that specifies the register.
	 */
	static RegisterSet getDestReg(Element x, String name) throws DataConversionException
	{
		return RegisterSet.from(x.getAttribute(name).getIntValue());
	}

	/*
	 * The following patterns help us identify and act upon instructions in DEX
	 * that are similar.
	 */
	static String nonObjTypesString= "(wide|boolean|byte|char|short|int|double|float|long)";

	static Pattern match3Op= Pattern.compile("^" + "(add|sub|mul|div|rem|and|or|xor|shl|shr|ushr)-" + nonObjTypesString + ".*");
	static Pattern match2Op= Pattern.compile("^" + "(add|sub|mul|div|rem|and|or|xor|shl|shr|ushr)-" + nonObjTypesString + "-2addr" + ".*");
	static Pattern matchOneOp= Pattern.compile("^" + "(add|rsub|mul|div|rem|and|or|xor)-" + nonObjTypesString + "-lit(8|16)" + ".*");
	static Pattern matchIf1= Pattern.compile("^" + "(if)-(eq|ne|lt|ge|gt|le)z" + ".*");
	static Pattern matchIf2= Pattern.compile("^" + "(if)-(eq|ne|lt|ge|gt|le)" + ".*");
	static Pattern iput= Pattern.compile("^" + "(iput|sput)-" + nonObjTypesString + ".*");
	static Pattern iget= Pattern.compile("^" + "(iget|sget)-" + nonObjTypesString + ".*");
	static Pattern invoke= Pattern.compile("^" + "invoke-" + ".*");
	static Pattern constDef= Pattern.compile("^" + "const" + ".*");
	static Pattern aputNonObj= Pattern.compile("^" + "aput-" + nonObjTypesString + ".*");
	static Pattern agetNonObj= Pattern.compile("^" + "aget-" + nonObjTypesString + ".*");
	static Pattern conversionNegNot= Pattern.compile("^" + "(neg|not)-" + nonObjTypesString + ".*");
	static Pattern conversionfromTo= Pattern.compile("^" + nonObjTypesString + "-to-" + nonObjTypesString + ".*");
	static Pattern compInstr= Pattern.compile("^" + "cmp" + ".*");

	public static Pattern nonObjTypes= Pattern.compile("^" + nonObjTypesString);

	/**
	 * We try and match the instruction to an action given our list of regex.
	 * Returns whether we were able to find a match and fill in the i. If it
	 * returns false, the caller must look for a more specific handler.
	 */
	static boolean processGeneric(Element element, InstructionUseInfo i) throws DataConversionException
	{
		String elemName= element.getName();
		if (matchOneOp.matcher(elemName).matches())
		{
			i.isWrite= true;
		}
		else if (match2Op.matcher(elemName).matches())
		{
			i.isWrite= true;
		}
		else if (match3Op.matcher(elemName).matches())
		{
			i.isWrite= true;
		}
		else if (matchIf1.matcher(elemName).matches())
		{
			i.isWrite= false;
		}
		else if (matchIf2.matcher(elemName).matches())
		{
			i.isWrite= false;
		}
		else if (iput.matcher(elemName).matches())
		{
			i.isWrite= false;
		}
		else if (iget.matcher(elemName).matches())
		{
			i.isWrite= true;
		}
		else if (invoke.matcher(elemName).matches())
		{
			getUsedFromParams(element, i);
			getMoveResult(element, i);
		}
		else if (constDef.matcher(elemName).matches())
		{
			if (elemName.equals("const-class"))
			{
			}
			// TODO: verify const-class is OK.
			i.isWrite= true;
		}
		else if (aputNonObj.matcher(elemName).matches())
		{
			i.isWrite= false;
		}
		else if (agetNonObj.matcher(elemName).matches())
		{
			i.isWrite= true;
		}
		else if (conversionNegNot.matcher(elemName).matches() || conversionfromTo.matcher(elemName).matches())
		{
			i.isWrite= true;
		}
		else if (compInstr.matcher(elemName).matches())
		{
			i.isWrite= true;
		}
		else
		{
			return false;
		}
		return true;

	}

	/*
	 * Helper for processing function call parameters.
	 */
	@SuppressWarnings("unchecked")
	static void getUsedFromParams(Element funcCallElement, InstructionUseInfo i) throws DataConversionException
	{

		Element pHolder= funcCallElement.getChild("parameters", dex);

		List<Element> params= (List<Element>) pHolder.getChildren("parameter", dex);
		for (Element parameter : params)
		{
			i.checkUsage(parameter.getAttribute("register"), parameter.getAttribute("type"));
		}

	}

	/*
	 * Helper for processing function call returns.
	 */
	static void getMoveResult(Element funcCallElement, InstructionUseInfo i) throws DataConversionException
	{
		String returnType= funcCallElement.getChild("parameters", dex).getChild("return", dex).getAttribute("type").getValue();
		boolean returnsVoid= returnType.equals("void");

		Element pHolder= funcCallElement.getChild("move-result", dex);
		if (pHolder != null)
		{
			i.checkUsage(pHolder.getAttribute("vx"), pHolder.getAttribute("vx-type"));
			i.isWrite= true;
		}
		else
		{
			if (!returnsVoid && !nonObjTypes.matcher(returnType).matches())
			{
				// if the result is an object and it is not used, we must
				// free rTmp. Recall that xmlvm2objc.xsl sticks unused results
				// into rTmp.
				i.freeTmpAfter= true;

			}
			i.isWrite= false;
		}
	}

	/*
	 * The following functions are handers for specific instructions. They are
	 * named to correspond exactly with the instruction name so that reflection
	 * can be used to call into them.
	 * 
	 * In general none of them are going to make any sense what so ever until
	 * you look at them along side the XML they process and what can be filled
	 * out in InstructionUseInfo. Hence the lack of specific comments.
	 * 
	 * Key to note is that they all should fill out i.isWrite before returning
	 * 
	 * They may also fill out information on retains/releases, or temp register
	 * usage.
	 */
	static public void process_return(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_return_void(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_new_instance(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;
	}

	static public void process_move_object_from16(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain.orEq(i.writesObj());

	}

	static public void process_move_exception(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		// Exceptions don't require retains as they use the retain count the
		// exception had when it was thrown
	}

	static public void process_move_object(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain.orEq(i.writesObj());

	}

	static public void process_move_wide(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;

	}

	static public void process_move_wide_from16(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;

	}

	static public void process_move_from16(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;

	}

	static public void process_move(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;
	}

	static public void process_return_object(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_var(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.possibleWrites.clear();
		i.possibleWrites.add("register");
		i.requiresRetain= i.writesObj();
	}

	static public void process_source_position(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_label(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_goto(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_goto_32(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_goto_16(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_aput(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= false;
		i.requiresRetain.orEq(getDestReg(element, "vx").and(i.usesAsObj()));
	}

	static public void process_aget(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain.orEq(i.writesObj());

	}

	@SuppressWarnings("unchecked")
	static public void process_aput_object(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.requiresRetain.orEq(getDestReg(element, "vx").and(i.usesAsObj()));
		if (!i.requiresRetain.isEmpty())
		{

			Element toAdd= new Element(cmd_a_release, vm);
			for (Attribute a : (List<Attribute>) i.Instruction.getAttributes())
			{
				toAdd.setAttribute(a.getName(), a.getValue(), a.getNamespace());
			}

			i.putRelease= toAdd;
		}
		i.isWrite= false;
	}

	static public void process_aget_object(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain.orEq(i.writesObj());

	}

	@SuppressWarnings("unchecked")
	static public void process_iput_object(Element element, InstructionUseInfo i) throws DataConversionException
	{
		// need to release before we overwrite

		i.requiresRetain.orEq(getDestReg(element, "vx").and(i.usesAsObj()));
		if (!i.requiresRetain.isEmpty())
		{

			Element toAdd= new Element(cmd_i_release, vm);
			for (Attribute a : (List<Attribute>) i.Instruction.getAttributes())
			{
				toAdd.setAttribute(a.getName(), a.getValue(), a.getNamespace());
			}

			i.putRelease= toAdd;
		}
		i.isWrite= false;
	}

	static public void process_iget_object(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain.orEq(i.writesObj());

	}

	@SuppressWarnings("unchecked")
	static public void process_sput_object(Element element, InstructionUseInfo i) throws DataConversionException
	{

		i.requiresRetain.orEq(getDestReg(element, "vx").and(i.usesAsObj()));
		if (!i.requiresRetain.isEmpty())
		{
			// need to release before we overwrite
			Element toAdd= new Element(cmd_s_release, vm);
			for (Attribute a : (List<Attribute>) i.Instruction.getAttributes())
			{
				toAdd.setAttribute(a.getName(), a.getValue(), a.getNamespace());
			}

			i.putRelease= toAdd;
		}
		i.isWrite= false;
	}

	static public void process_sget_object(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain.orEq(i.writesObj());
	}

	static public void process_try_catch(Element element, InstructionUseInfo i)
	{
		// TODO: what do we do about catches
		i.isWrite= false;
	}

	static public void process_catches(Element element, InstructionUseInfo i)
	{
		// TODO: what do we do about catches
		i.isWrite= false;
	}

	static public void process_monitor_enter(Element element, InstructionUseInfo i)
	{
		// TODO: what do we do about catches
		i.isWrite= false;
	}

	static public void process_monitor_exit(Element element, InstructionUseInfo i)
	{
		// TODO: what do we do about catches
		i.isWrite= false;
	}

	static public void process_sget(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain= i.writesObj();
	}

	@SuppressWarnings("unchecked")
	static public void process_sput(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.requiresRetain.orEq(getDestReg(element, "vx").and(i.usesAsObj()));
		if (!i.requiresRetain.isEmpty())
		{
			// need to release before we overwrite
			Element toAdd= new Element(cmd_s_release, vm);
			for (Attribute a : (List<Attribute>) i.Instruction.getAttributes())
			{
				toAdd.setAttribute(a.getName(), a.getValue(), a.getNamespace());
			}
			i.putRelease= toAdd;
		}
		i.isWrite= false;
	}

	static public void process_iget(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.isWrite= true;
		i.requiresRetain= i.writesObj();
	}

	@SuppressWarnings("unchecked")
	static public void process_iput(Element element, InstructionUseInfo i) throws DataConversionException
	{
		i.requiresRetain.orEq(getDestReg(element, "vx").and(i.usesAsObj()));

		if (!i.requiresRetain.isEmpty())
		{
			// need to release before we overwrite
			Element toAdd= new Element(cmd_i_release, vm);
			for (Attribute a : (List<Attribute>) i.Instruction.getAttributes())
			{
				toAdd.setAttribute(a.getName(), a.getValue(), a.getNamespace());
			}
			i.putRelease= toAdd;
		}
		i.isWrite= false;
	}

	static public void process_new_array(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;
	}

	static public void process_sparse_switch(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_check_cast(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_packed_switch(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_array_length(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;
	}

	static public void process_return_wide(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_throw(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	static public void process_instance_of(Element element, InstructionUseInfo i)
	{
		i.isWrite= true;
	}

	static public void process_fill_array_data(Element element, InstructionUseInfo i)
	{
		i.isWrite= false;
	}

	@SuppressWarnings("unchecked")
	static public void process_filled_new_array(Element element, InstructionUseInfo i) throws DataConversionException
	{
		Element moveResult= element.getChild("move-result", dex);

		for (Element valElement : (List<Element>) element.getChildren("value", dex))
		{
			i.checkUsage(valElement.getAttribute("register"), valElement.getAttribute("type"));
		}

		i.checkUsage(moveResult.getAttribute("vx"), moveResult.getAttribute("vx-type"));

		i.isWrite= true;
	}

	static public void process_filled_new_array_range(Element element, InstructionUseInfo i) throws DataConversionException
	{
		process_filled_new_array(element, i);
	}

	static public void process_nop(Element element, InstructionUseInfo i)
	{
	}
}
