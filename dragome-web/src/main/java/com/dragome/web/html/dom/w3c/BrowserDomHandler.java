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
package com.dragome.web.html.dom.w3c;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.DomHandler;

public class BrowserDomHandler implements DomHandler
{
	public BrowserDomHandler()
	{
	}

	public Document getDocument()
	{
		Object documentNode= ScriptHelper.eval("document", this);
		return JsCast.castTo(documentNode, Document.class);
	}

	public Element getElementBySelector(String selector)
	{
		ScriptHelper.put("selector", selector, this);
		Object object= ScriptHelper.eval("document.querySelectorAll(selector)[0]", this);
		Element result= null;
		if (object != null)
			result= JsCast.castTo(object, Element.class);

		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T castTo(Object instance, Class<T> type, Object callerInstance)
	{
		boolean clientSide= WebServiceLocator.getInstance().isClientSide();

		if (instance != null && !clientSide && type.isAssignableFrom(instance.getClass()))
			return (T) instance;

		if (true || clientSide)
			return createCastedInstance(instance, type, callerInstance);
		else
		{
			T result= (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { type }, new AbstractProxyRelatedInvocationHandler()
			{
				private Object castedInstance;

				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
				{
					if (castedInstance == null)
						castedInstance= createCastedInstance(instance, type, callerInstance);

					return method.invoke(castedInstance, args);
				}

			});

			return result;
		}
	}

	public <T> T createCastedInstance(Object instance, Class<T> type, Object callerInstance)
	{
		if (instance == null)
			return null;
		else
			return createDelegateInstance(instance, type, callerInstance);

	}

	private <T> T createDelegateInstance(Object instance, Class<T> type, Object callerInstance)
	{
		try
		{
			String delegateClassName= JsCast.createDelegateClassName(type.getName());
			Class<?> class2= Class.forName(delegateClassName);
			Object newInstance= class2.newInstance();

			ScriptHelper.put("delegate", newInstance, callerInstance);
			ScriptHelper.put("instance", instance, callerInstance);
			ScriptHelper.evalNoResult("delegate.node= instance.node != null ? instance.node : instance", callerInstance);

			return (T) newInstance;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void reset()
	{
	}
}
