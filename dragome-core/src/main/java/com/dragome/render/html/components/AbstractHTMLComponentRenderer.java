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
import com.dragome.model.listeners.ListenerChanged;
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
		visualComponent.addListener(ListenerChanged.class, new ListenerChanged()
		{
			public <T extends EventListener> void listenerAdded(Class<? extends T> type, T listener)
			{
				addListeners(visualComponent, element, type);
			}

			public <T extends EventListener> void listenerRemoved(Class<? extends T> type, T listener)
			{
			}
		});

		addListeners(visualComponent, element, null);

		visualComponent.getStyle().fireStyleChanged();
	}

	private void addListeners(final VisualComponent visualComponent, final Element element, Class<? extends EventListener> expectedType)
	{
		addListener(visualComponent, element, ClickListener.class, "onclick", expectedType);
		addListener(visualComponent, element, DoubleClickListener.class, "ondblclick", expectedType);
		addListener(visualComponent, element, KeyUpListener.class, "onkeyup", expectedType);
		addListener(visualComponent, element, KeyDownListener.class, "onkeydown", expectedType);
		addListener(visualComponent, element, KeyPressListener.class, "onkeypress", expectedType);
		addListener(visualComponent, element, InputListener.class, "oninput", expectedType);
		addListener(visualComponent, element, MouseOverListener.class, "onmouseover", expectedType);
		addListener(visualComponent, element, MouseOutListener.class, "onmouseout", expectedType);
		addListener(visualComponent, element, BlurListener.class, "onblur", expectedType);
	}

	protected void addListener(final VisualComponent visualComponent, final Element element, Class<? extends EventListener> listenerType, String jsAttributeName, Class<? extends EventListener> expectedType)
	{
		if (visualComponent.hasListener(listenerType) && (expectedType == null || expectedType.equals(listenerType)))
			element.setAttribute(jsAttributeName, "_ed.onEvent()");
	}
}
