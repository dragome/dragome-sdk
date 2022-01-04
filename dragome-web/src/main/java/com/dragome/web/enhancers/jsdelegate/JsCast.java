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

import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.WebServiceLocator;

public class JsCast
{
	

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
		return WebServiceLocator.getInstance().getDomHandler().castTo(instance, type, null);
	}

	public static <T> T createInstanceOf(Class<T> type)
	{
		String script= "new " + type.getSimpleName() + "()";
		return ScriptHelper.evalCasting(script, type, null);
	}

	public static String createDelegateClassName(String type)
	{
		int lastIndexOf= type.lastIndexOf(".");
		String classname= type.substring(0, lastIndexOf + 1) + "Delegate" + type.substring(lastIndexOf + 1);
		return classname;
	}
}
