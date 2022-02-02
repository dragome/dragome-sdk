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
import com.dragome.web.debugging.interfaces.CrossExecutionCommand;
import com.dragome.web.debugging.interfaces.CrossExecutionCommandProcessor;

public class ServerToClientServiceInvoker
{
	protected static List<ServiceInvocation> invocations= Collections.synchronizedList(new ArrayList<ServiceInvocation>());
	private static long lastTime= System.currentTimeMillis();

	public static synchronized ServiceInvocation invokeMethodInClient(Class<?> type, Method method, Object[] args)
	{
		ServiceInvocation returnValue= null;
		ServiceInvocation serviceInvocation= new ServiceInvocation(type, method, args != null ? Arrays.asList(args) : new ArrayList<Object>());
		invocations.add(serviceInvocation);

		long currentTimeMillis= System.currentTimeMillis();
		boolean canInvoke= false;

		if (currentTimeMillis - lastTime > 500)
		{
			lastTime= currentTimeMillis;
			canInvoke= true;
		}
		else if (currentTimeMillis - lastTime < 50)
			canInvoke= true;

		canInvoke= false;

		boolean methodVoid= WebServiceLocator.getInstance().isMethodVoid(method);
		if (canInvoke || !methodVoid)
		{
			returnValue= serviceInvocation;
			performInvocations(methodVoid);
		}

		return returnValue;
	}

	private static void performInvocations(boolean isVoid)
	{
		try
		{
			StringBuilder message= new StringBuilder();

			if (invocations.size() > 0)
			{
				message.append("_ed.nl(");

				synchronized (invocations)
				{
					List<CrossExecutionCommand> commands= new ArrayList<CrossExecutionCommand>();
					String id= "sin id";
					for (ServiceInvocation serviceInvocation2 : invocations)
					{
						List<?> args= serviceInvocation2.getArgs();
						if (args.size() > 1)
							System.out.println("sdgsdg");

						CrossExecutionCommand crossExecutionCommand= (CrossExecutionCommand) args.get(0);
						commands.add(crossExecutionCommand);
						id= serviceInvocation2.getId();
					}

					Class<CrossExecutionCommandProcessor> class1= CrossExecutionCommandProcessor.class;
					Method method= class1.getMethod("processMultiple", List.class);
					List<Object> commands2= new ArrayList<>();
					commands2.add(commands);
					ServiceInvocation serviceInvocation= new ServiceInvocation(class1, method, commands2);
					serviceInvocation.setId(id);
					serviceInvocation.setVoidService(isVoid);

					StringBuilder partialMessage= new StringBuilder();
					serializeServiceInvocation(partialMessage, serviceInvocation);
					message.append(partialMessage);
					message.append(",");

					//					for (ServiceInvocation serviceInvocation2 : invocations)
					//					{
					//						StringBuilder partialMessage= new StringBuilder();
					//						serializeServiceInvocation(partialMessage, serviceInvocation2);
					//						//				System.out.println(partialMessage);
					//						message.append(partialMessage);
					//						message.append(",");
					//					}
				}
				message.setLength(message.length() - 1);
				message.append(")");

				String message3= message.toString().replace("\"null\"", "null");
				invocations.clear();
				//		byte[] compress= compress(message3.getBytes());
				//		message3= new String(Base64Coder.encode(compress));

				WebServiceLocator.getInstance().getServerToClientMessageChannel().send(message3.toString());
			}
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new RuntimeException(e);
		}
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
		message.append("),");
		message.append(serviceInvocation2.isVoidService());
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
		else if (arg instanceof List)
		{
			List<Object> list= (List) arg;
			message.append("_ed.nl(");

			for (int i= 0; i < list.size(); i++)
			{
				serializeArg(message, list.get(i));
				if (i < list.size() - 1)
					message.append(",");
			}

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

	public static String escapeChars(String value)
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
		performInvocations(false);
	}
}
