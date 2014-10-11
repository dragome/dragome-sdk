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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dragome.annotations.ServiceImplementation;
import com.dragome.debugging.CrossExecutionCommandProcessor;
import com.dragome.debugging.CrossExecutionCommandProcessorImpl;
import com.dragome.debugging.execution.ApplicationExecutorImpl;
import com.dragome.html.dom.DomHandler;
import com.dragome.html.dom.EventDispatcher;
import com.dragome.html.dom.w3c.BrowserDomHandler;
import com.dragome.model.EventDispatcherImpl;
import com.dragome.remote.ApplicationExecutor;
import com.dragome.render.html.HtmlTemplateStorageImpl;
import com.dragome.render.interfaces.HtmlTemplateStorage;
import com.dragome.services.interfaces.AsyncResponseHandler;
import com.dragome.templates.HTMLTemplateManager;
import com.dragome.templates.interfaces.TemplateManager;

public class MetadataManager
{
	private Map<Class<?>, Class<?>> implementations= new HashMap<Class<?>, Class<?>>();

	public static String getIdFieldOf(Type type)
	{
		return null;
	}

	public static Class<?> getMainInterfaceFor(Object object)
	{
		return getMainInterfaceForClass(object.getClass());
	}

	public static Class<?> getMainInterfaceForClass(Class type)
	{
		if (ApplicationExecutorImpl.class.isAssignableFrom(type))
		{
			return ApplicationExecutor.class;
		}
		else
			return type;
	}

	public Class<?> getImplementationForInterface(Class type)
	{
		ServiceImplementation annotation= (ServiceImplementation) type.getAnnotation(ServiceImplementation.class);
		if (annotation != null)
			return annotation.value();
		else
		{
			Class<?> implementation= implementations.get(type);
			if (implementation != null)
			{
				return implementation;
			}
			else if (ApplicationExecutor.class.isAssignableFrom(type))
			{
				return ApplicationExecutorImpl.class;
			}
			else if (CrossExecutionCommandProcessor.class.isAssignableFrom(type))
			{
				return CrossExecutionCommandProcessorImpl.class;
			}
			else if (TemplateManager.class.isAssignableFrom(type))
			{
				return HTMLTemplateManager.class;
			}
			else if (DomHandler.class.isAssignableFrom(type))
			{
				return BrowserDomHandler.class;
			}
			else if (EventDispatcher.class.isAssignableFrom(type))
			{
				return EventDispatcherImpl.class;
			}
			else if (HtmlTemplateStorage.class.isAssignableFrom(type))
			{
				return HtmlTemplateStorageImpl.class;
			}
			else if (AsyncResponseHandler.class.isAssignableFrom(type))
			{
				return AsyncResponseHandlerImpl.class;
			}
			else
			{
				Set<Class<?>> implementations=ServiceLocator.getInstance().getReflectionService().getSubTypesOf(type);
				if (!implementations.isEmpty())
					return implementations.iterator().next();
			}
		}

		return ServiceLocator.getInstance().getReflectionService().forName(type.getPackage().getName() + ".serverside." + type.getSimpleName() + "Impl");
	}
	public <T> void addService(Class<? extends T> serviceInterface, Class<? extends T> serviceImplementation)
	{
		implementations.put(serviceInterface, serviceImplementation);
	}
}
