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

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;

import org.w3c.dom.Element;

import com.dragome.helpers.DragomeEntityManager;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.enhancers.jsdelegate.JsCastInvocationHandler;

import flexjson.ObjectBinder;
import flexjson.factories.BeanObjectFactory;

public final class ElementFactory extends BeanObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		String id= ((Map<String, String>) value).get("id");

		Object object= DragomeEntityManager.get(id);

		if (object != null)
		{
			return object;
		}
		else
		{
			Object instantiate= super.instantiate(context, value, targetType, targetClass);
			
			
			Object newProxyInstance= Proxy.newProxyInstance(JsCast.class.getClassLoader(), new Class[] { Element.class }, new JsCastInvocationHandler(instantiate));

			DragomeEntityManager.put(id, instantiate);
			return newProxyInstance;
		}
	}
}
