/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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

package org.xmlvm.util.analytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlvm.Log;
import org.xmlvm.util.analytics.data.Dependencies;
import org.xmlvm.util.analytics.data.TypeHierarchy;
import org.xmlvm.util.analytics.data.Util;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;
import org.xmlvm.util.universalfile.UniversalFileFilter;

import com.android.dx.cf.code.ConcreteMethod;
import com.android.dx.cf.code.Ropper;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.cf.iface.Method;
import com.android.dx.cf.iface.MethodList;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.dex.code.CatchHandlerList;
import com.android.dx.dex.code.CatchTable;
import com.android.dx.dex.code.CatchTable.Entry;
import com.android.dx.dex.code.CstInsn;
import com.android.dx.dex.code.DalvCode;
import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.DalvInsnList;
import com.android.dx.dex.code.Dop;
import com.android.dx.dex.code.Dops;
import com.android.dx.dex.code.HighRegisterPrefix;
import com.android.dx.dex.code.PositionList;
import com.android.dx.dex.code.RopTranslator;
import com.android.dx.dex.code.SimpleInsn;
import com.android.dx.rop.code.AccessFlags;
import com.android.dx.rop.code.DexTranslationAdvice;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.code.TranslationAdvice;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstBaseMethodRef;
import com.android.dx.rop.cst.CstMemberRef;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.type.Prototype;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.TypeList;

/**
 * The JDKAnalyzer is a swiss army knife for analyzing dependencies between
 * classes in a library. Given a black- and white-list of classes and packages, 
 * it analyzes the border betweteen these two groups of classes. All
 * dependencies between classes are mapped and can be written as a graph file,
 * for visual analysis.
 */
public class JDKAnalyzer
{
	public static final String TAG= JDKAnalyzer.class.getSimpleName();
	public static Map<String, Integer> badClassCount= new HashMap<String, Integer>();
	public static final String RESULTS_FILENAME= "results.bin";
	public static final String RESULTS2_FILENAME= "results2.bin";
	public static final String GRAPH_FILENAME= "fulldeps.gdf";
	final static String[] GOOD_PACKAGES= { "java.lang", "java.util", "java.math", "java.net", "java.io", "org.apache.harmony.luni.util" };

	/**
	 * For manual classification override: Classes that will always be
	 * classified as "good", regardless of their package.
	 */
	public final static Set<String> GOOD_CLASSES= new HashSet<String>();

	/**
	 * For manual classification override: Classes that will always be
	 * classified as "bad", regardless of their package.
	 */
	public final static Set<String> BAD_CLASSES= new HashSet<String>();

	public static void main(String[] args)
	{
		// The JDK JAR archive should be given as the argument.
		if (args.length != 1)
		{
			Log.error(TAG, "Invalid usage.");
			System.exit(-1);
		}
		fillGoodBadOverrides();
		String libraryPath= args[0];

		// Get a complete dependency map.
		Dependencies dependencies= loadDepencencies(libraryPath);

		writeDepsToGraphFile(dependencies);

		// Note(Sascha): The analysis showed, that in the JDK there are not a
		// lot of red/green mixed hierarchies. So for now, we skip this part.
		// It might become interesting for other libraries or for when the rules
		// change.

		// Compute the type hierarchy of the given set of classes.
		HierarchyAnalyzer hierarchyAnalyzer= new HierarchyAnalyzer(libraryPath);
		TypeHierarchy hierarchy= hierarchyAnalyzer.analyze();

		// First we determine the list of mock methods we need to insert into
		// green classes to prevent bubbling up of calls into red methods.
		Set<String> mockMethods= determineGreenMockMethods(dependencies, hierarchy);

		Pair<Set<String>, Set<String>> orangeLists= constructOrangeList(dependencies);
		Set<String> redList= constructRedList(dependencies, orangeLists.first);
		Set<String> greenList= constructGreenList(dependencies);
		writeSetToFile(orangeLists.first, "orange_classes.txt");
		writeSetToFile(orangeLists.second, "orange_details.txt");
		writeSetToFile(redList, "red.txt");
		writeSetToFile(greenList, "green.txt");

		// Find all good classes with bad references in them.
		// Log.debug(TAG, "Getting good classes with bad deps...");
		// Set<String> goodClassesWithBadDeps =
		// getGoodClassesWithBadReferences(dependencies);
		// Log.debug(TAG, "Found: " + goodClassesWithBadDeps.size());

		// Bad references that are referenced directly from good classes.
		// printDirectBadRefs();
	}

