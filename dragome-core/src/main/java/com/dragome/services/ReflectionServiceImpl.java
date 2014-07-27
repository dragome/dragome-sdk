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

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
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
		//		Reflections reflections= new Reflections("");
		//		Set<Class<?>> implementations= reflections.getSubTypesOf(type);

		return null;
	}
	@Override
    public DragomeConfigurator getConfigurator()
    {
	    return null;
    }
}
