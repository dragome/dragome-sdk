package com.dragome.guia.listeners;

import java.util.EventListener;

public interface BrowserEventListener extends EventListener
{
	public void onBrowserEvent(BrowserEvent event);
}