	private static void fillGoodBadOverrides()
	{
		// Problematic:
		// java.security.BasicPermission. Used by many different
		// classes and doing an import on all of java.security.
		// sun.misc.Unsafe: Very low-level functionality.

		GOOD_CLASSES.add("java.security.Permission");
		// Interfaces
		GOOD_CLASSES.add("java.security.Guard");

		// Super-class of a few good classes and not much functionality as it is
		// primarily abstract.
		GOOD_CLASSES.add("java.security.PermissionCollection");

		GOOD_CLASSES.add("java.security.BasicPermission");
		GOOD_CLASSES.add("java.security.PrivilegedAction");
		GOOD_CLASSES.add("java.security.PrivilegedActionException");
		GOOD_CLASSES.add("java.security.PrivilegedExceptionAction");

		// Interfaces from which good classes are created.
		// TODO(Sascha): Maybe do this automatically.
		GOOD_CLASSES.add("sun.reflect.LangReflectAccess");
		GOOD_CLASSES.add("sun.misc.JavaNetAccess");
		GOOD_CLASSES.add("sun.misc.JavaIOAccess");
		GOOD_CLASSES.add("org.xml.sax.EntityResolver");
		GOOD_CLASSES.add("sun.net.spi.nameservice.NameService");
		GOOD_CLASSES.add("org.xml.sax.ErrorHandler");
		GOOD_CLASSES.add("sun.misc.JavaIODeleteOnExitAccess");
		GOOD_CLASSES.add("sun.misc.SignalHandler");
		GOOD_CLASSES.add("sun.misc.JavaLangAccess");
		GOOD_CLASSES.add("sun.misc.JavaUtilJarAccess");
		GOOD_CLASSES.add("sun.misc.JavaIOFileDescriptorAccess");
		GOOD_CLASSES.add("sun.util.LocaleServiceProviderPool$LocalizedObjectGetter");
		GOOD_CLASSES.add("java.nio.channels.Channel");

		GOOD_CLASSES.add("org.apache.harmony.luni.internal.nls.Messages");
		GOOD_CLASSES.add("org.apache.harmony.math.internal.nls.Messages");
		GOOD_CLASSES.add("org.apache.harmony.regex.internal.nls.Messages");
		GOOD_CLASSES.add("org.apache.harmony.archive.internal.nls.Messages");

		BAD_CLASSES.add("java.util.jar.JarVerifier");
		BAD_CLASSES.add("java.lang.management.ManagementFactory");
		BAD_CLASSES.add("java.util.JapaneseImperialCalendar");
		BAD_CLASSES.add("java.lang.ClassLoader");
		BAD_CLASSES.add("java.net.URLClassLoader");
		BAD_CLASSES.add("java.net.URLClassLoader$SubURLClassLoader");
		BAD_CLASSES.add("java.net.FactoryURLClassLoader");

		// This one is a subclass of java.lang.ClassLoader, so it must go as
		// well.
		BAD_CLASSES.add("java.util.ResourceBundle$RBClassLoader");

		// Sub-class of bad class sun.misc.LRUCache.
		BAD_CLASSES.add("java.util.Scanner$1");

		// Sub-class of bad class org.apache.harmony.luni.util.ThreadLocalCache
		BAD_CLASSES.add("java.io.ObjectStreamClass$OSCThreadLocalCache");
		BAD_CLASSES.add("java.io.ObjectStreamClass$OSCThreadLocalCache$1");

		// DEX cannot parse this.
		BAD_CLASSES.add("org.apache.xerces.impl.xpath.regex.ParserForXMLSchema");
	}

