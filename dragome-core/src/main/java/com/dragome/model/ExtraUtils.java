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
package com.dragome.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.debugging.JsVariableCreationInMethod;
import com.dragome.debugging.ReferenceHolder;
import com.dragome.debugging.ScriptCrossExecutionCommand;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;

public class ExtraUtils
{
	@MethodAlias(alias= "EventDispatcher.equalsFunction")
	private static boolean equalsFunction(Object o1, Object o2)
	{
		return o1.equals(o2);
	}

	@MethodAlias(alias= "EventDispatcher.hashCodeFunction")
	private static int hashCodeFunction(Object o1)
	{
		return o1.hashCode();
	}

	@MethodAlias(alias= "EventDispatcher.njeim")
	private static ScriptCrossExecutionCommand njeim(String methodName, ReferenceHolder caller, String script, String type)
	{
		ScriptCrossExecutionCommand scriptCrossExecutionCommand= (ScriptCrossExecutionCommand) ServiceLocator.getInstance().getReflectionService().createClassInstance(type);
		scriptCrossExecutionCommand.setCallerReferenceHolder(caller);
		scriptCrossExecutionCommand.setScript(script);
		scriptCrossExecutionCommand.setMethodName(methodName);
		return scriptCrossExecutionCommand;
	}

	@MethodAlias(alias= "EventDispatcher.ns")
	private static String ns(String string)
	{
		ScriptHelper.put("string2", string, null);
		return (String) ScriptHelper.eval("'null' == string2 ? null: string2", null);
	}

	@MethodAlias(alias= "EventDispatcher.nrh")
	private static ReferenceHolder nrh(String id, String value, Boolean booleanValue, String type)
	{
		Boolean booleanValue2= ScriptHelper.evalBoolean("booleanValue", null);
		ScriptHelper.put("booleanValue2", booleanValue2, null);
		booleanValue2= (Boolean) ScriptHelper.eval("booleanValue != null ? booleanValue2 : null", null);
		ReferenceHolder referenceHolder= new ReferenceHolder(id, ServiceLocator.getInstance().getReflectionService().forName(type), value, booleanValue2);
		return referenceHolder;
	}

	@MethodAlias(alias= "EventDispatcher.njvcim")
	private static JsVariableCreationInMethod njvcim(String methodName, ReferenceHolder caller, String name, ReferenceHolder value)
	{
		return new JsVariableCreationInMethod(caller, name, value, methodName);
	}

	@MethodAlias(alias= "EventDispatcher.nsi")
	private static ServiceInvocation nsi(String type, String methodName, String id, List<Object> args) throws NoSuchMethodException
	{
		Class<?> forName= ServiceLocator.getInstance().getReflectionService().forName(type);

		ServiceInvocation serviceInvocation= new ServiceInvocation(forName, findMethod(methodName, forName), args, id);
		ScriptHelper.put("serviceInvocation", serviceInvocation, null);
		return serviceInvocation;
	}

	private static Method findMethod(String methodName, Class<?> aClass)
	{
		Method foundMethod= null;

		Method[] methods= aClass.getMethods();
		for (Method method : methods)
			if (method.getName().equals(methodName))
				foundMethod= method;
		
		return foundMethod;
	}

	@MethodAlias(alias= "EventDispatcher.nl")
	private static List<Object> nl()
	{
		List<Object> result= new ArrayList<Object>();
		ScriptHelper.put("i", 0, null);
		int childCount= ScriptHelper.evalInt("arguments.length", null);
		for (int i= 0; i < childCount; i++)
		{
			ScriptHelper.put("i", i, null);
			Object arg= ScriptHelper.eval("arguments[i];", null);
			result.add(arg);
		}
		return result;
	}

	@MethodAlias(alias= "EventDispatcher.nO")
	private static Object nO(String serializedObject)
	{
		return ServiceLocator.getInstance().getSerializationService().deserialize(serializedObject);
	}
}
