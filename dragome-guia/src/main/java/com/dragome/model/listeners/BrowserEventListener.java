package com.dragome.model.listeners;

import java.util.EventListener;

public interface BrowserEventListener extends EventListener
{
	public void onBrowserEvent(BrowserEvent event);
}
