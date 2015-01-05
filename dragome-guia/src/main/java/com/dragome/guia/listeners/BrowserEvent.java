package com.dragome.guia.listeners;

import org.w3c.dom.Element;

public interface BrowserEvent
{
	String getType();
	int getKeyCode();
	Element getTargetElement();
}
