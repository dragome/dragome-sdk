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
package com.dragome.web.dispatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.web.debugging.JsMethodReferenceCreationInMethod;
import com.dragome.web.debugging.JsVariableCreationInMethod;
import com.dragome.web.debugging.ReferenceHolder;
import com.dragome.web.debugging.ScriptCrossExecutionCommand;

public class EventDispatcherExtraUtils
{
	private static Map<String, Method> foundMethods= new HashMap<>();

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

	@MethodAlias(alias= "EventDispatcher.njmrcinm")
	private static JsMethodReferenceCreationInMethod njmrcinm(String methodName, ReferenceHolder caller, String name, String methodSignature, String declaringClassName)
	{
		return new JsMethodReferenceCreationInMethod(caller, name, methodSignature, methodName, declaringClassName);
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
		String key= aClass.getName() + "." + methodName;
		Method foundMethod= foundMethods.get(key);

		if (foundMethod == null)
		{
			Method[] methods= aClass.getMethods();
			for (Method method : methods)
				if (method.getName().equals(methodName))
				{
					foundMethod= method;
					foundMethods.put(key, foundMethod);
				}
		}

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
