package com.dragome.guia.events.listeners.interfaces;

import org.w3c.dom.Element;

public interface BrowserEvent
{
	String getType();
	int getKeyCode();
	Element getTargetElement();
}
