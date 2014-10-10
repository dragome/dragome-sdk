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
package com.dragome.services.interfaces;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import com.dragome.commons.DragomeConfigurator;

public interface ReflectionService
{
	public Object getPropertyValue(Object object, String propertyPath);
	public void setPropertyValue(Object object, String propertyPath, Object aValue);
	public <T> T createClassInstance(Class<? extends T> aClass);
	public List<Constructor<Class<?>>> getAllConstructors(Class<?> clazz);
	public Class<?> getRawType(Type type);
	public Set<Class<?>> getAllInterfaces(Class<?> clazz);
	public Object createClassInstance(String className);
	public Class<?> forName(String className);
	public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type);
	public DragomeConfigurator getConfigurator();
}
