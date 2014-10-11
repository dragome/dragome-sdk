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
package com.dragome.serverside.servlets;

import java.util.concurrent.Executor;

import com.dragome.debugging.messages.ServerToClientServiceInvoker;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.services.interfaces.SerializationService;

public class ServiceInvoker
{
	public String invoke(String invocationString)
	{
		return executeService(invocationString);
	}

	public static String executeService(final String parameter)
	{
		final String[] executeService= new String[1];

		Executor executor= ServiceLocator.getInstance().getConfigurator().getExecutionHandler().getExecutor();
		executor.execute(new Runnable()
		{
			public void run()
			{
				ServiceLocator.getInstance().getDomHandler().initDocument(); //TODO revisar!!

				SerializationService serializationService= ServiceLocator.getInstance().getSerializationService();

				Object result= ((ServiceInvocation) serializationService.deserialize(parameter)).invoke();
				executeService[0]= serializationService.serialize(result);
				ServerToClientServiceInvoker.finalizeMethodInvocationsInClient();
			}
		});

		return executeService[0];
	}
}
