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
package com.dragome.web.dispatcher;

import java.util.List;
import java.util.concurrent.Executor;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.AnnotationsHelper;
import com.dragome.helpers.AnnotationsHelper.AnnotationContainer.AnnotationEntry;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.web.annotations.PageAlias;
import com.dragome.web.config.DomHandlerApplicationConfigurator;
import com.dragome.web.execution.DragomeApplicationLauncher;

public class EventDispatcherHelper
{
	@MethodAlias(alias= "EventDispatcher.executeMainClass")
	public static void executeMainClass() throws Exception
	{
		try
		{
			WebServiceLocator.getInstance().setClientSideEnabled(true);

			ServiceLocator.getInstance().setConfigurator(getConfigurator());

			ParametersHandler parametersHandler= ServiceLocator.getInstance().getParametersHandler();

			String className= parametersHandler.getParameter("class");
			if (className == null || className.trim().length() == 0)
			{
				String requestURL= parametersHandler.getRequestURL();

				List<AnnotationEntry> annotationEntries= AnnotationsHelper.getAnnotationsByType(PageAlias.class).getEntries();
				for (AnnotationEntry annotationEntry : annotationEntries)
				{
					boolean isUnique= annotationEntries.size() == 2 && !"discoverer".equals(annotationEntry.getAnnotationValue());
					if (isUnique || (annotationEntry.getAnnotationKey().equals("alias") && requestURL.contains(annotationEntry.getAnnotationValue())))
						className= annotationEntry.getType().getName();
				}
			}

			launch(className);
		}
		catch (Exception e)
		{
			alert("ERROR (more info on browser console):" + e.getMessage());
			throw e;
		}
	}

	private static void launch(String className) throws Exception
	{
		try
		{
			if (className == null || className.trim().length() == 0)
				System.out.println("Please specify activity class to execute in querystring parameter 'class'");
			else
				new DragomeApplicationLauncher().launch(className);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	private static DragomeConfigurator getConfigurator()
	{
		DragomeConfigurator configurator= new DomHandlerApplicationConfigurator();
		List<AnnotationEntry> annotationEntries= AnnotationsHelper.getAnnotationsByType(DragomeConfiguratorImplementor.class).getEntries();
		for (AnnotationEntry annotationEntry : annotationEntries)
		{
			if (!annotationEntry.getType().equals(DomHandlerApplicationConfigurator.class))
				return ServiceLocator.getInstance().getReflectionService().createClassInstance((Class<? extends DragomeConfigurator>) annotationEntry.getType());
		}
		return configurator;
	}

	//	@MethodAlias(alias= "EventDispatcher.getComponentById")
	//	private static void getComponentById(Object event)
	//	{
	//		ScriptHelper.eval("stopEvent(event)", null);
	//		String id2= (String) ScriptHelper.eval("event.currentTarget.getAttribute('data-element-id')", null);
	//		EventDispatcherHelper.getComponentById(id2); //TODO revisar el static
	//	}

	@MethodAlias(alias= "EventDispatcher.onEvent")
	private static void onEvent()
	{
		Object event= ScriptHelper.eval("window.event || arguments.callee.caller.arguments[0]", null);
		Executor executor= ServiceLocator.getInstance().getConfigurator().getExecutionHandler().getExecutor();
		executor.execute(new EventExecutor(event));
	}
	protected static String getEventTargetId(Object event)
	{
		//	ScriptHelper.eval("stopEvent(event)", null);
		Object eventTarget= getEventTarget(event);
		ScriptHelper.put("eventTarget", eventTarget, null);
		String id= (String) ScriptHelper.eval("eventTarget.getAttribute('data-element-id')", null);
		return id;
	}

	protected static Object getEventTarget(Object event)
	{
		ScriptHelper.put("event", event, null);
		return ScriptHelper.eval("event.currentTarget ? event.currentTarget : event.target", null);
	}

	private static boolean processingEvent= false;

	public EventDispatcherHelper()
	{
	}

	public static void runOnlySynchronized(Runnable runnable)
	{
		try
		{
			//TODO revisar esto cuando se ejecuta en el cliente, posible freeze!
			if (!processingEvent)
			{
				processingEvent= true;
				runnable.run();
			}
		}
		finally
		{
			processingEvent= false;
		}
	}

	//	public static VisualComponent getComponentById(String id)
	//	{
	//		return ((VisualComponent) DragomeEntityManager.get(id));
	//	}

	public static Runnable applicationRunner;

	@MethodAlias(alias= "EventDispatcher.runApplication")
	public static void runApplication()
	{
		if (applicationRunner != null)
			applicationRunner.run();
		else
			alert("Cannot find any activity to execute, please add annotation @PageAlias(alias= \"page-name\") to your activity class.");
	}

	public static void alert(String message)
	{
		ScriptHelper.put("message", message, null);
		ScriptHelper.eval("alert(message)", null);
	}

	public static void runApplication(Runnable runnable)
	{
		if (WebServiceLocator.getInstance().isRemoteDebugging())
			applicationRunner= runnable;
		else
			runnable.run();
	}
}
