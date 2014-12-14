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
package com.dragome.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.dragome.debugging.messages.ServerToClientServiceInvoker;
import com.dragome.execution.ApplicationExecutorImpl;

public class ClientServiceInvocationHandler implements InvocationHandler
{
	private Class<?> type;

	public ClientServiceInvocationHandler(Class<?> type)
	{
		this.type= type;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		ServiceInvocation serviceInvocation= ServerToClientServiceInvoker.invokeMethodInClient(type, method, args);
		if (ServiceLocator.getInstance().isMethodVoid(method))
		{
			return null;
		}
		else
		{
			return waintUntilResultAvailable(serviceInvocation.getId());
		}
	}

	private Object waintUntilResultAvailable(String id)
	{
		return ApplicationExecutorImpl.semaphore.waitUntilResponse(id);
	}

}
