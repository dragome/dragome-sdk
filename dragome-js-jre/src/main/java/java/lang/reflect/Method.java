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
package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.annotations.DragomeCompilerSettings;
import com.dragome.commons.javascript.ScriptHelper;

@DragomeCompilerSettings(CompilerType.Standard)
public final class Method
{
	protected String signature;
	protected Class<?> cls;
	protected boolean accessible;
	protected Class<?>[] parametersTypes;
	protected Class<?> returnType;
	private int modifiers;

	public Method(Class<?> newCls, String theSignature, int modifiers)
	{
		signature= theSignature;
		cls= newCls;
		this.modifiers= modifiers;
	}

	public String getName()
	{
		String name= signature.substring(1, signature.lastIndexOf("$"));

		int parametersStart= name.indexOf("___");
		if (parametersStart != -1)
			return name.substring(0, parametersStart);
		else
			return name;
	}

	public Class<?>[] getParameterTypes()
	{
		String signatureWithNoReturnType= signature.substring(0, signature.lastIndexOf("$"));
		String[] parameters= signatureWithNoReturnType.replaceAll("____", "__").replaceAll("___", "__").split("__");
		List<Class<?>> result= new ArrayList<Class<?>>();

		if (parameters.length > 1)
		{
			for (int i= 1; i < parameters.length; i++)
			{
				String typeName= parameters[i];
				if (typeName.trim().length() > 0 && !typeName.contains("$"))
				{
					//			typeName= boxTypes(typeName);

					Class<?> parameterType= Object.class;
					try
					{
						parameterType= Class.forName(fixArrayClassName(typeName));
					}
					catch (Exception e)
					{
					}

					result.add(parameterType);
				}
			}
			return result.toArray(new Class[0]);
		}
		else
			return new Class[0];
	}

	public static String boxTypes(String typeName)
	{
		if ("boolean".equals(typeName))
			return "java.lang.Boolean";
		else if ("int".equals(typeName))
			return "java.lang.Integer";
		else if ("long".equals(typeName))
			return "java.lang.Long";
		else if ("short".equals(typeName))
			return "java.lang.Short";
		else if ("float".equals(typeName))
			return "java.lang.Float";
		else if ("double".equals(typeName))
			return "java.lang.Double";
		else if ("byte".equals(typeName))
			return "java.lang.Byte";
		else if ("char".equals(typeName))
			return "java.lang.Character";

		return typeName;
	}

	public Object invoke(Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		boxArguments(args);

		Object result= null;
		if (obj == null)
		{
			ScriptHelper.put("relatedClass", cls, this);
			obj= ScriptHelper.eval("relatedClass.$$$nativeClass", this);
		}

		ScriptHelper.put("obj", obj, this);
		ScriptHelper.put("args", args, this);
		ScriptHelper.put("sig", this.signature, this);

		Object instanceMethod= ScriptHelper.eval("obj[sig]", this);

		if (instanceMethod == null)
			result= ScriptHelper.eval("obj.clazz.constructor[sig](args)", this);
		else
			result= ScriptHelper.eval("obj[sig].apply(obj, args)", this);

		result= adaptResult(result);
		return result;
	}

	private void boxArguments(Object... args)
	{
		ScriptHelper.put("args", args, this);

		Class<?>[] parameterTypes= getParameterTypes();

		for (int i= 0; i < parameterTypes.length; i++)
		{
			ScriptHelper.put("parameterType", parameterTypes[i], this);

			if (args[i] != null)
			{
				if (ScriptHelper.evalBoolean("parameterType.realName == 'boolean'", this) && args[i] instanceof Boolean)
					ScriptHelper.put("argValue", Boolean.parseBoolean(args[i].toString()), this);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'int'", this) && args[i] instanceof Integer)
					ScriptHelper.put("argValue", Integer.parseInt(args[i].toString()), this);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'long'", this) && args[i] instanceof Long)
					ScriptHelper.put("argValue", Long.parseLong(args[i].toString()), this);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'short'", this) && args[i] instanceof Short)
					ScriptHelper.put("argValue", Short.parseShort(args[i].toString()), this);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'float'", this) && args[i] instanceof Float)
					ScriptHelper.put("argValue", Float.parseFloat(args[i].toString()), this);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'double'", this) && args[i] instanceof Double)
					ScriptHelper.put("argValue", Double.parseDouble(args[i].toString()), this);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'byte'", this) && args[i] instanceof Byte)
					ScriptHelper.put("argValue", Byte.parseByte(args[i].toString()), this);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'char'", this) && args[i] instanceof Character)
					ScriptHelper.put("argValue", ((Character) args[i]).charValue(), this);
				else
					ScriptHelper.put("argValue", args[i], this);

				ScriptHelper.put("i", i, this);
				ScriptHelper.eval("args[i]= argValue", this);
			}
		}
	}

