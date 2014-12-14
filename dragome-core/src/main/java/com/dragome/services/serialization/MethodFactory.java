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
package com.dragome.services.serialization;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.dragome.helpers.TimeCollector;
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