	/**
	 * Constructs a set of classes that can be completely removed.
	 */
	private static Set<String> constructRedList(Dependencies dependencies, Set<String> orangeList)
	{
		Set<String> result= new HashSet<String>();
		for (String clazz : dependencies.keySet())
		{
			// Red classes are all bad classes that are not orange.
			if (!isGoodClass(clazz) && !orangeList.contains(clazz))
			{
				result.add(clazz);
			}

		}
		return result;
	}

	private static Set<String> constructGreenList(Dependencies dependencies)
	{
		Set<String> result= new HashSet<String>();
		for (String clazz : dependencies.keySet())
		{
			// Red classes are all bad classes that are not orange.
			if (isGoodClass(clazz))
			{
				result.add(clazz);
			}
		}
		return result;
	}

	/**
	 * Constructs a list of <class/member> pairs that need to be mocked with
	 * assertions, as these members will be accessed by the remaining good
	 * classes.
	 */
	private static Pair<Set<String>, Set<String>> constructOrangeList(Dependencies dependencies)
	{
		// These are just the orange class names. This can be used to remove
		// these classes from he red list.
		Set<String> orangeClasses= new HashSet<String>();

		// These are the details about the classes and members that need to be
		// mocked.
		Set<String> details= new HashSet<String>();

		for (String clazz : dependencies.keySet())
		{
			if (!isGoodClass(clazz))
			{
				continue;
			}

			Dependencies.ClassDeps classDeps= dependencies.getDepsForClass(clazz);
			for (String method : classDeps.methodSet())
			{
				Dependencies.MethodDeps methodDeps= classDeps.getMethodDeps(method);
				for (String dep : methodDeps.classSet())
				{
					// Only bad dependencies are relevant here.
					// TODO: Add super-type relationship mocks.
					if (isGoodClass(dep))
					{
						continue;
					}
					orangeClasses.add(dep);
					for (String methodDep : methodDeps.getMethods(dep))
					{
						// MethodDep can be empty for e.g. the SUPER
						// relationship, which we don't care about for the
						// mocks at this point.
						if (!methodDep.isEmpty())
						{
							details.add(dep + "::" + methodDep);
						}
					}
				}
			}
		}
		return new Pair<Set<String>, Set<String>>(orangeClasses, details);
	}

	private static void writeDepsToGraphFile(Dependencies dependencies)
	{
		Log.debug(TAG, "Writing GDF file ...");
		StringBuilder nodeDef= new StringBuilder();
		StringBuilder edgeDef= new StringBuilder();

		nodeDef.append("nodedef> name,color,style\n");
		edgeDef.append("edgedef> node1,node2,method VARCHAR(32)\n");

		Set<String> allNodes= new HashSet<String>();
		for (String clazz : dependencies.keySet())
		{
			// Only write out good classes.
			if (!isGoodClass(clazz))
			{
				continue;
			}
			boolean hasDepsToGraph= false;
			Dependencies.ClassDeps classDeps= dependencies.getDepsForClass(clazz);
			for (String method : classDeps.methodSet())
			{
				Dependencies.MethodDeps methodDeps= classDeps.getMethodDeps(method);
				for (String dep : methodDeps.classSet())
				{
					// To reduce complexity in the graph, only map bad deps.
					if (isGoodClass(dep))
					{
						continue;
					}
					for (String methodDep : methodDeps.getMethods(dep))
					{
						allNodes.add(dep);
						edgeDef.append(clazz);
						edgeDef.append(",");
						edgeDef.append(dep);
						edgeDef.append(",");
						edgeDef.append(method + "->" + methodDep);
						edgeDef.append("\n");
						hasDepsToGraph= true;
					}
				}
			}
			if (hasDepsToGraph)
			{
				allNodes.add(clazz);
			}
		}

		for (String node : allNodes)
		{
			nodeDef.append(node);
			if (isGoodClass(node))
			{
				nodeDef.append(",");
				nodeDef.append("blue");
				nodeDef.append(",");
				nodeDef.append("1");
			}
			else
			{
				nodeDef.append(",");
				nodeDef.append("red");
				nodeDef.append(",");
				nodeDef.append("2");
			}
			nodeDef.append("\n");
		}

		FileWriter fw;
		try
		{
			fw= new FileWriter(GRAPH_FILENAME);
			fw.write(nodeDef.toString());
			fw.write(edgeDef.toString());
			fw.close();
			Log.debug(TAG, "Writing GDF file done.");
		}
		catch (IOException e)
		{
			Log.error(TAG, "Writing GDF file failed: " + e.getMessage());
		}
	}

