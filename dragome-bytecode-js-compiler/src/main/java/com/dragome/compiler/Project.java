/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

// Copyright 2011 The j2js Authors. All Rights Reserved.
//
// This file is part of j2js.
//
// j2js is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// j2js is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with j2js. If not, see <http://www.gnu.org/licenses/>.

package com.dragome.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;
import com.dragome.compiler.ast.ArrayCreation;
import com.dragome.compiler.ast.FieldAccess;
import com.dragome.compiler.ast.MethodBinding;
import com.dragome.compiler.ast.MethodDeclaration;
import com.dragome.compiler.ast.MethodInvocation;
import com.dragome.compiler.ast.TypeDeclaration;
import com.dragome.compiler.parser.Pass1;
import com.dragome.compiler.type.Signature;
import com.dragome.compiler.type.TypeResolver;
import com.dragome.compiler.type.TypeVisitor;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.units.ConstructorUnit;
import com.dragome.compiler.units.FieldUnit;
import com.dragome.compiler.units.MemberUnit;
import com.dragome.compiler.units.MethodUnit;
import com.dragome.compiler.units.ProcedureUnit;
import com.dragome.compiler.utils.Log;
import com.dragome.compiler.utils.Utils;

public class Project implements Serializable
{

	static final long serialVersionUID= 0;

	public static Project singleton;

	private Map<String, ClassUnit> classesByName;

	private ClassUnit javaLangObject;

	private boolean compressed;

	private boolean generateLineNumbers;

	private Map<String, Signature> signatures;

	private transient Stack<Integer> ids;

	private transient int currentId;

	private transient int currentIndex;

	public transient int currentGeneratedMethods;

	private List<String> clinits= new ArrayList<String>();

	private List<String> genericSignatures= new ArrayList<String>();

	transient private int badMethods= 0;

	public List<String> writtenSignatures= new ArrayList<String>();

	private Set<String> typeDeclarationsWithAnnotations= new HashSet<String>();

	transient private ClasspathFileFilter classpathFilter;

	private boolean stopOnMissingClass;

	public ClasspathFileFilter getClasspathFilter()
	{
		return classpathFilter;
	}

	public List<String> getWrittenSignatures()
	{
		if (writtenSignatures == null)
			writtenSignatures= new ArrayList<String>();

		return writtenSignatures;
	}

	public void writeClinits(Writer writer) throws IOException
	{
		//	for (String clinit : clinits)
		//	{
		//	    writer.write("new " + clinit.substring(0, clinit.indexOf(".")) + "();\n");
		//	}
		writer.write("new java_lang_String();\n");

		for (String clinit : clinits)
		{
			if (clinit.contains("java_lang_Class._clinit_"))
				writer.write(clinit + "\n");
		}

		for (String clinit : clinits)
		{
			writer.write(clinit + "\n");
		}
	}

	public List<String> getClinits()
	{
		return clinits;
	}

	public void setClinits(List<String> clinits)
	{
		this.clinits= clinits;
	}

	public static Project getSingleton()
	{
		if (singleton == null)
			throw new NullPointerException();
		return singleton;
	}

	public static Project createSingleton(File cacheFile)
	{

		if (cacheFile != null && cacheFile.exists())
		{
			Log.getLogger().debug("Using cache " + cacheFile);
			try
			{
				read(cacheFile);
			}
			catch (Exception e)
			{
				Log.getLogger().warn("Could not read cache:\n" + e.getMessage());
			}
		}

		if (singleton == null || singleton.compressed != DragomeJsCompiler.compiler.isCompression() || singleton.generateLineNumbers != DragomeJsCompiler.compiler.isGenerateLineNumbers())
		{
			singleton= new Project();
			singleton.clear();
		}

		return singleton;
	}

	private static void read(File file) throws Exception
	{
		FileInputStream fis= new FileInputStream(file);
		ObjectInputStream ois= new ObjectInputStream(fis);
		singleton= (Project) ois.readObject();
		ois.close();
	}

	public static void write() throws IOException
	{
		File file= DragomeJsCompiler.compiler.getCacheFile();
		if (file.exists() && !file.canWrite())
		{
			throw new IOException("Cannot write " + file);
		}
		FileOutputStream fos= new FileOutputStream(file);
		ObjectOutputStream oos= new ObjectOutputStream(fos);
		oos.writeObject(singleton);
		oos.close();
	}

	public Signature getArraySignature(Type type)
	{
		String signatureString= type.getSignature();

		if (!signatureString.startsWith("L") || !signatureString.endsWith(";"))
		{
			throw new RuntimeException("Not a class signature: " + signatureString);
		}
		signatureString= signatureString.substring(1, signatureString.length() - 1);
		return getSignature(signatureString);
	}

