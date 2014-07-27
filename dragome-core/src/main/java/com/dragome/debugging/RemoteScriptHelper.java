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
package com.dragome.debugging;

import com.dragome.commons.javascript.ScriptHelperInterface;
import com.dragome.remote.ServiceFactory;

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

	public long evalLong(String jsCode)
	{
		return 0;
	}

	public int evalInt(String script, Object caller)
	{
		StackTraceElement[] stackTrace= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement= stackTrace[3];

		CrossExecutionResult crossExecutionResult= crossExecutionCommandProcessor.process(new JsEvalIntegerInMethod(new ReferenceHolder(caller), script, stackTraceElement.getMethodName()));
		return Integer.parseInt(crossExecutionResult.getResult() + "");
	}

	public float evalFloat(String jsCode)
	{

		return 0;
	}

	public double evalDouble(String jsCode)
	{

		return 0;
	}

	public char evalChar(String jsCode)
	{

		return 0;
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
				result= new JavascriptReference(Integer.parseInt(stringResult.substring("js-ref".length() + 1)));
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

}
