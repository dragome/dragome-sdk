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
package com.dragome.utils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.ContinueReflection;

public class DragomeCallsiteFactory
{
	protected static class InvocationHandlerForLambdas extends AbstractProxyRelatedInvocationHandler
	{
		private final Class<?> class1;
		private final String methodName;
		private final Object[] parameters;
		private Class<?> returnType;
		private String invokeName;
		private String callType;

		protected InvocationHandlerForLambdas(Class<?> class1, String methodName, Object[] parameters, Class<?> returnTypeClass, String invokeName, String callType)
		{
			this.class1= class1;
			this.methodName= methodName;
			this.parameters= parameters;
			this.returnType= returnTypeClass;
			this.invokeName= invokeName;
			this.callType= callType;
		}

		@ContinueReflection
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			setProxy(proxy);

			try
			{
				//				if (returnType.getMethod(method.getName(), method.getParameterTypes()) != null)
				if (!method.getName().equals(invokeName))
					return invokeDefaultMethod(proxy, method, args);

				if ("<init>".equals(methodName))
					return class1.newInstance();

				List<Object> asList= new ArrayList<Object>(Arrays.asList(parameters));
				if (args != null)
					asList.addAll(Arrays.asList(args));

				Method[] methods= class1.getDeclaredMethods();
				Object result= null;
				for (int i= 0; i < methods.length && result == null; i++)
				{
					Method foundMethod= methods[i];
					if (foundMethod.getName().equals(methodName))
					{
						foundMethod.setAccessible(true);

						boolean isInstanceMethod= parameters.length > 0 && isSameClass();

						if ("static".equals(callType))
							isInstanceMethod= !Modifier.isStatic(foundMethod.getModifiers());

						Object obj= null;
						Object[] invocationArgs= asList.toArray();
						if (isInstanceMethod)
						{
							obj= asList.remove(0);
							invocationArgs= asList.toArray();
						}
						result= foundMethod.invoke(obj, invocationArgs);
					}
				}

				return result;
			}
			catch (Exception e1)
			{
				throw new RuntimeException(e1);
			}
		}
		private boolean isSameClass()
		{
			if (!(parameters[0] instanceof Object))
				return false;

			Class<? extends Object> type= parameters[0].getClass();

			if (Proxy.isProxyClass(type))
			{
				InvocationHandler invocationHandler= Proxy.getInvocationHandler(parameters[0]);
				if (invocationHandler instanceof InvocationHandlerForLambdas)
					type= ((InvocationHandlerForLambdas) invocationHandler).returnType;
				else
					return class1.isAssignableFrom(type);
			}

			return type.equals(class1);
		}

		private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws NoSuchMethodException, Throwable
		{
			final Constructor<MethodHandles.Lookup> constructor= MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
			if (!constructor.isAccessible())
				constructor.setAccessible(true);

			final InvocationHandler handler= new InvocationHandler()
			{
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
				{
					final Class<?> declaringClass= method.getDeclaringClass();
					return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
				}
			};

			return handler.invoke(proxy, method, args);
		}
	}

	public static Object create(String className, String invokeName, String returnType, String invokeType, String handle2, Object objects, String callType)
	{
		try
		{
			Class<?> class1= Class.forName(className.replace("/", "."));
			String methodName= handle2.substring(handle2.indexOf(".") + 1, handle2.indexOf("("));

			if (!handle2.startsWith(className + "."))
			{
				class1= Class.forName(handle2.substring(0, handle2.indexOf(".")).replace("/", "."));
				methodName= handle2.substring(handle2.indexOf(".") + 1, handle2.indexOf("("));
			}
			final Object[] parameters= (Object[]) objects;
			Class<?> returnTypeClass= Class.forName(returnType);
			return Proxy.newProxyInstance(DragomeCallsiteFactory.class.getClassLoader(), new Class<?>[] { returnTypeClass }, new InvocationHandlerForLambdas(class1, methodName, parameters, returnTypeClass, invokeName, callType));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
