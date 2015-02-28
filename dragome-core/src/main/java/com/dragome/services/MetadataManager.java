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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dragome.annotations.ServiceImplementation;

public class MetadataManager
{
	private Map<Class<?>, Class<?>> implementations= new HashMap<Class<?>, Class<?>>();

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
