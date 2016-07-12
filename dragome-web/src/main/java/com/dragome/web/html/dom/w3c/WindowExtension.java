package com.dragome.web.html.dom.w3c;

import com.dragome.w3c.dom.events.EventListener;
import com.dragome.w3c.dom.html.Window;

public interface WindowExtension extends Window
{
	long requestAnimationFrame(EventListener callback);
	void cancelAnimationFrame(long handle);
}
