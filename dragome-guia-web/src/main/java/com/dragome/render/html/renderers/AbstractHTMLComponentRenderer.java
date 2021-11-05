/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.render.html.renderers;

import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.events.listeners.interfaces.BlurListener;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.guia.events.listeners.interfaces.DoubleClickListener;
import com.dragome.guia.events.listeners.interfaces.InputListener;
import com.dragome.guia.events.listeners.interfaces.KeyDownListener;
import com.dragome.guia.events.listeners.interfaces.KeyPressListener;
import com.dragome.guia.events.listeners.interfaces.KeyUpListener;
import com.dragome.guia.events.listeners.interfaces.ListenerChanged;
import com.dragome.guia.events.listeners.interfaces.MouseOutListener;
import com.dragome.guia.events.listeners.interfaces.MouseOverListener;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;

public abstract class AbstractHTMLComponentRenderer<T> implements ComponentRenderer<Element, T>
{
	public static final String COMPONENT_ID_ATTRIBUTE= "data-component-id";

	private Set<String> listeners= new HashSet<String>();

	public AbstractHTMLComponentRenderer()
	{
	}

	public static void setElementInnerHTML(Element label1, String aText)
	{
		ScriptHelper.put("element", label1, null);
		ScriptHelper.put("value", aText, null);
		ScriptHelper.evalNoResult("element.node.innerHTML= value", null);
	}

	public static String getElementInnerHTML(Element label1)
	{
		ScriptHelper.put("element", label1, null);
		return (String) ScriptHelper.eval("element.node.innerHTML", null);
	}

	public void addListeners(final VisualComponent visualComponent, final Element element)
	{
		element.setAttribute(COMPONENT_ID_ATTRIBUTE, DragomeEntityManager.add(visualComponent));

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
		addListener(visualComponent, element, ClickListener.class, MultipleEventListener.CLICK, expectedType);
		addListener(visualComponent, element, DoubleClickListener.class, MultipleEventListener.DBLCLICK, expectedType);
		addListener(visualComponent, element, KeyUpListener.class, MultipleEventListener.KEYUP, expectedType);
		addListener(visualComponent, element, KeyDownListener.class, MultipleEventListener.KEYDOWN, expectedType);
		addListener(visualComponent, element, KeyPressListener.class, MultipleEventListener.KEYPRESS, expectedType);
		addListener(visualComponent, element, InputListener.class, MultipleEventListener.INPUT, expectedType);
		addListener(visualComponent, element, MouseOverListener.class, MultipleEventListener.MOUSEOVER, expectedType);
		addListener(visualComponent, element, MouseOutListener.class, MultipleEventListener.MOUSEOUT, expectedType);
		addListener(visualComponent, element, BlurListener.class, MultipleEventListener.BLUR, expectedType);

		EventTarget eventTarget= JsCast.castTo(element, EventTarget.class);

		for (String listener : listeners)
			eventTarget.addEventListener(listener, new MultipleEventListener<T>(visualComponent), false);
	}

	protected void addListener(final VisualComponent visualComponent, final Element element, Class<? extends EventListener> listenerType, String jsAttributeName, Class<? extends EventListener> expectedType)
	{
		if (visualComponent.hasListener(listenerType) && (expectedType == null || expectedType.equals(listenerType)))
			listeners.add(jsAttributeName);

		//			element.setAttribute(jsAttributeName, "_ed.onEvent()");
	}
	
	public boolean matches(T aVisualComponent, Template child)
	{
		return false;
	}
}