	private static void printDirectBadRefs()
	{
		List<String> clazzes= new ArrayList<String>(badClassCount.keySet());

		Collections.sort(clazzes, new Comparator<String>()
		{

			public int compare(String o1, String o2)
			{
				return badClassCount.get(o1) - badClassCount.get(o2);
			}
		});

		Log.debug(TAG, "Top direct bad refs:");
		for (String badDep : clazzes)
		{
			int count= badClassCount.get(badDep);
			if (count > 10)
			{
				Log.debug(TAG, count + " - " + badDep);
			}
		}
	}

	/**
	 * Returns a list of classes that contain bad dependencies.
	 */
	private static Set<String> getGoodClassesWithBadReferences(Dependencies data)
	{
		Set<String> result= new HashSet<String>();

		// We try to cache the result of this analysis on disk as well. If we
		// find this cache, we load it.
		File resultsFile= new File(RESULTS2_FILENAME);
		if (resultsFile.exists())
		{
			Log.debug(TAG, "Attempting to read second result from " + RESULTS2_FILENAME);
			try
			{
				ObjectInputStream ois= new ObjectInputStream(new FileInputStream(resultsFile));
				result= (Set<String>) ois.readObject();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			// If the cache could not be loaded, we perform the actual analysis
			// and store the result on disk for further calls.
			int goods= 0;
			for (String className : data.keySet())
			{
				if (isGoodClass(className))
				{
					goods++;
					if (hasBadDep(className, data))
					{
						result.add(className);
					}
					else
					{
						Log.debug(TAG, "A good class without bad rep!");
					}
				}
			}

			Log.debug(TAG, "Good classes: " + goods);
			try
			{
				ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(resultsFile));
				oos.writeObject(result);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return result;
	}

	private static boolean hasBadDep(String className, Dependencies data)
	{
		boolean result= false;
		for (String dep : data.getAllDepsForClass(className))
		{
			for (String goodDep : GOOD_PACKAGES)
			{
				if (!dep.startsWith(goodDep))
				{
					if (!badClassCount.containsKey(dep))
					{
						badClassCount.put(dep, 0);
					}
					badClassCount.put(dep, badClassCount.get(dep) + 1);
					result= true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns whether the given class name is a good class.
	 */
	private static boolean isGoodClass(String className)
	{
		// First check the overrides.
		for (String good : GOOD_CLASSES)
		{
			if (className.equals(good))
			{
				return true;
			}
		}
		for (String bad : BAD_CLASSES)
		{
			if (className.equals(bad))
			{
				return false;
			}
		}

		for (String goodDep : GOOD_PACKAGES)
		{
			if (className.startsWith(goodDep))
			{
				return true;
			}
		}
		return false;
	}

	private static Dependencies loadDepencencies(String jdkFileName)
	{
		Dependencies dependencies= new Dependencies();

		// This is a shortcut, in case we already have the analytical data about
		// the dependencies loaded and stored on the file system. If so, we use
		// this data.
		File resultsFile= new File(RESULTS_FILENAME);
		if (resultsFile.exists())
		{
			Log.debug(TAG, "Loading results from " + RESULTS_FILENAME + " ...");
			ObjectInputStream ois;
			try
			{
				ois= new ObjectInputStream(new FileInputStream(resultsFile));
				dependencies= (Dependencies) ois.readObject();
				ois.close();
				Log.debug(TAG, "Loaded results from file: " + dependencies.size());
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			// If there is no stored result, we nee to analyze the classes
			// first.
			// First the class files are loaded.
			UniversalFile library= UniversalFileCreator.createDirectory(null, jdkFileName);
			UniversalFile[] classes= library.listFilesRecursively(new UniversalFileFilter()
			{

				public boolean accept(UniversalFile file)
				{
					return file.getName().toLowerCase().endsWith(".class");
				}
			});
			Log.debug(TAG, "Analyzing " + classes.length + " classes.");
			final String basePath= library.getAbsolutePath();

			// We go through all the class files and get their dependencies.
			for (UniversalFile clazz : classes)
			{
				String fileName= clazz.getRelativePath(basePath).replace('\\', '.');
				Dependencies.ClassDeps classDeps= dependencies.getDepsForClass(fileName.substring(0, fileName.length() - 6).replace(File.separatorChar, '.'));
				getAllDependencies(clazz.getFileAsBytes(), fileName, classDeps);
			}

			// Store results on disk so we save time when executing this a
			// second time.
			try
			{
				Log.debug(TAG, "Writing result to file " + RESULTS_FILENAME);
				ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(RESULTS_FILENAME));
				oos.writeObject(dependencies);
				oos.close();
				Log.debug(TAG, "Done.");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return dependencies;
	}

	/**
	 * Adds all dependencies of the given class to classDeps.
	 */
	private static void getAllDependencies(byte[] bytes, String relativePath, Dependencies.ClassDeps classDeps)
	{

		Log.debug(TAG, relativePath);
		DirectClassFile classFile= new DirectClassFile(bytes, relativePath, false);
		classFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
		try
		{
			classFile.getMagic();
		}
		catch (ParseException ex)
		{
			Log.warn(TAG, "Put to red-list as it couldn't be parsed: " + relativePath);
			BAD_CLASSES.add(classDeps.getClassName());
			return;
		}

		String superClassName= "";
		// This can happen for java.lang.Object.
		if (classFile.getSuperclass() != null)
		{
			superClassName= Util.parseClassName(classFile.getSuperclass().getClassType().getClassName()).toString();
		}

		// Super Class
		if (!superClassName.isEmpty())
		{
			Set<String> superClass= new HashSet<String>();
			superClass.add(superClassName.replace('/', '.'));
			classDeps.getMethodDeps("SUPER").addDependency(superClassName.replace('/', '.'), "");
		}

		// Interfaces
		TypeList interfaces= classFile.getInterfaces();
		if (interfaces.size() > 0)
		{
			Set<String> interfaceList= new HashSet<String>();
			for (int i= 0; i < interfaces.size(); ++i)
			{
				interfaceList.add(Util.parseClassName(interfaces.getType(i).getClassName()).toString());
				classDeps.getMethodDeps("INTERFACES").addDependency(Util.parseClassName(interfaces.getType(i).getClassName()).toString(), "");
			}
		}

		// Methods
		MethodList methods= classFile.getMethods();
		for (int i= 0; i < methods.size(); i++)
		{
			Method method= methods.get(i);
			// CstMethodRef methodRef = new
			// CstMethodRef(method.getDefiningClass(), method.getNat());
			// We shouldn't need to go through the signature. If the class is
			// not used in the code block, we can ignore it.
			// processSignature(methodRef, dependencies);
			processCode(getCode(method, classFile), classDeps.getMethodDeps(method.getName().toHuman()));
		}
	}

	/**
	 * Extracts all types from the method signature.
	 */
	private static void processSignature(CstMethodRef methodRef, Set<String> dependencies)
	{
		Prototype prototype= methodRef.getPrototype();

		// Parameter types.
		StdTypeList parameters= prototype.getParameterTypes();
		for (int i= 0; i < parameters.size(); ++i)
		{
			String parameterType= parameters.get(i).toHuman();
			dependencies.add(parameterType);
		}

		// Return type.
		dependencies.add(prototype.getReturnType().getType().toHuman());
	}

	private static void processCode(DalvCode code, Dependencies.MethodDeps methodDeps)
	{
		if (code == null)
		{
			return;
		}
		// TODO: We don't need this, as it should be added by code blocks, if
		// the exception is actually used. If it's not used, we shouldn't care.
		// processCatchTable(code.getCatches(), dependencies);

		DalvInsnList instructions= code.getInsns();
		for (int i= 0; i < instructions.size(); ++i)
		{
			processInstruction(instructions.get(i), methodDeps);
		}
	}

	private static void processInstruction(DalvInsn instruction, Dependencies.MethodDeps methodDeps)
	{
		String opname= instruction.getOpcode().getName();
		if (opname.equals("instance-of") || opname.equals("const-class"))
		{
			CstInsn isaInsn= (CstInsn) instruction;
			// TODO: Do we need this?
			// dependencies.add(isaInsn.getConstant().toHuman());
		}

		// TODO: Do we need to add these?
		// RegisterSpecList registers = instruction.getRegisters();
		// for (int i = 0; i < registers.size(); ++i) {
		// RegisterSpec register = registers.get(i);
		// String descriptor = register.getType().getDescriptor();
		// String registerType = register.getType().toHuman();
		// // Sometimes a register type name starts with some info about the
		// // register. We need to cut this out.
		// if (descriptor.startsWith("N")) {
		// registerType = registerType.substring(registerType.indexOf('L') + 1);
		// }
		// methodDeps.addDependency(registerType, "TODO");
		// }

		if (instruction instanceof CstInsn)
		{
			CstInsn cstInsn= (CstInsn) instruction;
			if (isInvokeInstruction(cstInsn))
			{
				// Adds the class that a method references
			    	CstBaseMethodRef methodRef= (CstBaseMethodRef) cstInsn.getConstant();
				methodDeps.addDependency(methodRef.getDefiningClass().toHuman(), methodRef.getNat().getName().toHuman() + ":" + methodRef.getPrototype().toString());
			}
			else
			{
				Constant constant= cstInsn.getConstant();
				if (constant instanceof CstMemberRef)
				{
					CstMemberRef memberRef= (CstMemberRef) constant;

					methodDeps.addDependency(memberRef.getDefiningClass().getClassType().toHuman(), memberRef.getNat().getName().toHuman());

					// dependencies.add(memberRef.getNat().getFieldType().getType().toHuman());
				}
			}
		}
		else if (instruction instanceof HighRegisterPrefix)
		{
			HighRegisterPrefix highRegisterPrefix= (HighRegisterPrefix) instruction;
			SimpleInsn[] moveInstructions= highRegisterPrefix.getMoveInstructions();
			for (SimpleInsn moveInstruction : moveInstructions)
			{
				// Recurse.
				processInstruction(moveInstruction, methodDeps);
			}
		}
	}

	/**
	 * Adds all exception types.
	 */
	private static void processCatchTable(CatchTable catchTable, Map<String, Set<String>> dependencies)
	{
		if (catchTable.size() == 0)
		{
			return;
		}
		for (int i= 0; i < catchTable.size(); ++i)
		{
			Entry entry= catchTable.get(i);
			CatchHandlerList catchHandlers= entry.getHandlers();
			for (int j= 0; j < catchHandlers.size(); ++j)
			{
				// TODO: Do we need these? Shouldn't they be handled by code, if
				// accessed?
				// dependencies.add(catchHandlers.get(j).getExceptionType().toHuman());
			}
		}
	}

	/**
	 * Returns whether the given instruction is an invoke instruction that can
	 * be handled by {@link #processInvokeInstruction(CstInsn)}.
	 */
	private static boolean isInvokeInstruction(CstInsn cstInsn)
	{
		final Dop[] invokeInstructions= { Dops.INVOKE_VIRTUAL, Dops.INVOKE_VIRTUAL_RANGE, Dops.INVOKE_STATIC, Dops.INVOKE_STATIC_RANGE, Dops.INVOKE_DIRECT, Dops.INVOKE_DIRECT_RANGE, Dops.INVOKE_INTERFACE, Dops.INVOKE_INTERFACE_RANGE, Dops.INVOKE_SUPER, Dops.INVOKE_SUPER_RANGE };
		for (Dop dop : invokeInstructions)
		{
			if (dop.equals(cstInsn.getOpcode()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Extracts the code block from the given method of the given class, or
	 * <code>null</code>, if method is native or abstract.
	 */
	private static DalvCode getCode(Method method, DirectClassFile classFile)
	{
		boolean isNative= AccessFlags.isNative(method.getAccessFlags());
		boolean isStatic= AccessFlags.isStatic(method.getAccessFlags());
		boolean isAbstract= AccessFlags.isAbstract(method.getAccessFlags());

		if (isNative || isAbstract)
		{
			return null;
		}

		ConcreteMethod concrete= new ConcreteMethod(method, classFile, false, false);
		TranslationAdvice advice= DexTranslationAdvice.THE_ONE;
		RopMethod rmeth= Ropper.convert(concrete, advice);
		CstMethodRef meth= new CstMethodRef(method.getDefiningClass(), method.getNat());
		int paramSize= meth.getParameterWordCount(isStatic);
		DalvCode code= RopTranslator.translate(rmeth, PositionList.NONE, null, paramSize);
		DalvCode.AssignIndicesCallback callback= new DalvCode.AssignIndicesCallback()
		{
			public int getIndex(Constant cst)
			{
				// Everything is at index 0!
				return 0;
			}
		};
		code.assignIndices(callback);
		return code;
	}

	/**
	 * Writes a set to a file. Each item will be on a single line in the
	 * resulting file.
	 */
	private static void writeSetToFile(Set<String> set, String fileName)
	{
		try
		{
			FileWriter writer= new FileWriter(fileName);
			for (String item : set)
			{
				writer.write(item);
				writer.write('\n');
			}
			writer.close();
			Log.debug(TAG, "File written: " + fileName);
		}
		catch (IOException e)
		{
			Log.error(TAG, "Error while writing set to file: " + e.getMessage());
		}
	}

	/**
	 * This method returns a list of methods (in green classes) that need to be
	 * added and mocked failing implementations.
	 * <p>
	 * Details: Imagine a green subclass of a red class which doesn't override
	 * some protected or public methods/members of the red class. If such a
	 * member is accessed on the subclass, we would fail to detect that this is
	 * a bad call, that propagates up to the red class. With this list we detect
	 * this situation and don't let the call bubble up. Instead an assertion
	 * will fired in the mock implementation of the method in the sub-class.
	 * This way we don't need to remove the whole green class.
	 */
	private static Set<String> determineGreenMockMethods(Dependencies dependencies, TypeHierarchy hierarchy)
	{
		Set<String> result= new HashSet<String>();
		for (String clazz : dependencies.keySet())
		{
			// Once we find a bad class, we go through the sub-classes and try
			// to find good ones.
			if (!isGoodClass(clazz))
			{
				Set<String> subClasses= hierarchy.getDirectSubTypes(clazz);
				if (subClasses != null)
				{
					for (String subClass : subClasses)
					{
						if (isGoodClass(subClass))
						{
							System.out.println(clazz + " -> " + subClass);
							// TODO: Need to get list of private and protected
							// members of good and bad classes.
						}
					}
				}
			}
		}
		return result;
	}
}
