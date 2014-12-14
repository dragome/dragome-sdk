package com.dragome.model.listeners;

import org.w3c.dom.Element;

public interface BrowserEvent
{
	String getType();
	int getKeyCode();
	Element getTargetElement();
}
