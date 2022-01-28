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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.web.debugging.interfaces.CrossExecutionCommand;
import com.dragome.web.debugging.interfaces.CrossExecutionCommandProcessor;
import com.dragome.web.debugging.interfaces.CrossExecutionResult;

public class CrossExecutionCommandProcessorImpl implements CrossExecutionCommandProcessor
{
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
			ScriptHelper.eval("caller.variablesList[containerMethod]= []", this);

		ScriptHelper.eval("var variablesList= caller.variablesList[containerMethod]", this);
		CrossExecutionResult result= (CrossExecutionResult) ScriptHelper.eval("EventDispatcher.doProcess.call(caller, variablesList, crossExecutionCommand)", this);

		return result;
	}

	@MethodAlias(alias= "EventDispatcher.doProcess")
	public static CrossExecutionResult doProcess(Object variableCreations, CrossExecutionCommand crossExecutionCommand)
	{
		CrossExecutionResult result= new EmptyCrossExecutionResult();

		if (crossExecutionCommand instanceof JsVariableCreationInMethod)
			result= processVariableCreationCommand(variableCreations, (JsVariableCreationInMethod) crossExecutionCommand);
		else if (crossExecutionCommand instanceof JsMethodReferenceCreationInMethod)
			result= processMethodReferenceCreationCommand(variableCreations, (JsMethodReferenceCreationInMethod) crossExecutionCommand);

		else
		{
			ScriptHelper.put("variablesObject", variableCreations, null);
			ScriptHelper.eval("contextEvaluator.node= this.node", null);

			ScriptCrossExecutionCommand jsEvalInMethod= (ScriptCrossExecutionCommand) crossExecutionCommand;
			ScriptHelper.put("script", jsEvalInMethod.getScript(), null);
			String stringResult= "";

			if (crossExecutionCommand instanceof JsEvalInMethod)
			{
				Object eval2= ScriptHelper.eval("contextEvaluator.execute(variablesObject, script)", null);
				ScriptHelper.put("eval2", eval2, null);
				if (eval2 instanceof String)
					eval2= eval2.toString();
				else if (ScriptHelper.evalBoolean("typeof eval2 === 'number'", null))
					eval2= ScriptHelper.eval("eval2.toString()", null);
				else
					eval2= "js-ref:" + DragomeEntityManager.add(eval2);

				stringResult= (String) eval2;
			}
			else if (crossExecutionCommand instanceof JsEvalIntegerInMethod)
			{
				int evalInt= ScriptHelper.evalInt("contextEvaluator.execute(variablesObject, script)", null);
				stringResult= evalInt + "";
			}
			else if (crossExecutionCommand instanceof JsEvalBooleanInMethod)
			{
				boolean evalBoolean= ScriptHelper.evalBoolean("contextEvaluator.execute(variablesObject, script)", null);
				stringResult= evalBoolean + "";
			}

			result= new CrossExecutionResultImpl(stringResult);
		}
		return result;
	}

	private static CrossExecutionResult processVariableCreationCommand(Object variableCreations, JsVariableCreationInMethod jsVariableCreationInMethod)
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
			}
			else if (jsVariableCreationInMethod.getValueReferenceHolder().getBooleanValue() != null)
			{
				ScriptHelper.put("booleanPrimitiveValue", jsVariableCreationInMethod.getValueReferenceHolder().getBooleanValue().booleanValue(), null);
				reference= ScriptHelper.eval("booleanPrimitiveValue", null);
			}
			else
			{
			}
		}

		ScriptHelper.put("variables", variableCreations, null);
		ScriptHelper.put("variableName", jsVariableCreationInMethod.getName(), null);
		ScriptHelper.put("value", reference, null);

		ScriptHelper.eval("variables[variableName]= value", null);
		result= null;
		return result;
	}

	private static CrossExecutionResult processMethodReferenceCreationCommand(Object variableCreations, JsMethodReferenceCreationInMethod jsMethodReferenceCreationInMethod)
	{
		ScriptHelper.put("declaringClass", jsMethodReferenceCreationInMethod.getDeclaringClassName(), null);
		ScriptHelper.put("methodSignature", jsMethodReferenceCreationInMethod.getMethodSignature(), null);
		Object method= ScriptHelper.eval("dragomeJs.resolveMethod(declaringClass, methodSignature)", null);

		ScriptHelper.put("variables", variableCreations, null);
		ScriptHelper.put("variableName", jsMethodReferenceCreationInMethod.getName(), null);
		ScriptHelper.put("value", method, null);
		ScriptHelper.eval("variables[variableName]= value", null);
		return null;
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

				Object reference= DragomeEntityManager.get(id);
				if (reference == null)
				{
					reference= referenceHolder.getType().newInstance();
					ScriptHelper.put("javaId", id, null);
					ScriptHelper.put("reference", reference, null);
					ScriptHelper.eval("reference.javaId=javaId", null);
					DragomeEntityManager.put(id, reference);
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

	public CrossExecutionResult processMultiple(List<CrossExecutionCommand> commands)
	{
		List<CrossExecutionResult> result= new ArrayList<CrossExecutionResult>();
		String methodName= "";
		
		ScriptHelper.eval("var variablesList= []", this);

		for (CrossExecutionCommand crossExecutionCommand : commands)
		{
			boolean sameMethod= crossExecutionCommand.getMethodName().equals(methodName);

			if (!sameMethod)
			{
				ScriptHelper.put("crossExecutionCommand", crossExecutionCommand, null);
				ReferenceHolder callerReferenceHolder= crossExecutionCommand.getCallerReferenceHolder();
				Object caller= getOrCreateReference(callerReferenceHolder);
				ScriptHelper.put("caller", caller, null);

				ScriptHelper.put("containerMethod", crossExecutionCommand.getMethodName(), null);
				if (!ScriptHelper.evalBoolean("caller.variablesList", this))
					ScriptHelper.eval("caller.variablesList= []", this);

				if (!ScriptHelper.evalBoolean("caller.variablesList[containerMethod]", this))
					ScriptHelper.eval("caller.variablesList[containerMethod]= []", this);

				ScriptHelper.eval("variablesList= caller.variablesList[containerMethod]", this);
			}
			
			CrossExecutionResult result1= (CrossExecutionResult) ScriptHelper.eval("EventDispatcher.doProcess.call(caller, variablesList, crossExecutionCommand)", this);

			result.add(result1);
			methodName= crossExecutionCommand.getMethodName();
		}

		return result.get(result.size() - 1);
	}
}
