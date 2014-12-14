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
package com.dragome.dispatcher;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.ServiceLocator;

public class EventExecutor implements Runnable
{
	private final Object event;

	protected EventExecutor(Object event)
	{
		this.event= event;
	}

	public void run()
	{
		try
		{
			ServiceLocator instance= ServiceLocator.getInstance();
			EventDispatcher eventDispatcher= instance.getEventDispatcher();

			String id= EventDispatcherHelper.getEventTargetId(event);

			ScriptHelper.put("event", event, this);
			String eventType= (String) ScriptHelper.eval("event.type", this);

			boolean isKeyEvent= eventType.equals("keyup") || eventType.equals("keydown") || eventType.equals("keypress");
			ScriptHelper.put("eventTarget", EventDispatcherHelper.getEventTarget(event), this);
			if (isKeyEvent)
			{
				ScriptHelper.eval("stopEvent(event)", this);
				int code= ScriptHelper.evalInt("event.keyCode", this);
				eventDispatcher.keyEventPerformedById(eventType, id, code);
			}
			else
			{
				int clientX= ScriptHelper.evalInt("event.layerX === undefined ? 0 : event.layerX", this);
				int clientY= ScriptHelper.evalInt("event.layerY === undefined ? 0 : event.layerY", this);
				boolean shiftKey= ScriptHelper.evalBoolean("event.shiftKey === undefined ? false : event.shiftKey", this);
				eventDispatcher.mouseEventPerformedById(eventType, id, clientX, clientY, shiftKey);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
