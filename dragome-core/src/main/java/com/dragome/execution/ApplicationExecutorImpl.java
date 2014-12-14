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
package com.dragome.execution;

import java.util.concurrent.Executor;

import com.dragome.debugging.CrossExecutionSemaphore;
import com.dragome.debugging.ServiceInvocationResult;
import com.dragome.html.dom.DragomeJsException;
import com.dragome.services.ServiceLocator;
import com.dragome.view.VisualActivity;

public class ApplicationExecutorImpl implements ApplicationExecutor
{
	public static CrossExecutionSemaphore semaphore= new CrossExecutionSemaphore();
	protected static Thread thread;

	public ApplicationExecutorImpl()
	{
	}

	private void execute(final Class<?> type)
	{
		if (thread != null)
		{
			thread.interrupt();
			semaphore.reset();
		}

		thread= new Thread()
		{
			public void run()
			{
				Executor executor= ServiceLocator.getInstance().getConfigurator().getExecutionHandler().getExecutor();
				executor.execute(new Runnable()
				{
					public void run()
					{
						VisualActivity application= (VisualActivity) ServiceLocator.getInstance().getReflectionService().createClassInstance(type);
						application.onCreate();
					}
				});
			};
		};

		thread.start();
	}

	public void pushResult(ServiceInvocationResult result)
	{
		semaphore.pushResult(result);
	}

	public void executeByClassName(String typeName)
	{
		execute(ServiceLocator.getInstance().getReflectionService().forName(typeName));
	}

	public void pushException(DragomeJsException exception)
	{
		throw exception;
		//System.out.println("exception!!!");
	}
}
