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
package com.dragome.services.interfaces;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
	public Set<Class<?>> getTypesAnnotatedWith(Class<?> class1);
	<T> Set<Method> findMethodsThatReturns(Class<T> type);
}
