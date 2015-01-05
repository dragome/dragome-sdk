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
import java.util.List;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.javascript.ScriptHelper;

public class Proxy
{
	private InvocationHandler handler;
	private final Class[] interfaces;

	/** 
	 * Constructs a new Proxy instance.
	 */
	protected Proxy(InvocationHandler theHandler, Class[] interfaces)
	{
		handler= theHandler;
		this.interfaces= interfaces;
	}

	// TODO: Annotation
	//@Taint
	public static Object invoke(Proxy object, String methodSignature, Object[] args) throws Throwable
	{
		if (methodSignature.startsWith("getClass()"))
			return Proxy.class;
		else
		{
			Method method= new Method(null, methodSignature, Modifier.PUBLIC);
			return object.handler.invoke(object, method, args);
		}
	}

	/**
	 * Returns an instance of a proxy class for the specified interfaces
	 * that dispatches method invocations to the specified invocation handler.
	 */
	public static Object newProxyInstance(ClassLoader loader, Class[] interfaces, final InvocationHandler handler) throws IllegalArgumentException
	{
		List<Method> methods= new ArrayList<Method>();

		for (Class interfaze : interfaces)
			methods.addAll(Arrays.asList(interfaze.getMethods()));

		Method[] objectClassMethods= Object.class.getMethods();
		for (Method method : objectClassMethods)
		{
			if (method.getName().equals("equals") || method.getName().equals("hashCode"))
				methods.add(method);
		}

		InvocationHandler handler2= new AbstractProxyRelatedInvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				setProxy(proxy);
				
				ScriptHelper.put("method", method, this);
				String methodName= (String) ScriptHelper.eval("method.$$$signature", this);
				Class declaringClass= (Class) ScriptHelper.eval("method.$$$cls", this);

				method.boxParameters(args);

				Method method2= new Method(declaringClass, methodName, Modifier.PUBLIC);
				Class returnType= method.getReturnType();
				boolean isPrimitive= returnType != null && returnType.isPrimitive();

				Object result= handler.invoke(proxy, method2, args);

				if (isPrimitive)
					result= BoxingHelper.convertObjectToPrimitive(result);

				return result;
			}
		};

		ScriptHelper.put("interfaces", interfaces, null);
		ScriptHelper.put("handler1", handler, null);
		ScriptHelper.put("handler2", handler2, null);
		ScriptHelper.put("methods", methods, null);

		Object result= ScriptHelper.eval("createProxyOf(interfaces, methods, handler1, handler2)", null);

		return result;
		//
		//	return new Proxy(handler, interfaces);
	}

	public static boolean isProxyClass(Class class1)
	{
		ScriptHelper.put("class1", class1, null);

		return ScriptHelper.evalBoolean("class1 && class1.$$$nativeClass && class1.$$$nativeClass.classname.startsWith(\"ProxyOf_\")", null);
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
