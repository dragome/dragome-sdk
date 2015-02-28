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

package com.dragome.web.enhancers.jsdelegate.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.dragome.commons.javascript.ScriptHelper;

public class JsDelegateInvocationHandler implements InvocationHandler
{
	private JsDelegateInitializer initialiazer;
	private boolean initialized= false;

	public JsDelegateInvocationHandler(JsDelegateInitializer initialiazer)
	{
		this.initialiazer= initialiazer;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		checkInitialized(proxy);

		ScriptHelper.put("proxy", proxy, this);

		int i= 0;
		StringBuilder invocation= new StringBuilder("proxy.jsDelegate." + method.getName() + "(");

		if (args != null)
			for (Object argument : args)
			{
				Object value= argument;
				if (!argument.getClass().isInterface())
				{
					if (!Proxy.isProxyClass(argument.getClass()))
					{
						value= argument.toString();
					}
					else
					{
						JsDelegateInvocationHandler invocationHandler= (JsDelegateInvocationHandler) Proxy.getInvocationHandler(argument);
						invocationHandler.checkInitialized(argument);
						ScriptHelper.put("delegate", argument, this);
						value= ScriptHelper.eval("delegate.jsDelegate", this);
					}
				}

				String argName= "arg_" + i;
				ScriptHelper.put(argName, value, this);

				if (i++ > 0)
					invocation.append(", ");

				invocation.append(argName);
			}

		invocation.append(")");

		String string;
		if (method.getName().startsWith("set") && args.length == 1)
			string= "proxy.jsDelegate." + method.getName().toLowerCase().charAt(3) + method.getName().substring(4) + "= arg_0";
		else
			string= invocation.toString();

		Object result1;
		if (method.getReturnType().equals(Double.class) || method.getReturnType().equals(double.class))
			result1= ScriptHelper.evalDouble(string, this);
		else
			result1= ScriptHelper.eval(string, this);

		final Object result= result1;

		if (method.getReturnType().isInterface())
		{
			Object proxiedResult= Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { method.getReturnType() }, new JsDelegateInvocationHandler(new JsDelegateInitializer()
			{
				public void init(Object proxy)
				{
					ScriptHelper.put("delegate", proxy, this);
					ScriptHelper.put("original", result, this);
					ScriptHelper.eval("delegate.jsDelegate= original", this);
				}
			}));

			return proxiedResult;
		}

		return result1;
	}

	public void checkInitialized(Object proxy)
	{
		if (!initialized)
		{
			initialiazer.init(proxy);
			initialized= true;
		}
	}
}