	private Object adaptResult(Object result)
	{
		ScriptHelper.put("result", result, this);

		try
		{
			Class<?> currentReturnType= getReturnType();

			if (currentReturnType.equals(Boolean.class))
				result= result instanceof Boolean ? result : (ScriptHelper.evalBoolean("result", this) ? Boolean.TRUE : Boolean.FALSE);
			else if (currentReturnType.equals(Integer.class))
				result= Integer.parseInt(ScriptHelper.evalInt("result", this) + "");
			else if (currentReturnType.equals(Long.class))
				result= Long.parseLong(ScriptHelper.evalInt("result", this) + "");
			else if (currentReturnType.equals(Short.class))
				result= Short.parseShort(ScriptHelper.evalInt("result", this) + "");
			else if (currentReturnType.equals(Float.class))
				result= Float.parseFloat(ScriptHelper.evalFloat("result", this) + "");
			else if (currentReturnType.equals(Double.class))
				result= Double.parseDouble(ScriptHelper.evalDouble("result", this) + "");
			else if (currentReturnType.equals(Byte.class))
				result= Byte.valueOf((byte) ScriptHelper.evalChar("result", this));
			else if (currentReturnType.equals(Character.class))
				result= Character.valueOf((ScriptHelper.eval("result", this) + "").charAt(0));
		}
		catch (Exception e)
		{
		}
		return result;
	}
	public boolean isAnnotationPresent(Class<? extends Annotation> annotation)
	{
		return false; //throw new UnsupportedOperationException();
	}

	public String toString()
	{
		return signature;
	}

	public void setAccessible(boolean accessible)
	{
		this.accessible= accessible;
	}

	public int getModifiers()
	{
		return modifiers;
	}

	public <T> T getAnnotation(Class<T> class1)
	{
		return null;
	}

	public Class<?> getReturnType()
	{
		if (returnType == null)
		{
			try
			{
				String returnTypeString= signature.substring(signature.lastIndexOf("$") + 1);
				returnTypeString= fixArrayClassName(returnTypeString);
				//				returnTypeString= boxTypes(returnTypeString);
				returnType= Class.forName(returnTypeString);
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		}
		return returnType;
	}

	private String fixArrayClassName(String methodName)
	{
		if (methodName.endsWith("[]"))
			methodName= "[L" + methodName.substring(0, methodName.length() - 2) + ";";
		return methodName;
	}

	public Type[] getGenericParameterTypes()
	{
		return getParameterTypes();
	}

	public Class<?> getDeclaringClass()
	{
		return cls;
	}

	public Type getGenericReturnType()
	{
		Class<?> declaringClass= getDeclaringClass();
		ScriptHelper.put("declaringClass", declaringClass, this);
		if (ScriptHelper.evalBoolean("declaringClass.$$$nativeClass.$$$$signatures ", this))
		{
			String genericSignature= (String) ScriptHelper.eval("declaringClass.$$$nativeClass.$$$$signatures[this.$$$signature]", this);
			genericSignature= genericSignature.replaceAll(".*<L", "");
			genericSignature= genericSignature.replaceAll(";>;", "");
			genericSignature= genericSignature.replaceAll("/", "_");

			return new ParameterizedTypeImpl(genericSignature);
		}
		else
			return getReturnType();
	}

	public Object[] boxParameters(Object[] args)
	{
		Class<?>[] parameterTypes= getParameterTypes();
		for (int i= 0; i < parameterTypes.length; i++)
		{
			if (parameterTypes[i].isPrimitive())
			{
				ScriptHelper.put("primitiveValue", args[i], null);
				String stringValue= (String) ScriptHelper.eval("primitiveValue.toString()", this);

				if (parameterTypes[i].equals(int.class))
					args[i]= new Integer(stringValue);
				else if (parameterTypes[i].equals(long.class))
					args[i]= new Long(stringValue);
				else if (parameterTypes[i].equals(float.class))
					args[i]= new Float(stringValue);
				else if (parameterTypes[i].equals(double.class))
					args[i]= new Double(stringValue);
				else if (parameterTypes[i].equals(short.class))
					args[i]= new Short(stringValue);
				else if (parameterTypes[i].equals(char.class))
					args[i]= new Character(stringValue.charAt(0));
				else if (parameterTypes[i].equals(byte.class))
					args[i]= new Byte(stringValue);
				else if (parameterTypes[i].equals(boolean.class))
					args[i]= new Boolean(stringValue);
			}
		}
		return args;
	}
}