	public Signature getSignature(String signatureString)
	{
		if (signatureString.endsWith(";"))
		{

		}
		signatureString= signatureString.replaceAll("/", ".");

		Signature signature= signatures.get(signatureString);
		if (signature == null)
		{
			signature= new Signature(signatureString, getUniqueId());
			signatures.put(signatureString, signature);
		}

		return signature;
	}

	public Signature getSignature(String className, String relativeSignature)
	{
		return getSignature(className + '#' + relativeSignature);
	}

	public Signature getSignature(FieldAccess fa)
	{
		return getSignature(fa.getType().getClassName(), fa.getName());
	}

	private int getUniqueId()
	{
		if (ids == null)
		{
			ids= new Stack<Integer>();
			for (Signature signature : signatures.values())
			{
				ids.add(signature.getId());
			}
			Collections.sort(ids);
		}

		while (currentIndex < ids.size() && ids.get(currentIndex) == currentId)
		{
			currentId+= 1;
			currentIndex+= 1;
		}

		currentId++;
		return currentId - 1;
	}

	private void clear()
	{
		classesByName= new LinkedHashMap<String, ClassUnit>();
		javaLangObject= null;

		signatures= new LinkedHashMap<String, Signature>();
		ids= null;
		currentId= 0;
		currentIndex= 0;
		compressed= DragomeJsCompiler.compiler.isCompression();
		generateLineNumbers= DragomeJsCompiler.compiler.isGenerateLineNumbers();
		badMethods= 0;
		writtenSignatures= new ArrayList<String>();
	}

	public void remove(ClassUnit clazz)
	{
		classesByName.remove(clazz);
	}

	public void visitSuperTypes(ClassUnit clazz, TypeVisitor visitor)
	{
		visitor.visit(clazz);
		ClassUnit superClass= clazz.getSuperUnit();
		if (superClass != null)
		{
			visitSuperTypes(superClass, visitor);
		}

		for (ClassUnit interfaceUnit : clazz.getInterfaces())
		{
			visitor.visit(interfaceUnit);
			visitSuperTypes(interfaceUnit, visitor);
		}
	}

	public ClassUnit getJavaLangObject()
	{
		return javaLangObject;
	}

	public ClassUnit getClassUnit(String className)
	{
		if (className.startsWith("["))
		{
			String replace= className.replace("[", "");
			className= replace.substring(1, replace.length() - 1);
		}
		ClassUnit clazz= classesByName.get(className);
		if (clazz != null)
			return clazz;

		if (stopOnMissingClass)
			throw new RuntimeException("Class is missing: " + className);

		return new NullClassUnit(className);
		//		throw new RuntimeException("No such unit: " + className);
	}

	public ClassUnit getClassUnit(ReferenceType type)
	{
		String signature;
		if (type instanceof ArrayType)
		{
			ArrayType aType= (ArrayType) type;
			signature= Utils.getSignature(aType.getBasicType());
			for (int i= 0; i < aType.getDimensions(); i++)
			{
				signature+= "[]";
			}
		}
		else
		{
			signature= Utils.getSignature(type);
		}

		return getClassUnit(signature);
	}

	public ClassUnit createClassUnit(String className, ClasspathFile classpathFile)
	{
		ClassUnit classUnit= classesByName.get(className);
		if (classUnit != null)
			return classUnit;

		Signature signature= Project.singleton.getSignature(className);
		classUnit= new ClassUnit(this, signature);
		classesByName.put(className, classUnit);

		if (className.equals("java.lang.Object"))
		{
			javaLangObject= classUnit;
		}

		classUnit.setClassFile(classpathFile);

		return classUnit;
	}

	private MemberUnit getMemberUnitOrNull(String className, Signature signature)
	{
		ClassUnit classUnit= getClassUnit(className);
		if (classUnit == null)
			return null;
		return classUnit.getDeclaredMember(signature.toString());
	}

	private MemberUnit getMemberUnit(String className, Signature signature)
	{
		MemberUnit unit= getMemberUnitOrNull(className, signature);
		if (unit == null)
		{
			throw new RuntimeException("No such unit: " + className + "#" + signature);
		}

		return unit;
	}

	public ProcedureUnit getProcedureUnit(MethodBinding methodBinding)
	{
		Signature signature= Project.singleton.getSignature(methodBinding.getRelativeSignature());
		String className= methodBinding.getDeclaringClass().getClassName();
		return (ProcedureUnit) getMemberUnit(className, signature);
	}

