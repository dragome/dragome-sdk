/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 *  This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.dragome.web.enhancers.jsdelegate;

import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.WebServiceLocator;
import com.dragome.services.interfaces.ServiceFactory;

@SuppressWarnings("unchecked")
public class JsCast
{
	static ServiceFactory serverSideServiceFactory= WebServiceLocator.getInstance().getServerSideServiceFactory();
	public static ElementRepository elementRepository= serverSideServiceFactory.createSyncService(ElementRepository.class);

	static Map<String, ElementData> attributes= new Hashtable<>();

	public static <T> T castTo(Object instance, Class<T> type, Object callerInstance)
	{
		if (instance == null)
			return null;

		ScriptHelper.put("instance", instance, callerInstance);

		if (type.equals(Float.class))
			return (T) new Float(ScriptHelper.evalFloat("instance", callerInstance));
		else if (type.equals(Integer.class))
			return (T) new Integer(ScriptHelper.evalInt("instance", callerInstance));
		else if (type.equals(Double.class))
			return (T) new Double(ScriptHelper.evalDouble("instance", callerInstance));
		else if (type.equals(Long.class))
			return (T) new Long(ScriptHelper.evalLong("instance", callerInstance));
		else if (type.equals(Boolean.class))
			return (T) new Boolean(ScriptHelper.evalBoolean("instance", callerInstance));
		else if (type.equals(Short.class))
			return (T) new Short((short) ScriptHelper.evalInt("instance", callerInstance));
		else if (type.equals(String.class))
			return (T) ScriptHelper.eval("instance", callerInstance);
		else
		{
			Object newInstance= DefaultDelegateStrategy.createDelegateFor(type);

			ScriptHelper.put("delegate", newInstance, callerInstance);
			ScriptHelper.evalNoResult("delegate.node= instance.node != null ? instance.node : instance", callerInstance);

			T result= (T) newInstance;

			return result;
		}
	}

	public static <T> T cacheElement(Object instance, Class<T> type, Object callerInstance)
	{
		boolean isElement= Element.class.isAssignableFrom(type);

		if (isElement && instance != null)
		{
			isElement= false;
			
			if (isElement)
				if (!WebServiceLocator.getInstance().isClientSide())
					instance= unProxy(instance);

			ScriptHelper.put("instance", instance, callerInstance);

			Object newInstance= DefaultDelegateStrategy.createDelegateFor(type);

			ScriptHelper.put("delegate", newInstance, callerInstance);
			ScriptHelper.evalNoResult("delegate.node= instance.node != null ? instance.node : instance", callerInstance);

			T result= (T) newInstance;

			if (Element.class.isAssignableFrom(type))
			{
				CachedElement cachedElement= new CachedElement(null, (Element) newInstance);
				ScriptHelper.put("cachedElement", cachedElement, callerInstance);
				ScriptHelper.evalNoResult("cachedElement.node= instance.node != null ? instance.node : instance", callerInstance);
				result= (T) cachedElement;
			}

			if (isElement)
				if (!WebServiceLocator.getInstance().isClientSide())
				{
					result= (T) Proxy.newProxyInstance(JsCast.class.getClassLoader(), new Class[] { type }, new JsCastInvocationHandler(newInstance));

					ScriptHelper.put("delegateProxy2", newInstance, callerInstance);
					ScriptHelper.put("delegateProxy", result, callerInstance);
					ScriptHelper.evalNoResult("delegateProxy2.node= delegateProxy.node= instance.node != null ? instance.node : instance", callerInstance);
				}

			return result;
		}
		else
			return null;
	}
	
	public static <T> T cacheElement(Object instance, Class<T> type)
	{
		return cacheElement(instance, type, null);
	}

	public static Object unProxy(Object instance)
	{
		if (instance != null && Proxy.isProxyClass(instance.getClass()))
		{
			AbstractProxyRelatedInvocationHandler invocationHandler= (AbstractProxyRelatedInvocationHandler) Proxy.getInvocationHandler(instance);
			instance= invocationHandler.getProxy();
		}
		return instance;
	}

	public static void addEventListener(EventTarget eventTarget, String type, EventListener eventListener, boolean b)
	{
		Class<? extends EventListener> eventListenerClass= eventListener.getClass();
		ScriptHelper.putMethodReference("handleEventMethod", eventListenerClass, null).handleEvent(null);
		ScriptHelper.put("eventListener", eventListener, null);
		Object listener= ScriptHelper.eval("(function(){handleEventMethod.apply(eventListener, arguments)})", null);
		ScriptHelper.put("listener", listener, null);

		ScriptHelper.put("javaRefId", DragomeEntityManager.add(eventListener), null);
		ScriptHelper.eval("eventListener.javaRefId= javaRefId", null);

		ScriptHelper.put("eventTarget", eventTarget, null);
		ScriptHelper.put("type", type, null);
		ScriptHelper.eval("eventTarget.node.addEventListener(type, listener)", null);
	}

	public static void addOnEventListener(EventTarget eventTarget, EventListener eventListener, String methodName)
	{
		Class<? extends EventListener> eventListenerClass= eventListener.getClass();
		ScriptHelper.putMethodReference("handleEventMethod", eventListenerClass, null).handleEvent(null);
		ScriptHelper.put("eventListener", eventListener, null);
		Object listener= ScriptHelper.eval("(function(){handleEventMethod.apply(eventListener, arguments)})", null);
		ScriptHelper.put("listener", listener, null);

		ScriptHelper.put("javaRefId", DragomeEntityManager.add(eventListener), null);
		ScriptHelper.eval("eventListener.javaRefId= javaRefId", null);

		ScriptHelper.put("eventTarget", eventTarget, null);
		String script= "eventTarget.node." + methodName + "= listener";
		ScriptHelper.eval(script, null);
	}

	public static void addEventListener(EventTarget eventTarget, String type, EventListener eventListener)
	{
		addEventListener(eventTarget, type, eventListener, false);
	}

	public static <T> T castTo(Object instance, Class<T> type)
	{
		return castTo(instance, type, null);
	}

	public static <T> T createInstanceOf(Class<T> type)
	{
		String script= "new " + type.getSimpleName() + "()";
		return ScriptHelper.evalCasting(script, type, null);
	}
}
