package com.dragome.model;

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