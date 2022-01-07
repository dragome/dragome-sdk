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

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;

import org.cobraparser.DummyHtmlRendererContext;
import org.cobraparser.html.domimpl.HTMLDocumentImpl;
import org.cobraparser.html.domimpl.NodeImpl;
import org.cobraparser.html.parser.HtmlParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.ProxyRelatedInvocationHandler;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.html.dom.DomHandler;

@SuppressWarnings("unchecked")
public class CobraDomHandler implements DomHandler
{

	private Document result;

	public CobraDomHandler()
	{
	}

	public Document getDocument()
	{
		if (result == null)
			result= createCobraDocument();

		return result;
	}

	static DummyHtmlRendererContext rendererContext= new DummyHtmlRendererContext();
	public static HTMLDocumentImpl htmlDocumentImpl= new HTMLDocumentImpl(rendererContext);

	public Document createCobraDocument()
	{
		return (Document) createProxy(new CombinedDomInstance(htmlDocumentImpl, createRemoteDocumentInstance(), true));
	}

	public Document createRemoteDocumentInstance()
	{
		try
		{
			HtmlParser htmlParser= new HtmlParser(rendererContext.getUserAgentContext(), htmlDocumentImpl);
			String documentHtml= (String) ScriptHelper.eval("document.documentElement.innerHTML", this);
			htmlParser.parse(new StringReader(documentHtml));

			Object documentNode= ScriptHelper.eval("document", this);
			return new BrowserDomHandler().castTo(documentNode, Document.class, this);
			//			
			//			Document documentHtml1= ScriptHelper.evalCasting("document", Document.class, this);
			//			return documentHtml1;
		}
		catch (IOException | SAXException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Object wrapResult(Object result, Class<?>... returnType)
	{
		if (result == null || returnType[0].isPrimitive() || result instanceof String || result instanceof Number)
			return result;
		else
			return createProxy(CombinedDomInstance.getFromLocal(result));
	}

	public Object createProxy(CombinedDomInstance combinedDomInstance)
	{
		AbstractProxyRelatedInvocationHandler h= new AbstractProxyRelatedInvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				Object invoke= combinedDomInstance.invoke(method, args);
				return wrapResult(invoke, method.getReturnType());
			}
		};

		h.setProxy(combinedDomInstance);
		return Proxy.newProxyInstance(getClass().getClassLoader(), combinedDomInstance.getProxyInterfaces(combinedDomInstance.getLocalInstance()), h);
	}

	public Element getElementBySelector(String selector)
	{

		Element result= ((NodeImpl) htmlDocumentImpl).querySelector(selector);
		//		ScriptHelper.put("selector", selector, this);
		//		Object object= ScriptHelper.eval("document.querySelectorAll(selector)[0]", this);
		//		Element result= null;
		//		if (object != null)
		//			result= JsCast.castTo(object, Element.class);
		//
		return result;
	}

	public <T> T castTo(Object instance, Class<T> type, Object callerInstance)
	{
		if (instance == null || type == null)
			return null;
		else if (Proxy.isProxyClass(instance.getClass()))
		{
			ProxyRelatedInvocationHandler invocationHandler= (ProxyRelatedInvocationHandler) Proxy.getInvocationHandler(instance);
			CombinedDomInstance combinedDomInstance= (CombinedDomInstance) invocationHandler.getProxy();
			Object remoteInstance= combinedDomInstance.getRemoteInstance();

			T castTo;
			List<Class<?>> interfaces= combinedDomInstance.getInterfaces(instance);
			interfaces.add(type);
			Class[] interfacesArray= new HashSet<>(interfaces).toArray(new Class[0]);

			if (Proxy.isProxyClass(remoteInstance.getClass()))
			{
				AbstractProxyRelatedInvocationHandler h= new AbstractProxyRelatedInvocationHandler()
				{
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
					{
						Method actualMethod= CombinedDomInstance.getActualMethod(method, remoteInstance);
						Object invoke= actualMethod.invoke(remoteInstance, args);
						return invoke;
					}
				};
				h.setProxy(combinedDomInstance);
				castTo= (T) Proxy.newProxyInstance(getClass().getClassLoader(), interfacesArray, h);
			}
			else
				castTo= new BrowserDomHandler().castTo(remoteInstance, type, callerInstance);

			combinedDomInstance.setRemoteInstance(castTo);
			combinedDomInstance.updateMaps(combinedDomInstance.getLocalInstance(), castTo, combinedDomInstance);
			return (T) instance;
		}
		else
			return new BrowserDomHandler().castTo(instance, type, callerInstance);
	}
}
