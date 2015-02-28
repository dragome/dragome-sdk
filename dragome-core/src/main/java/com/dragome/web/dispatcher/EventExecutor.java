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
package com.dragome.web.dispatcher;

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
