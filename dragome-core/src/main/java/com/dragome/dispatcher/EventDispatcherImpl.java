/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.model;

import org.w3c.dom.Element;
import org.w3c.dom.events.EventListener;

import com.dragome.html.dom.EventDispatcher;
import com.dragome.html.dom.w3c.KeyboardEventImpl;
import com.dragome.html.dom.w3c.MouseEventImpl;
import com.dragome.remote.entities.DragomeEntityManager;

public class EventDispatcherImpl implements EventDispatcher
{
	private static boolean processingEvent= false;
	public static final String ELEMENT_ID_ATTRIBUTE= "data-element-id";

	public EventDispatcherImpl()
	{
	}

	private synchronized void runOnlySynchronized(Runnable runnable)
	{
		try
		{
			//TODO revisar esto cuando se ejecuta en el cliente, posible freeze!
			if (!processingEvent)
			{
				processingEvent= true;
				runnable.run();
			}
		}
		finally
		{
			processingEvent= false;
		}
	}

	public static void setEventListener(Element element, EventListener eventListener, String... eventTypes)
	{
		for (String eventType : eventTypes)
			element.setAttribute("on" + eventType, "_ed.onEvent()");

		element.setAttribute(ELEMENT_ID_ATTRIBUTE, DragomeEntityManager.add(eventListener));
	}

	public void keyEventPerformedById(final String eventName, final String id, final int code)
	{
		runOnlySynchronized(new Runnable()
		{
			public void run()
			{
				Object object= DragomeEntityManager.get(id);
				if (object instanceof EventListener)
					((EventListener) object).handleEvent(new KeyboardEventImpl(eventName, code));
			}
		});
	}

	public void mouseEventPerformedById(final String eventName, final String id, final int clientX, final int clientY, final boolean shiftKey)
	{
		runOnlySynchronized(new Runnable()
		{
			public void run()
			{
				Object object= DragomeEntityManager.get(id);
				if (object instanceof EventListener)
					((EventListener) object).handleEvent(new MouseEventImpl(eventName, clientX, clientY, shiftKey));
			}
		});
	}
}
