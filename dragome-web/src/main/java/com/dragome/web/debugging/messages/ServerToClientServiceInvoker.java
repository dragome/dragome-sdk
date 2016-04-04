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
package com.dragome.web.debugging.messages;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.Deflater;

import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.debugging.JsMethodReferenceCreationInMethod;
import com.dragome.web.debugging.JsVariableCreationInMethod;
import com.dragome.web.debugging.ReferenceHolder;
import com.dragome.web.debugging.ScriptCrossExecutionCommand;

public class ServerToClientServiceInvoker
{
	protected static List<ServiceInvocation> invocations= Collections.synchronizedList(new ArrayList<ServiceInvocation>());

	public static synchronized ServiceInvocation invokeMethodInClient(Class<?> type, Method method, Object[] args)
	{
		ServiceInvocation returnValue= null;
		ServiceInvocation serviceInvocation= new ServiceInvocation(type, method, args != null ? Arrays.asList(args) : new ArrayList<Object>());
		invocations.add(serviceInvocation);

		if (true ||!WebServiceLocator.getInstance().isMethodVoid(method))
		{
			returnValue= serviceInvocation;
			performInvocations();
		}

		return returnValue;
	}

	private static void performInvocations()
	{
		StringBuilder message= new StringBuilder();

		if (invocations.size() > 0)
		{
			message.append("_ed.nl(");

			synchronized (invocations)
			{
				for (ServiceInvocation serviceInvocation2 : invocations)
				{
					StringBuilder partialMessage= new StringBuilder();
					serializeServiceInvocation(partialMessage, serviceInvocation2);
					//				System.out.println(partialMessage);
					message.append(partialMessage);
					message.append(",");
				}
			}
			message.setLength(message.length() - 1);
			message.append(")");
		}

		String message3= message.toString().replace("\"null\"", "null");
		invocations.clear();
		//	byte[] compress= compress(message3.getBytes());
		//	message3= new String(Base64Coder.encode(compress));

		WebServiceLocator.getInstance().getServerToClientMessageChannel().send(message3.toString());
	}
	private static void serializeServiceInvocation(StringBuilder message, ServiceInvocation serviceInvocation2)
	{
		message.append("_ed.nsi(");
		String transformType= transformType(serviceInvocation2.getType().getName());
		//	transformType= "createClass(" + serviceInvocation2.getType().getName().replace(".", "_") + ")";
		message.append(transformType + ",");
		message.append("\"" + serviceInvocation2.getMethod().getName() + "\"" + ",");
		message.append("\"" + serviceInvocation2.getId() + "\"" + ",");

		message.append("_ed.nl(");
		for (Object arg : serviceInvocation2.getArgs())
		{
			serializeArg(message, arg);
			message.append(",");
		}
		if (!serviceInvocation2.getArgs().isEmpty())
			message.setLength(message.length() - 1);
		message.append(")");
		message.append(")");
	}

	private static void serializeArg(StringBuilder message, Object arg)
	{
		if (arg instanceof JsVariableCreationInMethod)
		{
			JsVariableCreationInMethod jsVariableCreationInMethod= (JsVariableCreationInMethod) arg;
			message.append("_ed.njvcim(");
			message.append("\"" + jsVariableCreationInMethod.getMethodName() + "\"" + ",");
			serializeReferenceHolder(message, jsVariableCreationInMethod.getCallerReferenceHolder());
			message.append(",");
			message.append("\"" + jsVariableCreationInMethod.getName() + "\"" + ",");
			serializeReferenceHolder(message, jsVariableCreationInMethod.getValueReferenceHolder());
			message.append(")");
		}
		else if (arg instanceof JsMethodReferenceCreationInMethod)
		{
			JsMethodReferenceCreationInMethod jsMethodReferenceCreationInMethod= (JsMethodReferenceCreationInMethod) arg;
			message.append("_ed.njmrcinm(");
			message.append("\"" + jsMethodReferenceCreationInMethod.getMethodName() + "\"" + ",");
			serializeReferenceHolder(message, jsMethodReferenceCreationInMethod.getCallerReferenceHolder());
			message.append(",");
			message.append("\"" + jsMethodReferenceCreationInMethod.getName() + "\"" + ",");
			message.append("\"" + jsMethodReferenceCreationInMethod.getMethodSignature() + "\"" + ",");
			message.append("\"" + jsMethodReferenceCreationInMethod.getDeclaringClassName() + "\"");
			message.append(")");
		}
		else if (arg instanceof ScriptCrossExecutionCommand)
		{
			ScriptCrossExecutionCommand scriptCrossExecutionCommand= (ScriptCrossExecutionCommand) arg;
			message.append("_ed.njeim(");
			message.append("\"" + scriptCrossExecutionCommand.getMethodName() + "\"" + ",");
			serializeReferenceHolder(message, scriptCrossExecutionCommand.getCallerReferenceHolder());
			message.append(",");
			message.append("\"" + scriptCrossExecutionCommand.getScript() + "\"" + ",");
			message.append(transformType(scriptCrossExecutionCommand.getClass().getName()));
			message.append(")");
		}
		else if (arg instanceof String)
		{
			String string= (String) arg;
			message.append("_ed.ns(");
			message.append("\"" + string + "\"");
			message.append(")");
		}
		else
		{
			String serialized= ServiceLocator.getInstance().getSerializationService().serialize(arg);

			message.append("_ed.nO(");
			message.append("\"" + serialized + "\"");
			message.append(")");
		}
	}

	private static void serializeReferenceHolder(StringBuilder message, ReferenceHolder callerReferenceHolder)
	{
		message.append("_ed.nrh(");
		message.append("\"" + callerReferenceHolder.getId() + "\"" + ",");
		message.append("\"" + escapeChars(callerReferenceHolder.getValue()) + "\"" + ",");
		message.append("" + callerReferenceHolder.getBooleanValue() + "" + ",");
		message.append(transformType(callerReferenceHolder.getType() != null && !callerReferenceHolder.getType().getName().contains("com.sun.proxy.$Proxy") ? callerReferenceHolder.getType().getName() : "java.lang.Object"));
		message.append(")");
	}

	private static String escapeChars(String value)
	{
		return value != null ? value.replace("\\\"", "\\\\\"").replace("\"", "\\\"").replace("\\n", "\\\\n").replace("\\r", "\\\\r").replace("\n", "\\n").replace("\r", "\\\\r") : value;
	}

	private static String transformType(String name)
	{
		return "\"" + name + "\"";
	}

	static public byte[] compress(byte[] input)
	{
		Deflater deflater= new Deflater();
		deflater.setInput(input, 0, input.length);
		deflater.finish();
		byte[] buff= new byte[input.length + 50];
		deflater.deflate(buff);

		int compressedSize= deflater.getTotalOut();

		if (deflater.getTotalIn() != input.length)
			return null;

		byte[] output= new byte[compressedSize - 6];

		System.arraycopy(buff, 2, output, 0, compressedSize - 6);// del head and
		// foot byte
		return output;
	}

	public static void finalizeMethodInvocationsInClient()
	{
		performInvocations();
	}
}
