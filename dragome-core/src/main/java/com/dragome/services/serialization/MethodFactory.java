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
package com.dragome.services.serialization;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.dragome.remote.TimeCollector;
import com.dragome.services.ServiceLocator;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public final class MethodFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		TimeCollector timeCollector= ServiceLocator.getInstance().getTimeCollector();
		timeCollector.registerStart("instantiate method");

		String methodSignature= (String) value;
		String className= methodSignature.substring(0, methodSignature.lastIndexOf("."));
		String methodName= methodSignature.substring(methodSignature.lastIndexOf(".") + 1, methodSignature.lastIndexOf(":"));
		int parametersCount= Integer.parseInt(methodSignature.substring(methodSignature.lastIndexOf(":") + 1));
		try
		{
			Method[] methods= Class.forName(className).getMethods();
			for (Method method : methods)
			{
				if (method.getName().equals(methodName) && method.getParameterTypes().length == parametersCount)
					return method;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			timeCollector.registerEnd();
		}
		return null;
	}
}
