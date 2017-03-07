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

package com.dragome.compiler.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.AnnotationDefault;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.AttributeReader;
import org.apache.bcel.classfile.ClassElementValue;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.commons.io.IOUtils;

import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.Project;
import com.dragome.compiler.annotations.AnnotationReader;
import com.dragome.compiler.ast.ASTNode;
import com.dragome.compiler.ast.Block;
import com.dragome.compiler.ast.MethodBinding;
import com.dragome.compiler.ast.MethodDeclaration;
import com.dragome.compiler.ast.ReturnStatement;
import com.dragome.compiler.ast.ThrowStatement;
import com.dragome.compiler.ast.TypeDeclaration;
import com.dragome.compiler.ast.VariableDeclaration;
import com.dragome.compiler.exceptions.UnhandledCompilerProblemException;
import com.dragome.compiler.generators.DragomeJavaScriptGenerator;
import com.dragome.compiler.invokedynamic.InvokeDynamicBackporter;
import com.dragome.compiler.type.Signature;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.utils.Log;
import com.dragome.compiler.utils.Utils;

public class Parser
{

	public static String getResourcePath(String name)
	{
		name= name.replace('.', '/') + ".class";
		java.net.URL url= Parser.class.getClassLoader().getResource(name);
		if (url == null)
			throw new RuntimeException("Resource not found: " + name);
		return url.getPath();
	}

	private JavaClass jc;

	private ClassUnit fileUnit;

	InvokeDynamicBackporter lambdaUsageBackporter= new InvokeDynamicBackporter();

