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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.html.dom.DomHandler;

public class JsCast
{
	private static DomHandler domHandler= WebServiceLocator.getInstance().getDomHandler();
	private static Map<Class, String> delegateClassNames= new HashMap<Class, String>();

	public static void addEventListener(EventTarget eventTarget, String type, EventListener eventListener, boolean b)
	{
		Class<? extends EventListener> eventListenerClass= eventListener.getClass();
		ScriptHelper.putMethodReference("handleEventMethod", eventListenerClass, null).handleEvent(null);
		ScriptHelper.put("eventListener", eventListener, null);

		ScriptHelper.put("javaRefId", DragomeEntityManager.add(eventListener), null);
		ScriptHelper.evalNoResult("eventListener.javaRefId= javaRefId", null);

		ScriptHelper.put("eventTarget", eventTarget, null);
		ScriptHelper.put("type", type, null);

		String handlerName= "handler_" + type;
		String handlerListenerName= handlerName + "_listener";
		ScriptHelper.put("handlerName", handlerName, null);
		ScriptHelper.put("handlerListenerName", handlerListenerName, null);
		ScriptHelper.evalNoResult("eventTarget.node[handlerName]= handleEventMethod", null);
		ScriptHelper.evalNoResult("eventTarget.node[handlerListenerName]= eventListener", null);
		ScriptHelper.evalNoResult("eventTarget.node.addEventListener(type, (function(event){event.currentTarget[\"" + handlerName + "\"].apply(event.currentTarget[\"" + handlerListenerName + "\"], arguments)}))", null);
	}

	public static void addOnEventListener(EventTarget eventTarget, EventListener eventListener, String methodName)
	{
		Class<? extends EventListener> eventListenerClass= eventListener.getClass();
		ScriptHelper.putMethodReference("handleEventMethod", eventListenerClass, null).handleEvent(null);
		ScriptHelper.put("eventListener", eventListener, null);

		ScriptHelper.put("javaRefId", DragomeEntityManager.add(eventListener), null);
		ScriptHelper.evalNoResult("eventListener.javaRefId= javaRefId", null);

		ScriptHelper.put("eventTarget", eventTarget, null);

		String handlerName= "handler_" + methodName;
		String handlerListenerName= handlerName + "_listener";
		ScriptHelper.put("handlerName", handlerName, null);
		ScriptHelper.put("handlerListenerName", handlerListenerName, null);
		ScriptHelper.evalNoResult("eventTarget.node[handlerName]= handleEventMethod", null);
		ScriptHelper.evalNoResult("eventTarget.node[handlerListenerName]= eventListener", null);
		String script= "eventTarget.node." + methodName + "= (function(event){event.target[\"" + handlerName + "\"].apply(event.target[\"" + handlerListenerName + "\"], arguments)})";
		ScriptHelper.evalNoResult(script, null);
	}

	public static void addEventListener(EventTarget eventTarget, String type, EventListener eventListener)
	{
		addEventListener(eventTarget, type, eventListener, false);
	}

	public static <T> T castTo(Object instance, Class<T> type)
	{
		return domHandler.castTo(instance, type, null);
	}

	public static <T> T createInstanceOf(Class<T> type)
	{
		String script= "new " + type.getSimpleName() + "()";
		return ScriptHelper.evalCasting(script, type, null);
	}

	public static String createDelegateClassName(Class type)
	{
		String result= delegateClassNames.get(type);
		if (result == null)
		{
			String name= type.getName();
			int lastIndexOf= name.lastIndexOf(".");
			result= name.substring(0, lastIndexOf + 1) + "Delegate" + name.substring(lastIndexOf + 1);

			delegateClassNames.put(type, result);
		}

		return result;
	}
}
