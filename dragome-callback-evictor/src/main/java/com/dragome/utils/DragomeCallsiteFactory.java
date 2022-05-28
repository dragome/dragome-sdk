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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.ContinueReflection;

import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.scope.ClassScope;

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

		protected InvocationHandlerForLambdas(Class<?> class1, String methodName, Object[] parameters, Class<?> returnTypeClass, String invokeName, String callType, Class[] parametersTypes)
		{
			this.class1= class1;
			this.methodName= methodName;
			this.parameters= parameters;
			this.returnType= returnTypeClass;
			this.invokeName= invokeName;
			init(callType, parametersTypes);
		}

		public InvocationHandlerForLambdas(Class<?> class1, String methodName, Object[] parameters, Class<?> returnTypeClass, String invokeName, Method foundMethod, boolean isInstanceMethod)
		{
			this.class1= class1;
			this.methodName= methodName;
			this.parameters= parameters;
			this.returnType= returnTypeClass;
			this.invokeName= invokeName;
			this.foundMethod= foundMethod;
			this.isInstanceMethod= isInstanceMethod;
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
				Object calcObj;
				if (isInstanceMethod)
					calcObj= parameters.length > 0 ? parameters[0] : args[0];
				else
					calcObj= null;
				Method foundMethod2= foundMethod;
				args= new Object[0];
				method= null;
				proxy= null;
				Object invoke;

				if (isInstanceMethod)
					invoke= foundMethod2.invoke(calcObj, createInvocationArgs);
				else
				{
					StaticMethodInvokerForLambdas staticMethodInvokerForLambdas= new StaticMethodInvokerForLambdas(foundMethod2, createInvocationArgs);
					invoke= staticMethodInvokerForLambdas.invoke();
				}

				//				StackRecorder.get().pushReference(this);

				return invoke;
			}
			catch (Exception e1)
			{
				throw new RuntimeException(e1);
			}
		}

		private void init(String callType, Class[] parametersTypes)
		{
			Method[] methods= class1.getDeclaredMethods();

			for (int i= 0; i < methods.length; i++)
			{
				foundMethod= methods[i];

				if (foundMethod.getName().equals(methodName))
				{
					Class<?>[] foundParameterTypes= foundMethod.getParameterTypes();
					if (Arrays.equals(foundParameterTypes, parametersTypes))
					{
						isInstanceMethod= parameters.length > 0 && calcIsSameClass();

						if ("static".equals(callType))
							isInstanceMethod= !Modifier.isStatic(foundMethod.getModifiers());

						foundMethod.setAccessible(true);
						return;
					}
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
		private boolean calcIsSameClass()
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

		public String toString()
		{
			return foundMethod.toString().replace("private ", "").replace("public ", "");
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
			this.foundMethod= foundMethod;
		}

		public boolean isInstanceMethod()
		{
			return isInstanceMethod;
		}

		public void setInstanceMethod(boolean isInstanceMethod)
		{
			this.isInstanceMethod= isInstanceMethod;
		}
	}

	private static Map<String, Object> callsites= new HashMap<>();

	public static Object create(String data, Object objects)
	{
		try
		{
			Object proxy= callsites.get(data);

			if (proxy == null)
			{
				String[] split= data.split("#");
				String className= split[0];
				String invokeName= split[1];
				String returnType= split[2];
				String invokeType= split[3];
				String callType= split[4];
				String handle2= split[5];

				CoreReflectionFactory reflectionFactory= CoreReflectionFactory.make(DragomeCallsiteFactory.class, ClassScope.make(DragomeCallsiteFactory.class));
				String signature= handle2.substring(handle2.indexOf("("));
				ConstructorRepository constructorRepository= ConstructorRepository.make(signature, reflectionFactory);
				Type[] parametersTypesArray= constructorRepository.getParameterTypes();

				Class<?>[] parametersTypes= new Class<?>[parametersTypesArray.length];
				for (int i= 0; i < parametersTypesArray.length; i++)
				{
					Type type= parametersTypesArray[i];
					parametersTypes[i]= (Class<?>) type;
				}

				int indexOfDot= handle2.indexOf(".");
				int indexOfBrackets= handle2.indexOf("(");
				String methodName= handle2.substring(indexOfDot + 1, indexOfBrackets);

				Class<?> class1;
				if (!handle2.startsWith(className + "."))
					class1= Class.forName(handle2.substring(0, indexOfDot).replace("/", "."));
				else
					class1= Class.forName(className.replace("/", "."));

				final Object[] parameters= (Object[]) objects;
				Class<?> returnTypeClass= Class.forName(returnType);
				Class<?>[] interfaces= new Class<?>[] { returnTypeClass };
				InvocationHandlerForLambdas invocationHandlerForLambdas= new InvocationHandlerForLambdas(class1, methodName, parameters, returnTypeClass, invokeName, callType, parametersTypes);
				invocationHandlerForLambdas.setupInterfaces(interfaces);
				proxy= Proxy.newProxyInstance(DragomeCallsiteFactory.class.getClassLoader(), interfaces, invocationHandlerForLambdas);
				callsites.put(data, proxy);
			}
			else
			{
				InvocationHandlerForLambdas invocationHandlerForLambdas= (InvocationHandlerForLambdas) Proxy.getInvocationHandler(proxy);
				InvocationHandlerForLambdas invocationHandlerForLambdas2= new InvocationHandlerForLambdas(invocationHandlerForLambdas.class1, invocationHandlerForLambdas.methodName, (Object[]) objects, invocationHandlerForLambdas.returnType, invocationHandlerForLambdas.invokeName, invocationHandlerForLambdas.foundMethod, invocationHandlerForLambdas.isInstanceMethod);
				invocationHandlerForLambdas2.setupInterfaces(new Class<?>[] { invocationHandlerForLambdas.returnType });
				proxy= Proxy.newProxyInstance(DragomeCallsiteFactory.class.getClassLoader(), invocationHandlerForLambdas.obtainInterfaces(), invocationHandlerForLambdas2);
			}
			return proxy;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
