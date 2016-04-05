package com.dragome.web.debugging;

import org.w3c.dom.events.Event;

public interface MessageEvent extends Event
{
	public String getData();
	public String getResponseBody();
}