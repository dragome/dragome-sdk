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
