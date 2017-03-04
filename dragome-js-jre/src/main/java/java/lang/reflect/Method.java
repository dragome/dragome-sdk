/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.annotations.DragomeCompilerSettings;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.annotations.ClientSideMethod;
import com.dragome.web.debugging.JavascriptReference;
import com.dragome.web.dispatcher.JavaRefId;
import com.dragome.web.enhancers.jsdelegate.JsCast;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

@DragomeCompilerSettings(CompilerType.Standard)
public final class Method extends Executable
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
		boxArguments(getParameterTypes(), args);

		Object result= null;
		if (obj == null)
		{
			ScriptHelper.put("relatedClass", cls, this);
			obj= ScriptHelper.eval("relatedClass.$$$nativeClass___java_lang_Object", this);
		}

		ScriptHelper.put("obj", obj, this);
		ScriptHelper.put("args", args, this);
		ScriptHelper.put("sig", this.signature, this);

		Object instanceMethod= ScriptHelper.eval("obj[sig]", this);

		if (instanceMethod == null)
			result= ScriptHelper.eval("obj.clazz.constructor[sig](args)", this);
		else
			result= ScriptHelper.eval("obj[sig].apply(obj, args)", this);

		result= adaptResult(result, getReturnType());
		return result;
	}

	@MethodAlias(local_alias= "apply")
	public Object javaCall(Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		WebServiceLocator webServiceLocator= WebServiceLocator.getInstance();
		boolean executeAtServer= webServiceLocator.isRemoteDebugging() && getAnnotation(ClientSideMethod.class) == null;

		Class<?>[] parameterTypes= getParameterTypes();
		Object[] typedArguments= new Object[parameterTypes.length];

		for (int j= 0; j < args.length; j++)
		{
			Object object= args[j];
			Object typedArgument= JsCast.castTo(object, parameterTypes[j]);
			typedArguments[j]= typedArgument;
			//			ScriptHelper.put("a", object, this);
			//			ScriptHelper.eval("console.log(a)", this);
		}

		if (executeAtServer)
		{
			ScriptHelper.put("obj", obj, this);
			String javaRefIdString= (String) ScriptHelper.eval("obj.javaRefId", this);

			JavaRefId javaRefId= new JavaRefId(javaRefIdString);

			List<Object> typedParametersRefs= new ArrayList<>();

			for (int i= 0; i < typedArguments.length; i++)
			{
				if (parameterTypes[i].isInterface())
				{
					String id= DragomeEntityManager.add(typedArguments[i]);
					typedParametersRefs.add(new JavascriptReference(parameterTypes[i].getName(), id));
				}
				else
					typedParametersRefs.add(args[i]);
			}

			ServiceInvocation serviceInvocation= new ServiceInvocation(getDeclaringClass(), this, typedParametersRefs, javaRefIdString);

			webServiceLocator.getEventDispatcher().callJavaMethod(serviceInvocation);
			return null;
		}
		else
			return invoke(obj, typedArguments);
	}

	public static void boxArguments(Class<?>[] parameterTypes, Object... args)
	{
		ScriptHelper.put("args", args, null);

		for (int i= 0; i < parameterTypes.length; i++)
		{
			ScriptHelper.put("parameterType", parameterTypes[i], null);

			if (args[i] != null)
			{
				if (ScriptHelper.evalBoolean("parameterType.realName == 'boolean'", null) && args[i] instanceof Boolean)
					ScriptHelper.put("argValue", Boolean.parseBoolean(args[i].toString()), null);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'int'", null) && args[i] instanceof Integer)
					ScriptHelper.put("argValue", Integer.parseInt(args[i].toString()), null);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'long'", null) && args[i] instanceof Long)
					ScriptHelper.put("argValue", Long.parseLong(args[i].toString()), null);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'short'", null) && args[i] instanceof Short)
					ScriptHelper.put("argValue", Short.parseShort(args[i].toString()), null);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'float'", null) && args[i] instanceof Float)
					ScriptHelper.put("argValue", Float.parseFloat(args[i].toString()), null);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'double'", null) && args[i] instanceof Double)
					ScriptHelper.put("argValue", Double.parseDouble(args[i].toString()), null);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'byte'", null) && args[i] instanceof Byte)
					ScriptHelper.put("argValue", Byte.parseByte(args[i].toString()), null);
				else if (ScriptHelper.evalBoolean("parameterType.realName == 'char'", null) && args[i] instanceof Character)
					ScriptHelper.put("argValue", ((Character) args[i]).charValue(), null);
				else
					ScriptHelper.put("argValue", args[i], null);

				ScriptHelper.put("i", i, null);
				ScriptHelper.eval("args[i]= argValue", null);
			}
		}
	}

	public static Object adaptResult(Object result, Class<?> returnType)
	{
		ScriptHelper.put("result", result, null);

		try
		{
			Class<?> currentReturnType= returnType;

			if (currentReturnType != null)
				if (currentReturnType.equals(Boolean.class))
					result= result instanceof Boolean ? result : (ScriptHelper.evalBoolean("result", null) ? Boolean.TRUE : Boolean.FALSE);
				else if (currentReturnType.equals(Integer.class))
					result= Integer.parseInt(ScriptHelper.evalInt("result", null) + "");
				else if (currentReturnType.equals(Long.class))
					result= Long.parseLong(ScriptHelper.evalInt("result", null) + "");
				else if (currentReturnType.equals(Short.class))
					result= Short.parseShort(ScriptHelper.evalInt("result", null) + "");
				else if (currentReturnType.equals(Float.class))
					result= Float.parseFloat(ScriptHelper.evalFloat("result", null) + "");
				else if (currentReturnType.equals(Double.class))
					result= Double.parseDouble(ScriptHelper.evalDouble("result", null) + "");
				else if (currentReturnType.equals(Byte.class))
					result= Byte.valueOf((byte) ScriptHelper.evalChar("result", null));
				else if (currentReturnType.equals(Character.class))
					result= Character.valueOf((ScriptHelper.eval("result", null) + "").charAt(0));
		}
		catch (Exception e)
		{
			System.out.println("adaptResult failed");
		}
		return result;
	}
	public boolean isAnnotationPresent(Class<? extends Annotation> annotation)
	{
		return getAnnotation(annotation) != null;
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

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
	{
		return Class.getAnnotationInternal(getDeclaringClass(), annotationClass, getName(), null, null);
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
		if (methodName.endsWith("_ARRAYTYPE"))
			methodName= methodName.replace("_ARRAYTYPE", "[]");

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
		if (ScriptHelper.evalBoolean("declaringClass.$$$nativeClass___java_lang_Object.$$$$signatures ", this))
		{
			String genericSignature= (String) ScriptHelper.eval("declaringClass.$$$nativeClass___java_lang_Object.$$$$signatures[this.$$$signature___java_lang_String]", this);

			return new ParameterizedTypeImpl(genericSignature.substring(genericSignature.indexOf(")") + 1));
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

	public Parameter[] getParameters()
	{
		List<Parameter> result= new ArrayList<>();

		Class<?>[] parameterTypes= getParameterTypes();
		for (int i= 0; i < parameterTypes.length; i++)
			result.add(new Parameter(getDeclaringClass(), this, "arg" + i, 0, i));

		return result.toArray(new Parameter[0]);
	}

	public boolean isSynthetic()
	{
		return false;
	}

	public TypeVariable<?>[] getTypeParameters()
	{
		return new TypeVariable[0];
	}

	public Annotation[][] getParameterAnnotations()
	{
		return new Annotation[0][0];
	}

	public boolean isBridge()
	{
		return (getModifiers() & Modifier.BRIDGE) != 0;
	}

	public String toGenericString()
	{
		return toString();
	}
}
