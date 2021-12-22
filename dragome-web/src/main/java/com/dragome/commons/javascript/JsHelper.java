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
package com.dragome.commons.javascript;

import java.lang.reflect.Proxy;

import org.w3c.dom.Element;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.enhancers.jsdelegate.CachedElement;

public final class JsHelper
{
	public static Object unProxy(Object instance)
	{
		if (!WebServiceLocator.getInstance().isClientSide() && instance != null && Proxy.isProxyClass(instance.getClass()))
		{
			AbstractProxyRelatedInvocationHandler invocationHandler= (AbstractProxyRelatedInvocationHandler) Proxy.getInvocationHandler(instance);
			instance= invocationHandler.getProxy();
		}
		return getFinalElement(instance);
	}

	public static Object getFinalElement(Object element)
	{
		if (element instanceof CachedElement)
		{
			CachedElement cachedElement= (CachedElement) element;
			return cachedElement.getElement();
		}
		else
			return element;
	}
}
