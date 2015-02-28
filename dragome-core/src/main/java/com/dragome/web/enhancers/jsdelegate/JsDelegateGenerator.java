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

package com.dragome.web.enhancers.jsdelegate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Descriptor;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.interfaces.DelegateStrategy;
import com.dragome.web.html.dom.html5canvas.HTMLCanvasElement;

public class JsDelegateGenerator
{
	private DelegateStrategy delegateStrategy;
	private String classpath= "";
	private Map<Class<?>, File> generatedFiles= new HashMap<Class<?>, File>();
	private File baseDir;

	public JsDelegateGenerator(DelegateStrategy delegateStrategy)
	{
		this.delegateStrategy= delegateStrategy;
	}

	public JsDelegateGenerator()
	{
		this(new DefaultDelegateStrategy());
	}

	public JsDelegateGenerator(File tempDir, String classpath, DelegateStrategy delegateStrategy)
	{
		this(classpath);
		this.baseDir= tempDir;
		this.delegateStrategy= delegateStrategy;
	}

	public JsDelegateGenerator(String classpath)
	{
		this();
		this.classpath= classpath;
	}

	public JsDelegateGenerator(File baseDir)
	{
		this();
		this.baseDir= baseDir;
	}

	public byte[] generateBytecode(String className, Class<?> interface1)
	{
		try
		{
			CtClass cc= generateCtClass(className, interface1);

			byte[] bytecode= cc.toBytecode();
			return bytecode;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private CtClass generateCtClass(String className, Class<?> interface1)
	{
		try
		{
			ClassPool pool= ClassPool.getDefault();
			CtClass cc= pool.makeClass(className);
			CtClass resolveCtClass= resolveCtClass(interface1);
			cc.addInterface(resolveCtClass);
			Class<?> scriptHelperClass= ScriptHelper.class;

            pool.insertClassPath(new ClassClassPath(Object.class));
            pool.insertClassPath(new ClassClassPath(scriptHelperClass));

			StringBuilder constructorBody= new StringBuilder();
			constructorBody.append(scriptHelperClass.getName() + ".put(\"$1\", $1, this);");
			constructorBody.append(scriptHelperClass.getName() + ".eval(\"this.node= $1\", this);");

			CtClass objectCtClass= resolveCtClass(Object.class);

			addConstructors(cc, constructorBody, objectCtClass);

			for (CtMethod method : resolveCtClass.getMethods())
				if (!method.getDeclaringClass().equals(objectCtClass))
					cc.addMethod(createDelegateMethod(interface1, cc, scriptHelperClass, method));

			return cc;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private CtMethod createDelegateMethod(Class<?> interface1, CtClass cc, Class<?> scriptHelperClass, CtMethod method) throws NotFoundException, CannotCompileException
	{
		StringBuffer code= new StringBuffer();

		if (delegateStrategy.isPropertyWriteMethod(method))
			createSetterMethodCall(scriptHelperClass, method, code);
		else if (delegateStrategy.isPropertyReadMethod(method))
			createGetterMethodCall(method, code);
		else
			createGenericDelegateMethodCall(scriptHelperClass, method, code);

		String body= code.toString();

		CtClass returnType= method.getReturnType();

		body= configureEvaluation(interface1, scriptHelperClass, method, body, returnType);

		body= "{" + body + "}";

		CtMethod ctMethod= CtNewMethod.make(AccessFlag.PUBLIC, returnType, method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body, cc);
		return ctMethod;
	}

	private void addConstructors(CtClass cc, StringBuilder constructorBody, CtClass objectCtClass) throws CannotCompileException
	{
		CtConstructor ctConstructor= CtNewConstructor.make(new CtClass[] { objectCtClass }, new CtClass[] {}, "{" + constructorBody.toString() + "}", cc);
		cc.addConstructor(ctConstructor);

		cc.addConstructor(CtNewConstructor.make(new CtClass[] {}, new CtClass[] {}, "{}", cc));
	}

	private String configureEvaluation(Class<?> interface1, Class<?> scriptHelperClass, CtMethod method, String body, CtClass returnType) throws NotFoundException
	{
		if (!typeIsEqual(returnType, Void.class) && !typeIsEqual(returnType, void.class))
		{
			if (typeIsEqual(returnType, Boolean.class) || typeIsEqual(returnType, boolean.class))
				body= body.replace("$eval$", "return " + scriptHelperClass.getName() + ".evalBoolean");
			else if (typeIsEqual(returnType, Integer.class) || typeIsEqual(returnType, int.class) || typeIsEqual(returnType, Short.class) || typeIsEqual(returnType, short.class))
				body= body.replace("$eval$", "return " + scriptHelperClass.getName() + ".evalInt");
			else if (typeIsEqual(returnType, Double.class) || typeIsEqual(returnType, double.class))
				body= body.replace("$eval$", "return " + scriptHelperClass.getName() + ".evalDouble");
			else if (typeIsEqual(returnType, String.class))
				body= body.replace("$eval$", "return (java.lang.String)" + scriptHelperClass.getName() + ".eval");
			else
				body= createBodyForReference(interface1, scriptHelperClass, method, body, returnType);
		}
		else
			body= body.replace("$eval$", scriptHelperClass.getName() + ".evalNoResult");
		return body;
	}

	private String createBodyForReference(Class<?> interface1, Class<?> scriptHelperClass, CtMethod method, String body, CtClass returnType)
	{
		body= body.replace("$eval$", "Object temp= " + scriptHelperClass.getName() + ".eval");
		String jvmName= toJavaName(returnType) + ".class";
		String subTypeExtractor= delegateStrategy.getSubTypeExtractorFor(interface1, method.getName());
		if (subTypeExtractor != null)
		{
			String s= scriptHelperClass.getName() + ".eval(\"" + subTypeExtractor + "\", this)";
			jvmName= "new " + delegateStrategy.getSubTypeFactoryClassFor(interface1, method.getName()).getName() + "().getSubTypeWith(" + s + ")";
			body+= scriptHelperClass.getName() + ".put (\"temp\", temp, this);";
		}

		body= body + "return " + JsDelegateFactory.class.getName() + ".createFrom(temp, " + jvmName + ");";
		return body;
	}

	private static String toJavaName(CtClass returnType)
	{
		return Descriptor.toJavaName(Descriptor.toJvmName(returnType));
	}

	private boolean typeIsEqual(CtClass returnType, Class<?> class1) throws NotFoundException
	{
		return returnType.equals(resolveCtClass(class1));
	}

	private static void createSetterMethodCall(Class<?> scriptHelperClass, CtMethod method, StringBuffer code) throws NotFoundException
	{
		CtClass parameterType= method.getParameterTypes()[0];
		code.append(scriptHelperClass.getName() + ".put(\"$1\", " + createParameterForPut("$1", parameterType) + ", this);");
		code.append("$eval$(\"this.node." + method.getName().toLowerCase().charAt(3) + method.getName().substring(4) + "= " + createVariableForEval("$1", parameterType) + "\", this);");
	}

	private static String createParameterForPut(String string, CtClass ctClass)
	{
		String javaName= toJavaName(ctClass);

		if (javaName.equals(Boolean.class.getName()) || javaName.equals(boolean.class.getName()))
			return string + " + \"\"";
		else if (javaName.equals(Integer.class.getName()) || javaName.equals(int.class.getName()) || //
				javaName.equals(Double.class.getName()) || javaName.equals(double.class.getName()) || //
				javaName.equals(Short.class.getName()) || javaName.equals(short.class.getName()) || //
				javaName.equals(Float.class.getName()) || javaName.equals(float.class.getName()) || //
				javaName.equals(Long.class.getName()) || javaName.equals(long.class.getName()))
			return "(double)" + string;
		else
			return string;
	}

	private static String createVariableForEval(String string, CtClass ctClass)
	{
		if (ctClass.isInterface())
			return string + ".node";
		else
			return string;
	}

	private static void createGetterMethodCall(CtMethod method, StringBuffer code)
	{
		code.append("$eval$(\"this.node." + method.getName().toLowerCase().charAt(3) + method.getName().substring(4) + "\", this);");
	}

	private static void createGenericDelegateMethodCall(Class<?> scriptHelperClass, CtMethod method, StringBuffer code) throws NotFoundException
	{
		StringBuilder parameters= new StringBuilder();
		int parametersCount= method.getParameterTypes().length;
		if (parametersCount > 0)
		{
			for (int i= 0; i < parametersCount; i++)
			{
				String variableName= "$" + (i + 1);
				CtClass parameterType= method.getParameterTypes()[i];
				code.append("" + scriptHelperClass.getName() + ".put(\"" + variableName + "\", " + createParameterForPut(variableName, parameterType) + ", this);");

				if (i != 0)
					parameters.append(", ");

				parameters.append(createVariableForEval(variableName, parameterType));
			}
		}

		code.append("$eval$(\"this.node." + method.getName() + "(" + parameters.toString() + ")\", this);");
	}

	private CtClass resolveCtClass(Class<?> clazz) throws NotFoundException
	{
		ClassPool pool= ClassPool.getDefault();
		pool.appendPathList(classpath);
		return pool.get(clazz.getName());
	}

	public static void main(String[] args) throws Exception
	{
		File baseDir= new File(System.getProperty("java.io.tmpdir"));

		byte[] generateFile= new JsDelegateGenerator(baseDir).generate(HTMLCanvasElement.class);
	}

	public byte[] generateBytecode(Class<?> interface1)
	{
		return generateBytecode("JsDelegate" + interface1.getSimpleName(), interface1);
	}

	public byte[] generate(Class<?> interface1)
	{
		try
		{
			File file= generatedFiles.get(interface1);
			String type= interface1.getName();
			String classname= createDelegateClassName(type);

			if (file == null)
			{
				CtClass generateCtClass= generateCtClass(classname, interface1);
				generateCtClass.writeFile(baseDir.getAbsolutePath());
				file= new File(baseDir.getAbsolutePath() + File.separatorChar + classname.replace('.', File.separatorChar) + ".class");
				generatedFiles.put(interface1, file);
			}

			CtClass ctClass= ClassPool.getDefault().get(classname);
			return ctClass.toBytecode();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String createDelegateClassName(String type)
	{
		int lastIndexOf= type.lastIndexOf(".");
		String classname= type.substring(0, lastIndexOf + 1) + "Delegate" + type.substring(lastIndexOf + 1);
		return classname;
	}

	public File getBaseDir()
	{
		return baseDir;
	}
}