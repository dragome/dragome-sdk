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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.javascript.ScriptHelper;

public class Proxy
{
	private static Map<String, Method> cachedMethods= new HashMap<>();
	private static Map<String, String> proxyClassesByName= new HashMap<>();
	
	private InvocationHandler handler;

	public static class MethodInvoker
	{
		public Method method;
		private boolean unboxReturnValue;

		public MethodInvoker(List<Method> interfacesMethods, Method method)
		{
			this.method= method;
			method= findMethod(interfacesMethods);
			Class<?> returnType= method.getReturnType();
			boolean isPrimitive= returnType != null && returnType.isPrimitive();
			unboxReturnValue= isPrimitive && !returnType.equals(Void.class) && !returnType.equals(void.class);
		}

		private Method findMethod(List<Method> interfacesMethods)
		{
			Method method2= cachedMethods.get(method.getSignature());
			if (method2 == null)
			{
				for (Method interfaceMethod : interfacesMethods)
				{
					if (method.getName().equals(interfaceMethod.getName()))
					{
						List<Class<?>> asList= Arrays.asList(method.getParameterTypes());
						List<Class<?>> asList2= Arrays.asList(interfaceMethod.getParameterTypes());
						if (asList.equals(asList2))
							method= interfaceMethod;
					}
				}

				cachedMethods.put(method.getSignature(), method);
				method2= method;
			}
			return method2;
		}
	}

	
	public static Object newProxyInstance(ClassLoader loader, Class[] interfaces, final InvocationHandler aHandler) throws IllegalArgumentException
	{
		StringBuilder keyBuilder= new StringBuilder();
		for (Class interfaze : interfaces)
			keyBuilder.append(interfaze.getName());
		String key= keyBuilder.toString();

		String proxyClassName= proxyClassesByName.get(key);
		if (proxyClassName == null)
		{
			proxyClassName= createProxyClass(interfaces, aHandler, key);
			proxyClassesByName.put(key, proxyClassName);
		}

		Object result= ScriptHelper.eval("new " + proxyClassName + "()", null);
		ScriptHelper.eval("aHandler.proxy= result", null);
		ScriptHelper.eval("result.$$$handler___java_lang_reflect_InvocationHandler= aHandler", null);

		return result;
	}

	private static String createProxyClass(Class[] interfaces, final InvocationHandler aHandler, String key)
	{
		List<Method> methods= new ArrayList<Method>();

		for (Class interfaze : interfaces)
		{
			if (!interfaze.isInterface())
				throw new IllegalArgumentException(interfaze.getName() + " is not an interface");

			List<Method> foundMethods= interfaze.internalGetMethods(false);
			methods.addAll(foundMethods);
		}

		List<Method> interfacesMethods= new ArrayList<Method>();
		for (Class interfaze : interfaces)
		{
			List<Method> foundMethods= interfaze.internalGetMethods(true);
			interfacesMethods.addAll(foundMethods);
		}

		addObjectClassMethods(methods);

		List<MethodInvoker> methodInvokers= new ArrayList<>();

		for (Method method : methods)
			methodInvokers.add(new MethodInvoker(interfacesMethods, method));

		ScriptHelper.put("interfaces", interfaces, null);
		ScriptHelper.put("handler1", aHandler, null);
		ScriptHelper.put("methods", methodInvokers.toArray(), null);

		return (String) ScriptHelper.eval("createProxyOf(interfaces, methods)", null);
	}

	private static void addObjectClassMethods(List<Method> methods)
	{
		Method[] objectClassMethods= Object.class.getMethods();
		for (Method method : objectClassMethods)
		{
			if (method.getName().equals("equals") || method.getName().equals("hashCode") || method.getName().equals("toString"))
				methods.add(method);
		}
	}

	public static boolean isProxyClass(Class class1)
	{
		ScriptHelper.put("class1", class1, null);
		return ScriptHelper.evalBoolean("class1 && class1.$$$nativeClass___java_lang_Object && class1.$$$nativeClass___java_lang_Object.classname.startsWith(\"ProxyOf_\")", null);
	}

	public static InvocationHandler getInvocationHandler(Object proxy) throws IllegalArgumentException
	{
		return ((Proxy) proxy).handler;
	}

	public static Object invokeStatic(InvocationHandler invocationHandler, Object proxy, Method method, Object[] args) throws Throwable
	{
		return invocationHandler.invoke(proxy, method, args);
	}

}
