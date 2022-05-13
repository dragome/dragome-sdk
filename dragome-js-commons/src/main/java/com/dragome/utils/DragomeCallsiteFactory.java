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
		private Class<?> class1;
		private String methodName;
		private Object[] parameters;
		private Class<?> returnType;
		private String invokeName;
		private Method foundMethod;
		private boolean isInstanceMethod;

		public InvocationHandlerForLambdas()
		{
		}

		protected InvocationHandlerForLambdas(Class<?> class1, String methodName, Object[] parameters, Class<?> returnTypeClass, String invokeName, String callType)
		{
			this.class1= class1;
			this.methodName= methodName;
			this.parameters= parameters;
			this.returnType= returnTypeClass;
			this.invokeName= invokeName;
			init(callType);
		}

		@ContinueReflection
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			setProxy(proxy);

			try
			{
				//				if (returnType.getMethod(method.getName(), method.getParameterTypes()) != null)
				if (method.getName().equals("hashCode"))
					return foundMethod.hashCode();
				if (!method.getName().equals(invokeName))
					return invokeDefaultMethod(proxy, method, args);
				if ("<init>".equals(methodName))
					return class1.newInstance();

				Object[] createInvocationArgs= createInvocationArgs(args);
				Object calcObj= isInstanceMethod ? parameters.length > 0 ? parameters[0] : args[0] : null;
				Method foundMethod2= foundMethod;
				args= new Object[0];
				method= null;
				proxy= null;
				Object invoke= foundMethod2.invoke(calcObj, createInvocationArgs);
				return invoke;
			}
			catch (Exception e1)
			{
				throw new RuntimeException(e1);
			}
		}

		private void init(String callType)
		{
			Method[] methods= class1.getDeclaredMethods();

			for (int i= 0; i < methods.length; i++)
			{
				setFoundMethod(methods[i]);
				if (getFoundMethod().getName().equals(methodName))
				{
					isInstanceMethod= parameters.length > 0 && isSameClass();

					if ("static".equals(callType))
						isInstanceMethod= !Modifier.isStatic(getFoundMethod().getModifiers());

					getFoundMethod().setAccessible(true);
					return;
				}
			}
		}

		private Object[] createInvocationArgs(Object[] args)
		{
			List<Object> asList= new ArrayList<Object>(Arrays.asList(parameters));
			if (args != null)
				asList.addAll(Arrays.asList(args));
			Object[] invocationArgs= asList.toArray();

			if (isInstanceMethod)
			{
				asList.remove(0);
				invocationArgs= asList.toArray();
			}
			return invocationArgs;
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

		public Class<?> getReturnType()
		{
			return returnType;
		}

		public void setReturnType(Class<?> returnType)
		{
			this.returnType= returnType;
		}

		public String getInvokeName()
		{
			return invokeName;
		}

		public void setInvokeName(String invokeName)
		{
			this.invokeName= invokeName;
		}

		public Class<?> getClass1()
		{
			return class1;
		}

		public String getMethodName()
		{
			return methodName;
		}

		public List<Object> getParameters()
		{
			return Arrays.asList(parameters);
		}

		public void setClass1(Class<?> class1)
		{
			this.class1= class1;
		}

		public void setMethodName(String methodName)
		{
			this.methodName= methodName;
		}

		public void setParameters(List<Object> parameters)
		{
			this.parameters= parameters.toArray();
		}

		public Method getFoundMethod()
		{
			return foundMethod;
		}

		public void setFoundMethod(Method foundMethod)
		{
			this.foundMethod = foundMethod;
		}

		public boolean isInstanceMethod()
		{
			return isInstanceMethod;
		}

		public void setInstanceMethod(boolean isInstanceMethod)
		{
			this.isInstanceMethod = isInstanceMethod;
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
			Class<?>[] interfaces= new Class<?>[] { returnTypeClass };
			InvocationHandlerForLambdas invocationHandlerForLambdas= new InvocationHandlerForLambdas(class1, methodName, parameters, returnTypeClass, invokeName, callType);
			invocationHandlerForLambdas.setInterfaces(Arrays.asList(interfaces));
			return Proxy.newProxyInstance(DragomeCallsiteFactory.class.getClassLoader(), interfaces, invocationHandlerForLambdas);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
