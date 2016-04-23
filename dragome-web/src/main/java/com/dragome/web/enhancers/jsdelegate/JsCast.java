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

public class JsCast
{
	@SuppressWarnings("unchecked")
	public static <T> T castTo(Object instance, Class<T> type, Object callerInstance)
	{
		try
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
				String delegateClassName= JsDelegateGenerator.createDelegateClassName(type.getName());
				Class<?> class2= Class.forName(delegateClassName);
				Object newInstance= class2.newInstance();

				ScriptHelper.put("delegate", newInstance, callerInstance);
				if (ScriptHelper.eval("instance.node", callerInstance) == null)
					ScriptHelper.eval("delegate.node= instance", callerInstance);
				else
					ScriptHelper.eval("delegate.node= instance.node", callerInstance);

				return (T) newInstance;
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
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
