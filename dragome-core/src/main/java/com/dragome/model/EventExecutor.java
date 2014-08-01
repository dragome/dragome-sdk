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
import com.dragome.model.listeners.EventDispatcher;
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
			String id= EventDispatcherHelper.getEventTargetId(event);
			Object arguments= null;

			ScriptHelper.put("event", event, this);
			String eventType= (String) ScriptHelper.eval("event.type", this);
			
			boolean isKeyEvent= eventType.equals("keyup") || eventType.equals("keydown")|| eventType.equals("keypress");
			if (isKeyEvent)
			{
				ScriptHelper.put("eventTarget", EventDispatcherHelper.getEventTarget(event), this);
				ScriptHelper.eval("stopEvent(event)", this);
				int code= ScriptHelper.evalInt("event.keyCode", this);
				arguments= code;
			}

			ServiceLocator instance= ServiceLocator.getInstance();
			EventDispatcher eventDispatcher= instance.getEventDispatcher();
			eventDispatcher.eventPerformedById(eventType, id, arguments);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
