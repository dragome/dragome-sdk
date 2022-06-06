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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.helpers.ReflectionHelper;
import com.dragome.services.interfaces.ReflectionService;

public class ReflectionServiceImpl implements ReflectionService
{
	public Object getPropertyValue(Object object, String aPropertyName)
	{
		return ReflectionHelper.getPropertyValue(object, aPropertyName);
	}
	public <T> T createClassInstance(Class<? extends T> aClass)
	{
		return ReflectionHelper.createClassInstance(aClass);
	}
	public List<Constructor<Class<?>>> getAllConstructors(Class<?> clazz)
	{
		return ReflectionHelper.getAllConstructors(clazz);
	}
	public Class<?> getRawType(Type type)
	{
		return ReflectionHelper.getRawType(type);
	}
	public Set<Class<?>> getAllInterfaces(Class<?> clazz)
	{
		return ReflectionHelper.getAllInterfaces(clazz);
	}
	public void setPropertyValue(Object object, String aPropertyName, Object aValue)
	{
		ReflectionHelper.setPropertyValue(object, aPropertyName, aValue);
	}
	public Object createClassInstance(String className)
	{
		return ReflectionHelper.createClassInstance(className);
	}
	public Class<?> forName(String className)
	{
		return ReflectionHelper.forName(className);
	}

	public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type)
	{
		Class<? extends T> result= getHardcodedImplementation(type);

		//		Reflections reflections= new Reflections("");
		//		Set<Class<?>> implementations= reflections.getSubTypesOf(type);

		return result != null ? new HashSet<Class<? extends T>>(Arrays.asList(result)) : null;
	}
	private <T> Class<? extends T> getHardcodedImplementation(final Class<T> type)
	{
		String[] implementations= new String[] { // 
				"com.dragome.templates.interfaces.TemplateLoadingStrategy", "com.dragome.render.html.HTMLTemplateLoadingStrategy", //
				"com.dragome.guia.GuiaServiceFactory", "com.dragome.render.html.HTMLGuiaServiceFactory", //
				"com.dragome.web.debugging.interfaces.CrossExecutionCommandProcessor", "com.dragome.web.debugging.CrossExecutionCommandProcessorImpl" };

		for (int i= 0; i < implementations.length; i++)
		{
			Class<? extends T> result= null;
			String checkType= implementations[i];
			String testImplementation= implementations[i + 1];

			if (type.getName().equals(checkType))
				try
				{
					result= (Class<? extends T>) Class.forName(testImplementation);
					return result;
				}
				catch (ClassNotFoundException e)
				{
				}
		}

		return null;
	}
	@Override
	public DragomeConfigurator getConfigurator()
	{
		return null;
	}
	@Override
	public Set<Class<?>> getTypesAnnotatedWith(Class<?> class1)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T> Set<Method> findMethodsThatReturns(Class<T> type)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