	public ProcedureUnit getOrCreateProcedureUnit(MethodBinding methodBinding)
	{
		Signature signature= Project.singleton.getSignature(methodBinding.getRelativeSignature());
		String className= methodBinding.getDeclaringClass().getClassName();
		return (ProcedureUnit) getOrCreateMemberUnit(className, signature, Pass1.extractMethodNameSignature(methodBinding));
	}

	private MemberUnit getOrCreateMemberUnit(String className, Signature signature, String nameAndSignature)
	{
		MemberUnit member= getMemberUnitOrNull(className, signature);

		if (member == null)
		{
			ClassUnit clazz= getClassUnit(className);
			if (signature.isMethod())
			{
				member= new MethodUnit(signature, clazz, nameAndSignature);
			}
			else if (signature.isConstructor())
			{
				member= new ConstructorUnit(signature, clazz, nameAndSignature);
			}
			else
			{
				member= new FieldUnit(signature, clazz);
			}

		}

		return member;
	}

	public FieldUnit getOrCreateFieldUnit(ObjectType type, String name)
	{
		return (FieldUnit) getOrCreateMemberUnit(type.getClassName(), Project.singleton.getSignature(name), "");
	}

	public void addReference(MethodDeclaration decl, FieldAccess fa)
	{
		ProcedureUnit source= getOrCreateProcedureUnit(decl.getMethodBinding());
		source.addTarget(Project.singleton.getSignature(fa));
	}

	public void addReference(MethodDeclaration decl, MethodInvocation invocation)
	{
		ProcedureUnit source= getOrCreateProcedureUnit(decl.getMethodBinding());
		source.addTarget(Project.singleton.getSignature(invocation.getMethodBinding().toString()));
	}

	public void addReference(MethodDeclaration decl, ArrayCreation ac)
	{
		ProcedureUnit source= getOrCreateProcedureUnit(decl.getMethodBinding());
		Signature signature= Project.getSingleton().getArraySignature(ac.getTypeBinding());
		for (int i= 0; i < ac.getDimensions().size(); i++)
		{

			source.addTarget(Project.singleton.getSignature(signature.toString().substring(i) + "#length"));
		}
	}

	public Collection<ClassUnit> getClasses()
	{
		return new ArrayList<ClassUnit>(classesByName.values());
	}

	public void resolve(ClassUnit clazz)
	{
		if (clazz.isResolved())
			return;

		if (clazz.getName().startsWith("["))
		{

			clazz.setSuperUnit(getJavaLangObject());

			clazz.setResolved(true);

			new FieldUnit(getSignature("length"), clazz);

			TypeDeclaration typeDecl= new TypeDeclaration(new ObjectType(clazz.getName()), 0, new HashMap<String, String>());//revisar annotations
			typeDecl.setSuperType(Type.OBJECT);
			typeDecl.visit(DragomeJsCompiler.compiler.generator);
		}
		else
		{
			TypeResolver resolver= new TypeResolver(this, DragomeJsCompiler.compiler.generator);
			visitSuperTypes(clazz, resolver);
		}
	}

	public void writeSignatures(Writer writer)
	{
		try
		{
			for (String genericSignature : genericSignatures)
			{
				String[] split= genericSignature.split("\\|");
				if (getWrittenSignatures().contains(split[0] /*+ "|" + split[1]*/))
					writer.write("addSignatureTo(" + split[0] + ",\"" + split[1] + "\", \"" + split[2] + "\");\n");
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void addGenericSignature(String genericSignature)
	{
		this.genericSignatures.add(genericSignature);
	}

	public int incrementBadMethods(int i)
	{
		return badMethods+= i;
	}

	public int getBadMethods()
	{
		return badMethods;
	}

	public void addTypeAnnotations(TypeDeclaration typeDecl)
	{
		for (Iterator<String> iterator= getTypeDeclarationsWithAnnotations().iterator(); iterator.hasNext();)
		{
			String declaredAnnotation= (String) iterator.next();
			if (declaredAnnotation.equals(typeDecl.getClassName()))
				iterator.remove();
		}

		if (!typeDecl.getAnnotations().isEmpty())
		{
			for (Entry<String, String> entry : typeDecl.getAnnotations().entrySet())
			{
				String value= entry.getValue();
				if (value.isEmpty())
					value= " ";
				getTypeDeclarationsWithAnnotations().add(typeDecl.getClassName() + "#" + entry.getKey() + "#" + value);
			}
		}
	}

	public Set<String> getTypeDeclarationsWithAnnotations()
	{
		return typeDeclarationsWithAnnotations;
	}

	public void setClasspathFilter(ClasspathFileFilter classpathFilter)
	{
		this.classpathFilter= classpathFilter;
	}

	public void setStopOnMissingClass(boolean stopOnMissingClass)
	{
		this.stopOnMissingClass= stopOnMissingClass;
	}
}
