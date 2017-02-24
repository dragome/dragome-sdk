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

package com.dragome.web.enhancers.jsdelegate.serverside;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.compiler.classpath.InMemoryClasspathFile;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.annotations.Keep;
import com.dragome.web.enhancers.jsdelegate.DefaultDelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.enhancers.jsdelegate.interfaces.DelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.interfaces.SubTypeFactory;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Descriptor;

public class JsDelegateGenerator
{
	private DelegateStrategy delegateStrategy;

	public JsDelegateGenerator(DelegateStrategy delegateStrategy)
	{
		this.delegateStrategy= delegateStrategy;
	}

	public JsDelegateGenerator()
	{
		this(new DefaultDelegateStrategy());
	}

	public JsDelegateGenerator(String classpath, DelegateStrategy delegateStrategy)
	{
		this(classpath);
		this.delegateStrategy= delegateStrategy;
	}

	public JsDelegateGenerator(String classpath)
	{
		this();
		appendClasspath(classpath);
	}

	private void appendClasspath(String classpath)
	{
		try
		{
			ClassPool pool= ClassPool.getDefault();
			pool.appendPathList(classpath);
		}
		catch (NotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public JsDelegateGenerator(File baseDir)
	{
		this();
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
			cc.addInterface(resolveCtClass(Keep.class));
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

			Set<CtMethod> methods= findAllMethods(resolveCtClass, new HashSet<CtMethod>());

			for (CtMethod method : methods)
			{
				DelegateCode annotation= (DelegateCode) method.getAnnotation(DelegateCode.class);
				if (annotation != null && annotation.ignore())
					continue;
				if (!method.getDeclaringClass().equals(objectCtClass))
					cc.addMethod(createDelegateMethod(interface1, cc, scriptHelperClass, method, annotation));
			}
			return cc;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private Set<CtMethod> findAllMethods(CtClass resolveCtClass, Set<CtMethod> methods) throws NotFoundException
	{
		if (resolveCtClass != null)
		{
			methods.addAll(Arrays.asList(resolveCtClass.getMethods()));

			findAllMethods(resolveCtClass.getSuperclass(), methods);

			CtClass[] interfaces= resolveCtClass.getInterfaces();
			for (CtClass ctClass : interfaces)
				findAllMethods(ctClass, methods);
		}
		return methods;
	}

	private CtMethod createDelegateMethod(Class<?> interface1, CtClass cc, Class<?> scriptHelperClass, CtMethod method, DelegateCode delegateCodeAnnotation) throws Exception
	{
		StringBuffer code= new StringBuffer();
		String params= createParameters(method, code);
		String customCode= null;
		CtClass returnType= method.getReturnType();

		String body= delegateStrategy.createMethodBody(toJavaMethod(interface1, method), params);
		if (body == null)
		{
			if (delegateCodeAnnotation != null && !delegateCodeAnnotation.eval().isEmpty())
				code.append("$eval$(\"" + delegateCodeAnnotation.eval() + "\", this);");
			else
			{
				customCode= delegateStrategy.createMethodCall(toJavaMethod(interface1, method), params);
				code.append("$eval$(\"" + customCode + "\", this);");
			}

			body= code.toString();

			body= configureEvaluation(interface1, scriptHelperClass, method, body, returnType);
		}
		body= "{" + body + "}";

		CtMethod ctMethod= CtNewMethod.make(AccessFlag.PUBLIC, returnType, method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body, cc);
		return ctMethod;
	}

	private Method toJavaMethod(Class<?> interface1, CtMethod method) throws Exception
	{
		CtClass[] parameterTypes= method.getParameterTypes();
		return interface1.getMethod(method.getName(), toJavaClass(parameterTypes));
	}

	private Class<?>[] toJavaClass(CtClass[] parameterTypes) throws Exception
	{
		List<Class<?>> result= new ArrayList<>();
		for (CtClass ctClass : parameterTypes)
		{
			String javaName= toJavaName(ctClass);
			result.add(getJavaClass(javaName));
		}

		return result.toArray(new Class[0]);
	}

	public static Class<?> getJavaClass(String classname) throws ClassNotFoundException
	{
		if (classname.equals("boolean"))
			return boolean.class;
		else if (classname.equals("int"))
			return int.class;
		else if (classname.equals("short"))
			return short.class;
		else if (classname.equals("long"))
			return long.class;
		else if (classname.equals("float"))
			return float.class;
		else if (classname.equals("double"))
			return double.class;
		else if (classname.equals("byte"))
			return byte.class;
		else
			return Class.forName(classname);
	}

	private void addConstructors(CtClass cc, StringBuilder constructorBody, CtClass objectCtClass) throws CannotCompileException
	{
		CtConstructor ctConstructor= CtNewConstructor.make(new CtClass[] { objectCtClass }, new CtClass[] {}, "{" + constructorBody.toString() + "}", cc);
		cc.addConstructor(ctConstructor);

		cc.addConstructor(CtNewConstructor.make(new CtClass[] {}, new CtClass[] {}, "{}", cc));
	}

	private String configureEvaluation(Class<?> interface1, Class<?> scriptHelperClass, CtMethod method, String body, CtClass returnType) throws Exception
	{
		String scriptHelperClassname= scriptHelperClass.getName();
		String returnTypeName= toJavaName(returnType);

		if (!returnTypeName.equals(Void.class.getName()) && !returnTypeName.equals(void.class.getName()))
		{
			if (returnTypeName.equals(Boolean.class.getName()) || returnTypeName.equals(boolean.class.getName()))
				body= body.replace("$eval$", "return " + scriptHelperClassname + ".evalBoolean");
			else
			{
				boolean isInteger= returnTypeName.equals(Integer.class.getName()) || returnTypeName.equals(int.class.getName());
				boolean isShort= returnTypeName.equals(Short.class.getName()) || returnTypeName.equals(short.class.getName());
				boolean isByte= returnTypeName.equals(Byte.class.getName()) || returnTypeName.equals(byte.class.getName());
				if (isInteger || isShort || isByte)
					body= body.replace("$eval$", "return " + scriptHelperClassname + ".evalInt");
				else if (returnTypeName.equals(Double.class.getName()) || returnTypeName.equals(double.class.getName()))
					body= body.replace("$eval$", "return " + scriptHelperClassname + ".evalDouble");
				else if (returnTypeName.equals(Float.class.getName()) || returnTypeName.equals(float.class.getName()))
					body= body.replace("$eval$", "return " + scriptHelperClassname + ".evalFloat");
				else if (returnTypeName.equals(Long.class.getName()) || returnTypeName.equals(long.class.getName()))
					body= body.replace("$eval$", "return " + scriptHelperClassname + ".evalLong");
				else if (returnTypeName.equals(String.class.getName()))
					body= body.replace("$eval$", "return (java.lang.String)" + scriptHelperClassname + ".eval");
				else
					body= createBodyForReference(interface1, scriptHelperClass, method, body, returnType);
			}
		}
		else
			body= body.replace("$eval$", scriptHelperClassname + ".evalNoResult");

		return body;
	}

	private String createBodyForReference(Class<?> interface1, Class<?> scriptHelperClass, CtMethod method, String body, CtClass returnType) throws Exception
	{
		body= body.replace("$eval$", "Object temp= " + scriptHelperClass.getName() + ".eval");
		String returnTypeAsString= toJavaName(returnType) + ".class";
		String subTypeExtractorExpression= delegateStrategy.getSubTypeExtractorFor(interface1, method.getName());
		if (subTypeExtractorExpression != null)
		{
			String subTypeId= scriptHelperClass.getName() + ".eval(\"" + subTypeExtractorExpression + "\", this)";
			Class<? extends SubTypeFactory> subTypeFactoryClass= delegateStrategy.getSubTypeFactoryClassFor(interface1, method.getName());
			returnTypeAsString= "new " + subTypeFactoryClass.getName() + "().getSubTypeWith(" + subTypeId + ")";
			body+= scriptHelperClass.getName() + ".put (\"temp\", temp, this);";
		}

		Method javaMethod= toJavaMethod(interface1, method);
		body= body + delegateStrategy.createReturnExpression(interface1, javaMethod, returnTypeAsString);
		return body;
	}

	private static String toJavaName(CtClass returnType)
	{
		return Descriptor.toJavaName(Descriptor.toJvmName(returnType));
	}

	private static String createParameters(CtMethod method, StringBuffer code) throws NotFoundException
	{
		Class<?> scriptHelperClass= ScriptHelper.class;
		int parametersCount= method.getParameterTypes().length;
		if (parametersCount > 0)
		{
			StringBuilder parameters= new StringBuilder();
			for (int i= 0; i < parametersCount; i++)
			{
				String variableName= "$" + (i + 1);
				CtClass parameterType= method.getParameterTypes()[i];
				code.append("" + scriptHelperClass.getName() + ".put(\"" + variableName + "\", " + createParameterForPut(variableName, parameterType) + ", this);");

				if (i != 0)
					parameters.append(", ");

				parameters.append(createVariableForEval(variableName, parameterType));
			}
			return parameters.toString();
		}
		return null;
	}

	private static String createParameterForPut(String string, CtClass ctClass)
	{
		String javaName= toJavaName(ctClass);

		if (javaName.equals(Integer.class.getName()) || javaName.equals(int.class.getName()) || //
				javaName.equals(Double.class.getName()) || javaName.equals(double.class.getName()) || //
				javaName.equals(Short.class.getName()) || javaName.equals(short.class.getName()) || //
				javaName.equals(Float.class.getName()) || javaName.equals(float.class.getName()) || //
				javaName.equals(Byte.class.getName()) || javaName.equals(byte.class.getName()) || //
				javaName.equals(Long.class.getName()) || javaName.equals(long.class.getName()))
			return "(double)" + string;
		else
			return string;
	}

	public static String createVariableForEval(String string, Class<?> ctClass)
	{
		return createVariableForEval(string, resolveCtClass(ctClass));
	}

	public static String createVariableForEval(String string, CtClass ctClass)
	{
		if (ctClass.isInterface())
			return string + " ? " + string + ".node" + " : " + string;
		else
			return string;
	}

	private static CtClass resolveCtClass(Class<?> clazz)
	{
		try
		{
			ClassPool pool= ClassPool.getDefault();
			return pool.get(clazz.getName());
		}
		catch (NotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public byte[] generateBytecode(Class<?> interface1)
	{
		return generateBytecode("JsDelegate" + interface1.getSimpleName(), interface1);
	}

	public byte[] generate(Class<?> interface1)
	{
		try
		{
			String type= interface1.getName();
			String classname= JsCast.createDelegateClassName(type);
			CtClass ctClass= ClassPool.getDefault().getOrNull(classname);

			if (ctClass == null)
			{
				ctClass= generateCtClass(classname, interface1);
			}

			return ctClass.toBytecode();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public InMemoryClasspathFile generateAsClasspathFile(Class<?> class1)
	{
		return new InMemoryClasspathFile(JsCast.createDelegateClassName(class1.getName()), generate(class1));
	}
}