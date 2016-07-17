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

package com.dragome.web.enhancers.jsdelegate.reflection;

import java.lang.reflect.Proxy;

import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;

public class ElementTransformer
{
	public static <T extends Element> T transformElementTo(final Element foundElement, Class<T> type)
	{
		return wrap(new JsDelegateInitializer()
		{
			public void init(Object proxy)
			{
				ScriptHelper.put("newElement", proxy, this);
				ScriptHelper.put("foundElement", foundElement, this);
				ScriptHelper.eval("newElement.jsDelegate= foundElement.node", this);
			}
		}, type);
	}

	public static <T> T wrap(JsDelegateInitializer jsDelegateInitializer, Class<T> type)
	{
		return (T) Proxy.newProxyInstance(ElementTransformer.class.getClassLoader(), new Class<?>[] { type }, new JsDelegateInvocationHandler(jsDelegateInitializer));
	}
}
