package com.dragome.render.html.renderers;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.KeyboardEvent;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.events.listeners.interfaces.BlurListener;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.guia.events.listeners.interfaces.DoubleClickListener;
import com.dragome.guia.events.listeners.interfaces.InputListener;
import com.dragome.guia.events.listeners.interfaces.KeyDownListener;
import com.dragome.guia.events.listeners.interfaces.KeyPressListener;
import com.dragome.guia.events.listeners.interfaces.KeyUpListener;
import com.dragome.guia.events.listeners.interfaces.MouseDownListener;
import com.dragome.guia.events.listeners.interfaces.MouseOutListener;
import com.dragome.guia.events.listeners.interfaces.MouseOverListener;
import com.dragome.guia.events.listeners.interfaces.MouseUpListener;
import com.dragome.web.enhancers.jsdelegate.JsCast;

public class MultipleEventListener<T> implements EventListener
{
	private final VisualComponent visualComponent;

	public static final String KEYPRESS= "keypress";
	public static final String KEYDOWN= "keydown";
	public static final String KEYUP= "keyup";
	public static final String INPUT= "input";
	public static final String BLUR= "blur";
	public static final String MOUSEOUT= "mouseout";
	public static final String MOUSEOVER= "mouseover";
	public static final String MOUSEDOWN= "mousedown";
	public static final String MOUSEUP= "mouseup";
	public static final String DBLCLICK= "dblclick";
	public static final String CLICK= "click";

	public MultipleEventListener(VisualComponent visualComponent)
	{
		this.visualComponent= visualComponent;
	}

	public void handleEvent(Event event)
	{
		String type= event.getType();

		if (type.equals(CLICK))
			visualComponent.getListener(ClickListener.class).clickPerformed(visualComponent);
		else if (type.equals(DBLCLICK))
			visualComponent.getListener(DoubleClickListener.class).doubleClickPerformed(visualComponent);
		else if (type.equals(MOUSEOVER))
			visualComponent.getListener(MouseOverListener.class).mouseOverPerformed(visualComponent);
		else if (type.equals(MOUSEOUT))
			visualComponent.getListener(MouseOutListener.class).mouseOutPerformed(visualComponent);
		else if (type.equals(MOUSEDOWN))
			visualComponent.getListener(MouseDownListener.class).mouseDownPerformed(null);
		else if (type.equals(MOUSEUP))
			visualComponent.getListener(MouseUpListener.class).mouseUpPerformed(visualComponent);
		else if (type.equals(BLUR))
			visualComponent.getListener(BlurListener.class).blurPerformed(visualComponent);
		else if (type.equals(INPUT))
			visualComponent.getListener(InputListener.class).inputPerformed(visualComponent);
		else
		{
			KeyboardEvent keyboardEvent= JsCast.castTo(event, KeyboardEvent.class);
			if (type.equals(KEYUP))
				visualComponent.getListener(KeyUpListener.class).keyupPerformed(visualComponent, keyboardEvent.getKeyCode());
			else if (type.equals(KEYDOWN))
				visualComponent.getListener(KeyDownListener.class).keydownPerformed(visualComponent, keyboardEvent.getKeyCode());
			else if (type.equals(KEYPRESS))
				visualComponent.getListener(KeyPressListener.class).keypressPerformed(visualComponent, keyboardEvent.getKeyCode());
		}
	}
}