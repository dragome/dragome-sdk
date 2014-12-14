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
package com.dragome.debugging.execution;

import java.util.List;
import java.util.concurrent.Executor;

import com.dragome.debugging.ServiceInvocationResult;
import com.dragome.debugging.messages.Receiver;
import com.dragome.debugging.temp.TempHelper;
import com.dragome.html.dom.w3c.BrowserDomHandler;
import com.dragome.model.EventDispatcherHelper;
import com.dragome.remote.ApplicationExecutor;
import com.dragome.services.RequestExecutorImpl;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;

public class DragomeApplicationLauncher
{
	protected int responseMessagesCounter= 0;

	private ApplicationExecutor prepareLaunch()
	{
		ServiceLocator.getInstance().setDomHandler(new BrowserDomHandler());
		ServiceLocator.getInstance().setClientSideEnabled(true);

		if (ServiceLocator.getInstance().isRemoteDebugging())
			return createDebuggingApplicationExecutor();
		else
			return createProductionApplicationExecutor();
	}

	private ApplicationExecutor createProductionApplicationExecutor()
	{
		return new ApplicationExecutor()
		{
			public void pushResult(ServiceInvocationResult result)
			{
			}

			public void executeByClassName(String typeName)
			{
				execute((Class<?>) ServiceLocator.getInstance().getReflectionService().forName(typeName));
			}

			public void execute(Class<?> type)
			{
				VisualActivity visualActivity= (VisualActivity) ServiceLocator.getInstance().getReflectionService().createClassInstance(type);
				visualActivity.onCreate();
			}

			public void pushException(DragomeJsException exception)
			{
				throw exception;
			}
		};
	}

	private ApplicationExecutor createDebuggingApplicationExecutor()
	{
		final ApplicationExecutor applicationExecutor= RequestExecutorImpl.createRemoteServiceByWebSocket(ApplicationExecutor.class);
		ServiceLocator.getInstance().getClientToServerMessageChannel().setReceiver(new Receiver()
		{
			public void reset()
			{
			}

			public void messageReceived(String aMessage)
			{
				List<ServiceInvocation> serviceInvocations= (List<ServiceInvocation>) TempHelper.getObjectFromMessage(aMessage);
				for (ServiceInvocation serviceInvocation : serviceInvocations)
				{
					try
					{
						Object result= serviceInvocation.invoke();
						if (!ServiceLocator.getInstance().isMethodVoid(serviceInvocation.getMethod()))
						{
							applicationExecutor.pushResult(new ServiceInvocationResult(serviceInvocation, result));
//							System.out.println("response message: " + responseMessagesCounter++);
						}
					}
					catch (Exception e)
					{
						//			    applicationExecutor.pushException(new DragomeJsException(e, e.getMessage()));
						applicationExecutor.pushResult(new ServiceInvocationResult(serviceInvocation, new DragomeJsException(e, "Execution failed in browser: " + e.getMessage())));
					}
				}
			}
		});
		return applicationExecutor;
	}

	public void launch(final String typeName)
	{
		final ApplicationExecutor applicationExecutor= prepareLaunch();
		Runnable runnable= new Runnable()
		{
			public void run()
			{
				Executor executor= ServiceLocator.getInstance().getConfigurator().getExecutionHandler().getExecutor();
				executor.execute(new Runnable()
				{
					public void run()
					{
						applicationExecutor.executeByClassName(typeName);
					}
				});
			}
		};

		EventDispatcherHelper.runApplication(runnable);
	}
}
