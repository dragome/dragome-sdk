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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.debugging.interfaces.CrossExecutionCommand;
import com.dragome.debugging.interfaces.CrossExecutionCommandProcessor;
import com.dragome.debugging.interfaces.CrossExecutionResult;
import com.dragome.helpers.DragomeEntityManager;

public class CrossExecutionCommandProcessorImpl implements CrossExecutionCommandProcessor
{
	public static Map<String, Object> javaInstances= new HashMap<String, Object>();

	public CrossExecutionCommandProcessorImpl()
	{
	}

	public CrossExecutionResult process(CrossExecutionCommand crossExecutionCommand)
	{
		ScriptHelper.put("crossExecutionCommand", crossExecutionCommand, null);
		ReferenceHolder callerReferenceHolder= crossExecutionCommand.getCallerReferenceHolder();
		Object caller= getOrCreateReference(callerReferenceHolder);
		ScriptHelper.put("caller", caller, null);

		ScriptHelper.put("containerMethod", crossExecutionCommand.getMethodName(), null);
		if (!ScriptHelper.evalBoolean("caller.variablesList", this))
			ScriptHelper.eval("caller.variablesList= []", this);

		if (!ScriptHelper.evalBoolean("caller.variablesList[containerMethod]", this))
		{
			Map<String, Object> variables= new HashMap<String, Object>();
			ScriptHelper.put("list", variables, null);
			ScriptHelper.eval("caller.variablesList[containerMethod]= list", this);
		}
		ScriptHelper.eval("var variablesList= caller.variablesList[containerMethod]", this);
		CrossExecutionResult result= (CrossExecutionResult) ScriptHelper.eval("EventDispatcher.doProcess.call(caller, variablesList, crossExecutionCommand)", this);

		return result;
		//	    boolean sameId= nextCommand.getCallerReferenceHolder().getId().equals(crossExecutionCommand.getCallerReferenceHolder().getId());
		//	    boolean sameMethodName= nextCommand.getMethodName().equals(crossExecutionCommand.getMethodName());
	}

	@MethodAlias(alias= "EventDispatcher.doProcess")
	public static CrossExecutionResult doProcess(Map<String, Object> variableCreations, CrossExecutionCommand crossExecutionCommand)
	{
		CrossExecutionResult result= new EmptyCrossExecutionResult();

		if (crossExecutionCommand instanceof JsVariableCreationInMethod)
			result= processVariableCreationCommand(variableCreations, (JsVariableCreationInMethod) crossExecutionCommand);

		for (Entry<String, Object> entry : variableCreations.entrySet())
		{
			ScriptHelper.put("_name", entry.getKey(), null);
			ScriptHelper.put("_value", entry.getValue(), null);
			ScriptHelper.eval("eval('var '+_name+'= _value')", null);
		}

		if (crossExecutionCommand instanceof JsEvalInMethod)
		{
			ScriptCrossExecutionCommand jsEvalInMethod= (ScriptCrossExecutionCommand) crossExecutionCommand;
			ScriptHelper.put("script", jsEvalInMethod.getScript(), null);
			//	    System.out.println(jsEvalInMethod.getScript());
			Object eval2= ScriptHelper.eval("eval(script)", null);
			ScriptHelper.put("eval2", eval2, null);
			if (eval2 instanceof String)
				eval2= eval2.toString();
			else if (ScriptHelper.evalBoolean("typeof eval2 === 'number'", null))
				eval2= ScriptHelper.eval("eval2.toString()", null);
			else
			{
				//		eval2= new JavascriptReference(eval2);
				eval2= "js-ref:" + DragomeEntityManager.add(eval2);
			}

			result= new CrossExecutionResultImpl((String) eval2);
		}
		else if (crossExecutionCommand instanceof JsEvalIntegerInMethod)
		{
			JsEvalIntegerInMethod jsEvalInMethod= (JsEvalIntegerInMethod) crossExecutionCommand;
			ScriptHelper.put("script", jsEvalInMethod.getScript(), null);
			//	    System.out.println(jsEvalInMethod.getScript());
			int evalInt= ScriptHelper.evalInt("eval(script)", null);
			result= new CrossExecutionResultImpl(evalInt + "");
		}
		else if (crossExecutionCommand instanceof JsEvalBooleanInMethod)
		{
			JsEvalBooleanInMethod jsEvalInMethod= (JsEvalBooleanInMethod) crossExecutionCommand;
			ScriptHelper.put("script", jsEvalInMethod.getScript(), null);
			//	    System.out.println(jsEvalInMethod.getScript());
			boolean evalBoolean= ScriptHelper.evalBoolean("eval(script)", null);
			result= new CrossExecutionResultImpl(evalBoolean + "");
		}

		for (Entry<String, Object> entry : variableCreations.entrySet())
		{
			ScriptHelper.put("localVariable", entry.getKey(), null);

			Object evaluation= ScriptHelper.eval("eval(localVariable.toString())", null);
			variableCreations.remove(entry.getKey());
			variableCreations.put(entry.getKey(), evaluation);
		}

		return result;
	}

	private static CrossExecutionResult processVariableCreationCommand(Map<String, Object> variableCreations, JsVariableCreationInMethod jsVariableCreationInMethod)
	{
		CrossExecutionResult result;
		Object reference;
		if (jsVariableCreationInMethod.getValueReferenceHolder().getType().equals(JavascriptReference.class))
		{
			reference= DragomeEntityManager.get(jsVariableCreationInMethod.getValueReferenceHolder().getId());
		}
		else
		{
			if (jsVariableCreationInMethod.getValueReferenceHolder().getId() == null && jsVariableCreationInMethod.getValueReferenceHolder().getValue() == null)
				reference= null;
			else
				reference= getOrCreateReference(jsVariableCreationInMethod.getValueReferenceHolder());

			if (jsVariableCreationInMethod.getValueReferenceHolder().getValue() != null)
			{
				reference= jsVariableCreationInMethod.getValueReferenceHolder().getValue();
				//		System.out.println(jsVariableCreationInMethod.getName() + "= " + reference);
			}
			else if (jsVariableCreationInMethod.getValueReferenceHolder().getBooleanValue() != null)
			{
				reference= jsVariableCreationInMethod.getValueReferenceHolder().getBooleanValue().booleanValue();
				//		System.out.println(jsVariableCreationInMethod.getName() + "= " + reference);
			}
			else
			{
				//	    else
				//		System.out.println(jsVariableCreationInMethod.getName() + "= " + reference.getClass());
			}
		}

		variableCreations.put(jsVariableCreationInMethod.getName(), reference);
		result= null;
		return result;
	}

	public static Object getOrCreateReference(ReferenceHolder referenceHolder)
	{
		try
		{
			if (referenceHolder.getValue() != null)
				return referenceHolder.getValue();
			else
			{
				String id= referenceHolder.getId();
				Object reference= CrossExecutionCommandProcessorImpl.javaInstances.get(id);
				if (reference == null)
				{
					reference= referenceHolder.getType().newInstance();
					ScriptHelper.put("javaId", id, null);
					ScriptHelper.put("reference", reference, null);
					ScriptHelper.eval("reference.javaId=javaId", null);
					CrossExecutionCommandProcessorImpl.javaInstances.put(id, reference);
				}

				return reference;
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void processNoResult(CrossExecutionCommand crossExecutionCommand)
	{
		process(crossExecutionCommand);
	}
}
