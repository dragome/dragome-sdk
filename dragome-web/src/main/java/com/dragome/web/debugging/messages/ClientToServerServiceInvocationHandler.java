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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.debugging.CrossExecutionResultImpl;
import com.dragome.web.debugging.ServiceInvocationResult;
import com.dragome.web.debugging.interfaces.CrossExecutionResult;
import com.dragome.web.execution.ApplicationExecutor;

public class ClientToServerServiceInvocationHandler implements InvocationHandler
{
	private Class<?> type;

	public ClientToServerServiceInvocationHandler(Class<?> type)
	{
		this.type= type;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		return invokeMethod2(type, method, args);
	}

	public Object invokeMethod2(Class type, Method method, Object[] args)
	{
		boolean serialized= true;

		String serialize= "";

		serialize= "{\"@id\":0,\"method\":{\"class\":\"java.lang.reflect.Method\", \"name\":\"${AE}.pushResult:1\"},\"id\":\"16\",\"args\":[{\"@id\":1,\"listResult\":null,\"id\":\"${id}\",\"objectResult\":{\"@id\":2,\"result\":\"${result}\",\"class\":\"${CERI}\"},\"class\":\"${SIR}\"}],\"type\":{\"name\":\"${AE}\",\"class\":\"java.lang.Class\"},\"class\":\"${SI}\"}";

		//		serialize= "{\"@id\":0,\"method\":\"${AE}.pushResult:1\",\"id\":\"16\",\"args\":[{\"@id\":1,\"listResult\":null,\"id\":\"${id}\",\"objectResult\":{\"@id\":2,\"result\":\"${result}\",\"class\":\"${CERI}\"},\"class\":\"${SIR}\"}],\"type\":{\"name\":\"${AE}\",\"class\":\"java.lang.Class\"},\"class\":\"${SI}\"}";
		//	serialize= "{\"@id\":0,\"method\":\"${AE}.pushResult:1\",\"id\":\"292\",\"args\":[{\"@id\":1,\"result\":{\"@id\":2,\"result\":\"${result}\",\"class\":\"${CERI}\"},\"id\":\"${id}\",\"class\":\"${SIR}\"}],\"type\":{\"name\":\"${AE}\",\"class\":\"java.lang.Class\"},\"class\":\"${SI}\"}";

		serialize= serialize.replace("${AE}", ApplicationExecutor.class.getName());
		serialize= serialize.replace("${CERI}", CrossExecutionResultImpl.class.getName());
		serialize= serialize.replace("${SIR}", ServiceInvocationResult.class.getName());
		serialize= serialize.replace("${SI}", ServiceInvocation.class.getName());

		if (method.getName().equals("pushResult") && args[0] instanceof ServiceInvocationResult)
		{
			ServiceInvocationResult serviceInvocationResult= (ServiceInvocationResult) args[0];
			if (serviceInvocationResult.getObjectResult() instanceof CrossExecutionResult)
			{
				CrossExecutionResult result= (CrossExecutionResult) serviceInvocationResult.getObjectResult();
				serialize= serialize.replace("${result}", (result.getResult() + "").replace("\"", "\\\"").replace("\n", "\\\\n").replace("\r", "\\\\r"));
				serialize= serialize.replace("${id}", serviceInvocationResult.getId());
				serialized= true;
			}
			else
				serialize= serializeResult(type, method, args);
		}
		else
		{
			serialize= serializeResult(type, method, args);
		}

		Sender serverMessageChannel= WebServiceLocator.getInstance().getClientToServerMessageChannel();
		serverMessageChannel.send(serialize);
		return null;
	}

	public String serializeResult(Class type, Method method, Object[] args)
	{
		String serialize;
		List<Object> arguments= args != null ? Arrays.asList(args) : new ArrayList<Object>();
		ServiceInvocation serviceInvocation= new ServiceInvocation(type, method, arguments);
		serialize= ServiceLocator.getInstance().getSerializationService().serialize(serviceInvocation);
		//	    System.out.println(serialize);
		return serialize;
	}
}