	public Parser(ClassUnit theFileUnit)
	{
		fileUnit= theFileUnit;
		fileUnit.annotations= null;

		AttributeReader r= new AnnotationReader(fileUnit);
		Attribute.addAttributeReader("RuntimeVisibleAnnotations", r);

		try
		{
			InputStream openInputStream= fileUnit.getClassFile().openInputStream();

			String filename= fileUnit.getName();
			byte[] originalByteArray= IOUtils.toByteArray(openInputStream);
			byte[] transformedArray= originalByteArray;

			transformedArray= lambdaUsageBackporter.transform(filename, transformedArray);

			if (DragomeJsCompiler.compiler.bytecodeTransformer != null)
				if (DragomeJsCompiler.compiler.bytecodeTransformer.requiresTransformation(filename))
					transformedArray= DragomeJsCompiler.compiler.bytecodeTransformer.transform(filename, transformedArray);

			fileUnit.setBytecodeArrayI(transformedArray);

			ClassParser cp= new ClassParser(new ByteArrayInputStream(transformedArray), filename);
			jc= cp.parse();

		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	public TypeDeclaration parse()
	{
		DescendingVisitor classWalker= new DescendingVisitor(jc, new EmptyVisitor()
		{
			public void visitConstantClass(ConstantClass obj)
			{
				ConstantPool cp= jc.getConstantPool();
				String bytes= obj.getBytes(cp);
				fileUnit.addDependency(bytes.replace("/", "."));
			}
		});
		classWalker.visit();

		org.apache.bcel.classfile.Method[] bcelMethods= jc.getMethods();
		org.apache.bcel.classfile.Field[] bcelFields= jc.getFields();

		ObjectType type= new ObjectType(jc.getClassName());
		Map<String, String> annotationsValues= getAnnotationsValues(jc.getAttributes(), "::::");

		for (Field field : bcelFields)
		{
			Attribute[] attributes= field.getAttributes();
			String name= ":" + field.getName() + ":::";
			Map<String, String> methodAnnotationsValues= getAnnotationsValues(attributes, name);
			annotationsValues.putAll(methodAnnotationsValues);
		}

		for (Method method : bcelMethods)
		{
			Attribute[] attributes= method.getAttributes();
			String name= "::" + method.getName() + ":";
			Map<String, String> methodAnnotationsValues= getAnnotationsValues(attributes, name + ":");

			ParameterAnnotationEntry[] parameterAnnotationEntries= method.getParameterAnnotationEntries();
			for (int i= 0; i < parameterAnnotationEntries.length; i++)
			{
				AnnotationEntry[] annotationEntries= parameterAnnotationEntries[i].getAnnotationEntries();
				putEntries(name + "arg" + i + ":", annotationsValues, annotationEntries);
			}

			annotationsValues.putAll(methodAnnotationsValues);
		}

		TypeDeclaration typeDecl= new TypeDeclaration(type, jc.getAccessFlags(), annotationsValues);
		fileUnit.isInterface= Modifier.isInterface(typeDecl.getAccess());

		fileUnit.isAbstract= Modifier.isAbstract(typeDecl.getAccess());
		Project.singleton.addTypeAnnotations(typeDecl);

		fileUnit.setAnnotations(annotationsValues);

		if (!type.getClassName().equals("java.lang.Object"))
		{

			ObjectType superType= new ObjectType(jc.getSuperclassName());
			typeDecl.setSuperType(superType);
			ClassUnit superUnit= Project.getSingleton().getClassUnit(superType.getClassName());
			fileUnit.setSuperUnit(superUnit);

			String[] interfaceNames= jc.getInterfaceNames();
			for (int i= 0; i < interfaceNames.length; i++)
			{
				ObjectType interfaceType= new ObjectType(interfaceNames[i]);
				ClassUnit interfaceUnit= Project.getSingleton().getClassUnit(interfaceType.getClassName());
				fileUnit.addInterface(interfaceUnit);
			}
		}

		Field[] fields= jc.getFields();
		for (int i= 0; i < fields.length; i++)
		{
			Field field= fields[i];
			VariableDeclaration variableDecl= new VariableDeclaration(VariableDeclaration.NON_LOCAL);
			variableDecl.setName(field.getName());
			variableDecl.setModifiers(field.getModifiers());
			variableDecl.setType(field.getType());

			typeDecl.addField(variableDecl);

			String genericSignature= field.getGenericSignature();
			if (genericSignature != null && !genericSignature.equals(field.getSignature()))
			{
				Type type2= Type.getType(field.getSignature());
				String normalizedSignature= "$$$" + field.getName() + DragomeJavaScriptGenerator.FIELD_TYPE_SEPARATOR + DragomeJavaScriptGenerator.normalizeExpression(type2.toString());
				String normalizedClassname= DragomeJavaScriptGenerator.normalizeExpression(type.getClassName());
				Project.getSingleton().addGenericSignature(normalizedClassname + "|" + normalizedSignature + "|" + genericSignature);
				//		System.out.println(genericSignature);
			}
		}

		for (int i= 0; i < bcelMethods.length; i++)
		{
			Method method= bcelMethods[i];

			Map<String, String> methodAnnotationsValues= null;

			try
			{
				methodAnnotationsValues= checkSuperAnnotations(method, jc, "MethodAlias", 0, 4);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			MethodBinding binding= MethodBinding.lookup(jc.getClassName(), method.getName(), method.getSignature());

			String genericSignature= method.getGenericSignature();
			if (genericSignature != null && !genericSignature.equals(method.getSignature()))
			{
				Signature signature= Project.getSingleton().getSignature(binding.toString()).relative();
				String normalizedSignature= DragomeJavaScriptGenerator.normalizeExpression(signature);
				String normalizedClassname= DragomeJavaScriptGenerator.normalizeExpression(type.getClassName());
				Project.getSingleton().addGenericSignature(normalizedClassname + "|" + normalizedSignature + "|" + genericSignature);
				//		System.out.println(genericSignature);
			}

			if (DragomeJsCompiler.compiler.getSingleEntryPoint() != null)
			{
				Signature signature= Project.getSingleton().getSignature(binding.toString());
				String singleSignature= DragomeJsCompiler.compiler.getSingleEntryPoint();
				if (!signature.toString().equals(singleSignature))
					continue;
			}

			MethodDeclaration methodDecl= new MethodDeclaration(binding, method.getAccessFlags(), method.getCode(), methodAnnotationsValues);
			typeDecl.addMethod(methodDecl);

			parseMethod(typeDecl, methodDecl, method);
		}

		return typeDecl;
	}

	// Recursive algorithm to check for annotations. If the super method has the annotation it will use it. if the child has it, it will skip the super method.
	private Map<String, String> checkSuperAnnotations(final Method method, JavaClass curClass, final String annotationName, int nDepth, final int maxRecursive) throws ClassNotFoundException
	{
		String methodName= method.getName();
		String signature= method.getSignature();
		nDepth++;
		Map<String, String> curAnnotationsValues= null;

		Method curMethod= null;
		Method[] methods= curClass.getMethods();
		for (int j= 0; j < methods.length; j++)
		{ // find the method if there is one.
			if (methods[j].getName().equals(methodName) && methods[j].getArgumentTypes().length == method.getArgumentTypes().length && methods[j].getSignature().equals(signature))
			{
				curMethod= methods[j];
				break;
			}
		}

		if (curMethod != null)
		{
			Attribute[] attributes= curMethod.getAttributes();
			curAnnotationsValues= getAnnotationsValues(attributes, "");

			Set<Entry<String, String>> entrySet= curAnnotationsValues.entrySet();
			Iterator<Entry<String, String>> iterator= entrySet.iterator();
			while (iterator.hasNext())
			{
				Entry<String, String> next= iterator.next();
				String key= next.getKey();
				if (key.contains(annotationName))
					return curAnnotationsValues; // if contains the annotation already skip checking the super methods.
			}
		}
		else
			curAnnotationsValues= new LinkedHashMap<String, String>();

		if (nDepth >= maxRecursive)
			return curAnnotationsValues;

		JavaClass[] interfaces= curClass.getInterfaces();
		for (int i= 0; i < interfaces.length; i++)
		{ // check interfaces
			JavaClass javaClass= interfaces[i];
			Map<String, String> returnedAnnotation= checkSuperAnnotations(method, javaClass, annotationName, nDepth, maxRecursive);
			mergeAnno(curAnnotationsValues, returnedAnnotation, annotationName);
		}
		JavaClass superClass= curClass.getSuperClass(); // check super class
		if (superClass != null && superClass.getClassName().contains("java.lang.Object") == false)
		{ // stop checking super when It detects root java object.
			Map<String, String> returnedAnnotation= checkSuperAnnotations(method, superClass, annotationName, nDepth, maxRecursive);
			mergeAnno(curAnnotationsValues, returnedAnnotation, annotationName);
		}
		return curAnnotationsValues;
	}

	private void mergeAnno(Map<String, String> curAnnotationsValues, Map<String, String> returnedAnnotation, String annoationName)
	{
		Set<Entry<String, String>> entrySet= returnedAnnotation.entrySet();
		Iterator<Entry<String, String>> iterator= entrySet.iterator();
		while (iterator.hasNext())
		{
			Entry<String, String> next= iterator.next();
			String key= next.getKey();
			String value= next.getValue();
			if (key.contains(annoationName))
			{
				boolean containsKey= curAnnotationsValues.containsKey(key);
				if (containsKey == false)
					curAnnotationsValues.put(key, value);
			}
		}
	}

	private Map<String, String> getAnnotationsValues(Attribute[] attributes, String prefix)
	{
		Map<String, String> result= new LinkedHashMap<String, String>();
		for (Attribute attribute : attributes)
		{
			if (attribute instanceof Annotations)
			{
				Annotations annotations= (Annotations) attribute;
				AnnotationEntry[] entries= annotations.getAnnotationEntries();
				List<AnnotationEntry> newEntries= new ArrayList<AnnotationEntry>();
				putEntries(prefix, result, entries);
			}
		}
		return result;
	}
	private void putEntries(String prefix, Map<String, String> result, AnnotationEntry[] entries)
	{
		for (AnnotationEntry entry : entries)
		{
			Type type= Type.getType(entry.getAnnotationType());
			addDefaults(type);
			String key= type + "#" + prefix;

			if (entry.getElementValuePairs().length == 0)
				result.put(key, " ");

			for (int i= 0; i < entry.getElementValuePairs().length; i++)
			{
				ElementValuePair elementValuePair= entry.getElementValuePairs()[i];
				result.put(key + elementValuePair.getNameString(), elementValuePair.getValue().toString());
			}
		}
	}

	private void addDefaults(Type type)
	{
		try
		{
			ClassUnit classUnit= Project.singleton.getClassUnit(type.toString());
			JavaClass aClass= Repository.lookupClass(type.toString());
			for (Method method : aClass.getMethods())
			{
				final AnnotationDefault a= (AnnotationDefault) findAttribute("AnnotationDefault", method.getAttributes());
				if (a != null)
					if (a.getDefaultValue() instanceof ClassElementValue)
					{
						ClassElementValue aClass1= (ClassElementValue) a.getDefaultValue();
						classUnit.addAnnotationDefault(method.getName(), aClass1.getClassString());
					}
					else
						classUnit.addAnnotationDefault(method.getName(), a.getDefaultValue().toString());
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	protected Attribute findAttribute(final String name, final Attribute[] all)
	{
		final List<Attribute> chosenAttrsList= new ArrayList<>();
		for (final Attribute element : all)
		{
			if (element.getName().equals(name))
			{
				chosenAttrsList.add(element);
			}
		}
		return chosenAttrsList.isEmpty() ? null : chosenAttrsList.get(0);
	}

	public void parseMethod(TypeDeclaration typeDecl, MethodDeclaration methodDecl, Method method)
	{
		Type[] types= method.getArgumentTypes();

		int offset;
		if (Modifier.isStatic(methodDecl.getAccess()))
		{
			offset= 0;
		}
		else
		{

			offset= 1;
		}
		for (int i= 0; i < types.length; i++)
		{
			VariableDeclaration variableDecl= new VariableDeclaration(VariableDeclaration.LOCAL_PARAMETER);
			variableDecl.setName(VariableDeclaration.getLocalVariableName(method, offset, 0));
			variableDecl.setType(types[i]);
			methodDecl.addParameter(variableDecl);
			offset+= types[i].getSize();
		}

		if (methodDecl.getCode() == null)
			return;

		Log.getLogger().debug("Parsing " + methodDecl.toString());
		Pass1 pass1= new Pass1(jc);

		try
		{
			pass1.parse(method, methodDecl);
		}
		catch (Throwable ex)
		{
			if (ex instanceof UnhandledCompilerProblemException)
			{
				Pass1.setClassNotReversible(methodDecl);
			}
			else
			{
				ASTNode node= null;
				if (ex instanceof ParseException)
				{
					node= ((ParseException) ex).getAstNode();
				}
				else
				{
					node= Pass1.getCurrentNode();
				}

				if (DragomeJsCompiler.compiler.isFailOnError())
				{
					throw Utils.generateException(ex, methodDecl, node);
				}
				else
				{
					fileUnit.addNotReversibleMethod(Pass1.extractMethodNameSignature(methodDecl.getMethodBinding()));
					//String msg= Utils.generateExceptionMessage(methodDecl, node);
					//DragomeJsCompiler.errorCount++;
					//		    Log.getLogger().error(msg + "\n" + Utils.stackTraceToString(ex));
				}

			}
			Block body= new Block();
			ThrowStatement throwStmt= new ThrowStatement();
			/*
			MethodBinding binding= MethodBinding.lookup("java.lang.RuntimeException", "<init>", "(java/lang/String)V;");
			ClassInstanceCreation cic= new ClassInstanceCreation(methodDecl, binding);
			cic.addArgument(new StringLiteral("Unresolved decompilation problem"));
			throwStmt.setExpression(cic);
			body.appendChild(throwStmt);*/
			methodDecl.setBody(body);

		}

		if (DragomeJsCompiler.compiler.optimize && methodDecl.getBody().getLastChild() instanceof ReturnStatement)
		{
			ReturnStatement ret= (ReturnStatement) methodDecl.getBody().getLastChild();
			if (ret.getExpression() == null)
			{
				methodDecl.getBody().removeChild(ret);
			}
		}

		//		Pass1.dump(methodDecl.getBody(), "Body of " + methodDecl.toString());

		return;
	}

	public ConstantPool getConstantPool()
	{
		return jc.getConstantPool();
	}

	public String toString()
	{
		return jc.getClassName();
	}
}
