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
package com.dragome.web.execution;

import java.util.concurrent.Executor;

import com.dragome.services.ServiceLocator;
import com.dragome.view.VisualActivity;
import com.dragome.web.debugging.CrossExecutionSemaphore;
import com.dragome.web.debugging.ServiceInvocationResult;
import com.dragome.web.html.dom.DragomeJsException;

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
