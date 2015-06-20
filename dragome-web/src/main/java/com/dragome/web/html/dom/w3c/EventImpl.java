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

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventImpl implements Event
{
	private String eventType;

	public EventImpl(String eventType)
	{
		this.eventType= eventType;
	}

	public void stopPropagation()
	{
		// TODO Auto-generated method stub

	}

	public void preventDefault()
	{
		// TODO Auto-generated method stub

	}

	public void initEvent(String arg0, boolean arg1, boolean arg2)
	{
		// TODO Auto-generated method stub

	}

	public String getType()
	{
		return eventType;
	}

	public long getTimeStamp()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public EventTarget getTarget()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public short getEventPhase()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public EventTarget getCurrentTarget()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getCancelable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getBubbles()
	{
		// TODO Auto-generated method stub
		return false;
	}
}