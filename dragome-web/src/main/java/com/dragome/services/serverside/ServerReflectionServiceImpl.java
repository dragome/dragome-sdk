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
package com.dragome.services.serverside;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.services.ReflectionServiceImpl;
import com.dragome.web.config.DomHandlerApplicationConfigurator;

public class ServerReflectionServiceImpl extends ReflectionServiceImpl
{
	static Reflections reflections= initReflections();

	private static Reflections initReflections()
	{
		return new Reflections( //
				new ConfigurationBuilder().setUrls( //
						ClasspathHelper.forPackage("com")).setScanners( //
								new FieldAnnotationsScanner(), //
								new MethodParameterScanner(), //
								new SubTypesScanner(), // 
								new TypeAnnotationsScanner())//
						.filterInputsBy(//
								new FilterBuilder()//
										.include("com/dragome/.+Configurator.*")//
										.include("com/dragome/.+Compiler.*")//
										.include("com/dragome/.+ApplicationExecutor.*")//
										.include("com/dragome/.+GuiaServiceFactory.*")//
										.include("com/dragome/.+EventDispatcher.*")//
										.include("com/dragome/.+VisualActivity.*")//
										.includePackage("com.fpetrola")));
	}

	//	static Reflections reflections= new Reflections("^");
	//	static Reflections reflections2= new Reflections(".*");

	public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type)
	{
		Set<Class<? extends T>> implementations= reflections.getSubTypesOf(type);
		return implementations;
	}

	public <T> Set<Method> findMethodsThatReturns(final Class<T> type)
	{
		Set<Method> implementations= reflections.getMethodsReturn(type);
		return implementations;
	}

	public static void reset()
	{
		reflections= initReflections();
	}

	public Set<Class<?>> getTypesAnnotatedWith(Class<?> class1)
	{
		return reflections.getTypesAnnotatedWith((Class<? extends Annotation>) class1);
	}

	public DragomeConfigurator getConfigurator()
	{
		try
		{
			DragomeConfigurator foundConfigurator= null;

			Set<Class<?>> typesAnnotatedWith= null;
			typesAnnotatedWith= reflections.getTypesAnnotatedWith(DragomeConfiguratorImplementor.class);
			int priorityMax= -1;
			Class<?> nextClass= null;
			Iterator<Class<?>> iterator= typesAnnotatedWith.iterator();
			while (iterator.hasNext())
			{
				Class<?> next= iterator.next();
				DragomeConfiguratorImplementor annotation= next.getAnnotation(DragomeConfiguratorImplementor.class);
				if (annotation != null)
				{
					int priorityAnno= annotation.priority();
					if (priorityAnno > priorityMax)
					{
						priorityMax= priorityAnno;
						nextClass= next;
					}
				}
			}
			if (nextClass != null)
				foundConfigurator= (DragomeConfigurator) nextClass.newInstance();

			if (foundConfigurator == null)
			{
				Set<Class<? extends DragomeConfigurator>> configurators= reflections.getSubTypesOf(DragomeConfigurator.class);
				for (Class<? extends DragomeConfigurator> class1 : configurators)
				{
					if (!class1.equals(DomHandlerApplicationConfigurator.class))
						foundConfigurator= class1.newInstance();
				}
				if (foundConfigurator == null)
					foundConfigurator= new DomHandlerApplicationConfigurator();
			}

			return foundConfigurator;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
