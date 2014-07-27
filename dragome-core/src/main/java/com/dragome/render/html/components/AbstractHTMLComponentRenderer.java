/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.render.html.components;

import java.util.EventListener;

import org.w3c.dom.Element;

import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.listeners.BlurListener;
import com.dragome.model.listeners.ClickListener;
import com.dragome.model.listeners.DoubleClickListener;
import com.dragome.model.listeners.InputListener;
import com.dragome.model.listeners.KeyDownListener;
import com.dragome.model.listeners.KeyPressListener;
import com.dragome.model.listeners.KeyUpListener;
import com.dragome.model.listeners.MouseOutListener;
import com.dragome.model.listeners.MouseOverListener;
import com.dragome.render.interfaces.ComponentRenderer;

public abstract class AbstractHTMLComponentRenderer<T> implements ComponentRenderer<Element, T>
{
	public static final String COMPONENT_ID_ATTRIBUTE= "data-component-id";

	public AbstractHTMLComponentRenderer()
	{
	}

	public void addListeners(final VisualComponent visualComponent, final Element element)
	{
		addListener(visualComponent, element, ClickListener.class, "onclick");
		addListener(visualComponent, element, DoubleClickListener.class, "ondblclick");
		addListener(visualComponent, element, KeyUpListener.class, "onkeyup");
		addListener(visualComponent, element, KeyDownListener.class, "onkeydown");
		addListener(visualComponent, element, KeyPressListener.class, "onkeypress");
		addListener(visualComponent, element, InputListener.class, "oninput");
		addListener(visualComponent, element, MouseOverListener.class, "onmouseover");
		addListener(visualComponent, element, MouseOutListener.class, "onmouseout");
		addListener(visualComponent, element, BlurListener.class, "onblur");
	}

	protected void addListener(final VisualComponent visualComponent, final Element element, Class<? extends EventListener> listenerType, String jsAttributeName)
	{
		if (visualComponent.hasListener(listenerType))
			element.setAttribute(jsAttributeName, "_ed.onEvent(event)");
	}
}
