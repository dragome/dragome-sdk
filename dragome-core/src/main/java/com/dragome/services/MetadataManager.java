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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dragome.annotations.ServiceImplementation;
import com.dragome.debugging.CrossExecutionCommandProcessorImpl;
import com.dragome.debugging.interfaces.CrossExecutionCommandProcessor;
import com.dragome.dispatcher.EventDispatcher;
import com.dragome.dispatcher.EventDispatcherImpl;
import com.dragome.execution.ApplicationExecutor;
import com.dragome.execution.ApplicationExecutorImpl;
import com.dragome.html.dom.DomHandler;
import com.dragome.html.dom.w3c.BrowserDomHandler;
import com.dragome.services.interfaces.AsyncResponseHandler;

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
//			else if (TemplateManager.class.isAssignableFrom(type))
//			{
//				return HTMLTemplateManager.class;
//			}
			else if (DomHandler.class.isAssignableFrom(type))
			{
				return BrowserDomHandler.class;
			}
			else if (EventDispatcher.class.isAssignableFrom(type))
			{
				return EventDispatcherImpl.class;
			}
//			else if (HtmlTemplateStorage.class.isAssignableFrom(type))
//			{
//				return HtmlTemplateStorageImpl.class;
//			}
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
