/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.web.debugging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.dragome.commons.javascript.ScriptHelperInterface;
import com.dragome.services.interfaces.ServiceFactory;
import com.dragome.web.debugging.interfaces.CrossExecutionCommandProcessor;
import com.dragome.web.debugging.interfaces.CrossExecutionResult;

public class RemoteScriptHelper implements ScriptHelperInterface
{
	protected CrossExecutionCommandProcessor crossExecutionCommandProcessor;

	public RemoteScriptHelper(ServiceFactory remoteObjectsService)
	{
		crossExecutionCommandProcessor= remoteObjectsService.createFixedSyncService(CrossExecutionCommandProcessor.class);
	}

	public void put(String name, double value, Object caller)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		crossExecutionCommandProcessor.processNoResult(new JsVariableCreationInMethod(new ReferenceHolder(caller), name, new ReferenceHolder(Math.round(value) + ""), stackTraceElement.getMethodName()));
	}

	public void put(String name, boolean value, Object caller)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		crossExecutionCommandProcessor.processNoResult(new JsVariableCreationInMethod(new ReferenceHolder(caller), name, new ReferenceHolder(value), stackTraceElement.getMethodName()));
	}

	public void put(String name, Object value, Object caller)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		crossExecutionCommandProcessor.processNoResult(new JsVariableCreationInMethod(new ReferenceHolder(caller), name, new ReferenceHolder(value), stackTraceElement.getMethodName()));
	}

	public long evalLong(String jsCode, Object caller)
	{
		return evalInt(jsCode, caller);
	}

	public int evalInt(String script, Object caller)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		CrossExecutionResult crossExecutionResult= crossExecutionCommandProcessor.process(new JsEvalIntegerInMethod(new ReferenceHolder(caller), script, stackTraceElement.getMethodName()));
		return Integer.parseInt(crossExecutionResult.getResult() + "");
	}

	public float evalFloat(String jsCode, Object caller)
	{
		return evalInt(jsCode, caller);
	}

	public double evalDouble(String jsCode, Object caller)
	{
		return evalFloat(jsCode, caller);
	}

	public char evalChar(String jsCode, Object caller)
	{
		return eval(jsCode, caller).toString().charAt(0);
	}

	public boolean evalBoolean(String script, Object caller)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		CrossExecutionResult crossExecutionResult= crossExecutionCommandProcessor.process(new JsEvalBooleanInMethod(new ReferenceHolder(caller), script, stackTraceElement.getMethodName()));
		return Boolean.parseBoolean(crossExecutionResult.getResult() + "");
	}

	public Object eval(String script, Object caller)
	{
		Object result;
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		CrossExecutionResult crossExecutionResult= crossExecutionCommandProcessor.process(new JsEvalInMethod(new ReferenceHolder(caller), script, stackTraceElement.getMethodName()));
		String stringResult= crossExecutionResult.getResult();
		if (stringResult.startsWith("js-ref:"))
			if (stringResult.equals("js-ref:0"))
				result= null;
			else
				result= new JavascriptReference(stringResult.substring("js-ref".length() + 1));
		else
			result= stringResult;

		return result;
	}
	public void evalNoResult(String script, Object caller)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		crossExecutionCommandProcessor.processNoResult(new JsEvalInMethod(new ReferenceHolder(caller), script, stackTraceElement.getMethodName()));
	}

	@SuppressWarnings("unchecked")
	public <T> T putMethodReference(final String name, final Class<? extends T> declaringClass, final Object callerInstance)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		final StackTraceElement stackTraceElement= stackTrace[3];

		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { declaringClass }, new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				crossExecutionCommandProcessor.processNoResult(new JsMethodReferenceCreationInMethod(new ReferenceHolder(callerInstance), name, method.getName(), stackTraceElement.getMethodName(), declaringClass.getName()));
				return null;
			}
		});
	}
}
