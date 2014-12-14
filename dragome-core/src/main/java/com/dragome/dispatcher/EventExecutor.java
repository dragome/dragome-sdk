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
package com.dragome.model;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.html.dom.EventDispatcher;
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
				int clientX= ScriptHelper.evalInt("event.layerX", this);
				int clientY= ScriptHelper.evalInt("event.layerY", this);
				//				int offsetLeft= ScriptHelper.evalInt("eventTarget.offsetLeft", this);
				//				int offsetTop= ScriptHelper.evalInt("eventTarget.offsetTop", this);
				boolean shiftKey= ScriptHelper.evalBoolean("event.shiftKey", this);
				eventDispatcher.mouseEventPerformedById(eventType, id, clientX, clientY, shiftKey);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
