package com.dragome.web.html.dom.w3c;

import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.Window;

public interface WindowExtension extends Window
{
	long requestAnimationFrame(EventListener callback);
	void cancelAnimationFrame(long handle);
}
