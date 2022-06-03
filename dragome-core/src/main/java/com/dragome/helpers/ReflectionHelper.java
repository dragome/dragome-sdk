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
package com.dragome.helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

public class ReflectionHelper
{
	public static Object invokeMethod(Method method, Object object, Object[] args)
	{
		try
		{
			return method.invoke(object, args);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String uncapitalise(String str)
	{
		int strLen;
		if (str == null || (strLen= str.length()) == 0)
			return str;

		return new StringBuffer(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
	}

	public static String capitalise(String str)
	{
		if (str == null)
			return null;
		if (str.length() == 0)
			return "";
		else
			return (new StringBuffer(str.length())).append(str.substring(0, 1).toUpperCase()).append(str.substring(1)).toString();
	}

	public static <T> T createClassInstance(Class<? extends T> aClass)
	{
		return (T) createClassInstance(aClass.getName());
	}

	public static Object createClassInstance(String aClassName)
	{
		try
		{
			if (aClassName.length() > 0)
				return Class.forName(aClassName).newInstance();
			else
				return null;
		}

		catch (ClassNotFoundException e)
		{
			throw new RuntimeException("Cannot create an instance of '" + aClassName + "', class not found", e);
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException("Cannot create an instance of '" + aClassName + "', trying to instantiate an interface	or an object that does not have a default constructor ", e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException("Cannot create an instance of '" + aClassName + "', illegal access", e);
		}
	}

	public static Object[] createObjectArray(String[] split)
	{
		Object[] result= new Object[split.length];
		for (int i= 0; i < split.length; i++)
			result[i]= split[i];

		return result;
	}

	public static List<Field> getAllFields(Class<?> aClass)
	{
		List<Field> fields= new Vector<Field>();

		do
		{
			Field[] declaredFields= aClass.getDeclaredFields();
			for (int i= 0; i < declaredFields.length; i++)
				fields.add(declaredFields[i]);
		}
		while ((aClass= aClass.getSuperclass()) != null);

		return fields;
	}

	public static List<Field> getAllFields(Object anObject)
	{
		Class<?> aClass= anObject.getClass();
		return getAllFields(aClass);
	}

	public static List<Method> getGetters(Object anObject, boolean excludereadonly)
	{
		Class<?> aClass= anObject.getClass();
		return getGettersImpl(aClass, excludereadonly);
	}

	public static List<BeanProperty> createEntityProperties(Object entity)
	{
		return getAllGetters(entity).stream().map(m -> new BeanProperty(m, entity)).collect(Collectors.toList());
	}
	
	public static List<BeanProperty> createEntityProperties(Class entityType)
	{
		return getAllGetters(entityType).stream().map(m -> new BeanProperty(m, null)).collect(Collectors.toList());
	}

	public static List<Method> getAllGetters(Class<?> aClass)
	{
		Set<Method> getters= new HashSet<Method>();
		do
		{
			Method[] methods= aClass.getMethods();
			for (int i= 0; i < methods.length; i++)
			{
				Method method= methods[i];

				if (method.getParameterTypes().length == 0 && ((isGetter(method) && getSetterImpl(aClass, method.getName().substring(3)) != null) || (method.getName().startsWith("is") && getSetterImpl(aClass, method.getName().substring(2)) != null)))
					getters.add(method);
			}
		}
		while ((aClass= aClass.getSuperclass()) != null);

		List<Method> result= new ArrayList<>(getters);

		Collections.sort(result, (m1, m2) -> m1.getName().compareTo(m2.getName()));

		return result;
	}

	public static List<Method> getGettersImpl(Class<?> aClass, boolean excludereadonly)
	{
		List<Method> getters= new Vector<Method>();
		do
		{
			Method[] declaredMethods= aClass.getDeclaredMethods();
			for (int i= 0; i < declaredMethods.length; i++)
			{
				Method method= declaredMethods[i];
				if ((method.getModifiers() & java.lang.reflect.Modifier.PUBLIC) == 0)
					continue;

				if (method.getParameterTypes().length == 0 && isGetter(method))
				{
					if (!excludereadonly)
						getters.add(method);
					else if (((getSetterImpl(aClass, method.getName().substring(3)) != null) || (method.getName().startsWith("is") && getSetterImpl(aClass, method.getName().substring(2)) != null)))
						getters.add(method);
				}
			}
		}
		while ((aClass= aClass.getSuperclass()) != null);

		return getters;
	}

	public static List<Method> getAllGetters(Object anObject)
	{
		return getGetters(anObject, true);
	}

	public static List<Method> getReadGetters(Object object)
	{
		return getGetters(object, false);
	}

	public static List<Method> getAllMethods(Object anObject)
	{
		return getAllMethods(anObject.getClass());
	}

	public static Method getGetterImpl(Class clazz, String aPropertyName)
	{
		try
		{
			return clazz.getMethod("get" + ReflectionHelper.capitalise(aPropertyName), null);
		}
		catch (Exception e)
		{
			try
			{
				return clazz.getMethod("is" + ReflectionHelper.capitalise(aPropertyName), null);
			}
			catch (Exception e1)
			{
				return null;
			}
		}
	}

	public static Method getGetter(final Object anObject, String aPropertyName)
	{
		return getGetterImpl(anObject.getClass(), aPropertyName);
	}

	//    public static String getNoPackageClassName(Class aClass)
	//    {
	//	String t= aClass.getName();
	//	return t.substring(t.lastIndexOf(".") + 1);
	//    }

	public static String getNoPackageClassName(Object anObject)
	{
		return getNoPackageClassName(anObject.getClass());
	}

	//    public static List<MethodInvoker> getNoParamsGetterMethodInvokers(List instances)
	//    {
	//	List<MethodInvoker> result= new Vector<MethodInvoker>();
	//	for (Iterator i= instances.iterator(); i.hasNext();)
	//	{
	//	    ValueHolder object= (ValueHolder) i.next();
	//
	//	    for (Iterator i2= getAllGetters(object.getValue()).iterator(); i2.hasNext();)
	//	    {
	//		Method method= (Method) i2.next();
	//
	//		if (method.getParameterTypes().length == 0)
	//		    result.add(new MethodInvoker(object, method));
	//	    }
	//	}
	//
	//	return result;
	//    }

	public static List<String> getProperties(Object anObject)
	{
		List<String> result= new Vector<String>();

		if (anObject != null)
		{
			for (Iterator i= getAllGetters(anObject).iterator(); i.hasNext();)
			{
				Method getter= (Method) i.next();
				String propertyName= getter.getName().startsWith("get") ? getter.getName().substring(3) : getter.getName().substring(2);
				result.add(propertyName);
			}
		}

		return result;
	}

	public static String getPropertyName(Method aMethod)
	{
		if (isGetter(aMethod) || isSetter(aMethod))
			return aMethod.getName().substring((aMethod.getReturnType().equals(boolean.class) && isGetter(aMethod)) ? 2 : 3);
		else
			return null;
	}

	public static Object getPropertyValue(final Object anObject, String aPropertyName)
	{
		try
		{
			return getGetter(anObject, aPropertyName).invoke(anObject, null);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static List<String> getReadOnlyProperties(Object anObject)
	{
		List<String> result= new Vector<String>();

		if (anObject != null)
		{
			for (Iterator<Method> i= getAllMethods(anObject).iterator(); i.hasNext();)
			{
				Method getter= i.next();

				String propertyName= null;
				if (getter.getName().startsWith("get"))
					propertyName= getter.getName().substring(3);
				else if (getter.getName().startsWith("is"))
					propertyName= getter.getName().substring(2);

				if (propertyName != null)
					result.add(propertyName);
			}
		}

		return result;
	}

	public static Method getSetter(final Object anObject, String aPropertyName)
	{
		Class<? extends Object> type= anObject.getClass();
		return getSetterImpl(type, aPropertyName);
	}

	public static Method getSetterImpl(Class<? extends Object> type, String aPropertyName)
	{
		try
		{
			Method getter= getGetterImpl(type, aPropertyName);

			return getter != null ? type.getMethod("set" + ReflectionHelper.capitalise(aPropertyName), new Class[] { getter.getReturnType() }) : null;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static boolean isGetter(Method aMethod)
	{
		return aMethod.getParameterTypes().length == 0 && (aMethod.getName().startsWith("get") || aMethod.getName().startsWith("is"));
	}

	public static boolean isSetter(Method aMethod)
	{
		return aMethod.getParameterTypes().length == 0 && aMethod.getName().startsWith("set");
	}

	public static void setPropertyValue(final Object anObject, String aPropertyName, Object aValue)
	{
		try
		{
			Method setter= getSetter(anObject, aPropertyName);
			setter.invoke(anObject, new Object[] { aValue });
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static Class<?> forName(String aClassName)
	{
		try
		{
			return Class.forName(aClassName);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	//    public static String getMethodId(Method aMethod)
	//    {
	//	String methodId= aMethod.getDeclaringClass().getName().replaceAll("\\.", "_") + "$" + aMethod.getName();
	//	for (Class<?> parameterType : aMethod.getParameterTypes())
	//	    methodId+= "$" + parameterType.getName().replaceAll("\\.", "_");
	//
	//	return methodId;
	//    }

	public static Method getMethodFromId(String aMethodId)
	{
		try
		{
			String[] parts= aMethodId.split("\\$");
			Class<?>[] parameterType= new Class<?>[parts.length - 2];
			for (int i= 2; i < parts.length; i++)
				parameterType[i - 2]= forName(parts[i].replaceAll("_", "."));

			return forName(parts[0].replaceAll("_", ".")).getMethod(parts[1], parameterType);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static List<Constructor<Class<?>>> getAllConstructors(Class aClass)
	{
		List<Constructor<Class<?>>> methods= new ArrayList<Constructor<Class<?>>>();

		Constructor<Class<?>>[] declaredMethods= aClass.getDeclaredConstructors();
		for (int i= 0; i < declaredMethods.length && declaredMethods[i].getDeclaringClass().toString().indexOf("java.lang.reflect.Proxy") == -1; i++)
			methods.add(declaredMethods[i]);

		return methods;
	}

	public static Set<Class<?>> getAllInterfaces(Class<?> aClass)
	{
		Class<?> currentClass= aClass;

		Set<Class<?>> result= new HashSet<Class<?>>();

		result= new LinkedHashSet<Class<?>>();

		while (currentClass != null)
		{
			result.add(currentClass);
			List<Class<?>> interfaces= Arrays.asList(currentClass.getInterfaces());
			result.addAll(interfaces);
			for (int i= 0; i < interfaces.size(); i++)
				result.addAll(getAllInterfaces(interfaces.get(i)));

			currentClass= currentClass.getSuperclass();
		}

		result.add(Object.class);

		return result;
	}

	public static List<Method> getAllMethods(Class aClass)
	{
		List<Method> methods= new Vector<Method>();

		do
		{
			Method[] declaredMethods= aClass.getDeclaredMethods();
			for (int i= 0; i < declaredMethods.length && declaredMethods[i].getDeclaringClass().toString().indexOf("java.lang.reflect.Proxy") == -1; i++)
				methods.add(declaredMethods[i]);
		}
		while ((aClass= aClass.getSuperclass()) != null);

		return methods;
	}

	public static Class<?> getRawType(Type result)
	{
		if (result instanceof ParameterizedType)
			result= ((ParameterizedType) result).getRawType();

		return (Class<?>) result;
	}

	public static boolean isMethodEquals(Method method1, Method method2)
	{
		if (method2.getName().equals(method1.getName()))
		{
			List<Class<?>> asList= Arrays.asList(method2.getParameterTypes());
			List<Class<?>> asList2= Arrays.asList(method1.getParameterTypes());
			if (asList.equals(asList2))
				return true;
		}
		return false;
	}
}
