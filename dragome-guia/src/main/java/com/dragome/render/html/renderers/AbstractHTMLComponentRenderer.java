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

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.KeyboardEvent;

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
import com.dragome.guia.events.listeners.interfaces.MouseDownListener;
import com.dragome.guia.events.listeners.interfaces.MouseOutListener;
import com.dragome.guia.events.listeners.interfaces.MouseOverListener;
import com.dragome.guia.events.listeners.interfaces.MouseUpListener;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.web.dispatcher.EventDispatcherImpl;

public abstract class AbstractHTMLComponentRenderer<T> implements ComponentRenderer<Element, T>
{
	private static final String KEYPRESS= "keypress";
	private static final String KEYDOWN= "keydown";
	private static final String KEYUP= "keyup";
	private static final String INPUT= "input";
	private static final String BLUR= "blur";
	private static final String MOUSEOUT= "mouseout";
	private static final String MOUSEOVER= "mouseover";
	private static final String MOUSEDOWN= "mousedown";
	private static final String MOUSEUP= "mouseup";
	private static final String DBLCLICK= "dblclick";
	private static final String CLICK= "click";
	public static final String COMPONENT_ID_ATTRIBUTE= "data-component-id";

	private List<String> listeners= new ArrayList<String>();

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
		addListener(visualComponent, element, ClickListener.class, CLICK, expectedType);
		addListener(visualComponent, element, DoubleClickListener.class, DBLCLICK, expectedType);
		addListener(visualComponent, element, KeyUpListener.class, KEYUP, expectedType);
		addListener(visualComponent, element, KeyDownListener.class, KEYDOWN, expectedType);
		addListener(visualComponent, element, KeyPressListener.class, KEYPRESS, expectedType);
		addListener(visualComponent, element, InputListener.class, INPUT, expectedType);
		addListener(visualComponent, element, MouseOverListener.class, MOUSEOVER, expectedType);
		addListener(visualComponent, element, MouseOutListener.class, MOUSEOUT, expectedType);
		addListener(visualComponent, element, BlurListener.class, BLUR, expectedType);

		EventDispatcherImpl.setEventListener(element, new org.w3c.dom.events.EventListener()
		{
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

				else if (event instanceof KeyboardEvent)
				{
					KeyboardEvent keyboardEvent= (KeyboardEvent) event;
					int keyId= Integer.parseInt(keyboardEvent.getKeyIdentifier());
					if (type.equals(KEYUP))
						visualComponent.getListener(KeyUpListener.class).keyupPerformed(visualComponent, keyId);
					else if (type.equals(KEYDOWN))
						visualComponent.getListener(KeyDownListener.class).keydownPerformed(visualComponent, keyId);
					else if (type.equals(KEYPRESS))
						visualComponent.getListener(KeyPressListener.class).keypressPerformed(visualComponent, keyId);
				}

			}
		}, listeners.toArray(new String[0]));
	}
	protected void addListener(final VisualComponent visualComponent, final Element element, Class<? extends EventListener> listenerType, String jsAttributeName, Class<? extends EventListener> expectedType)
	{
		if (visualComponent.hasListener(listenerType) && (expectedType == null || expectedType.equals(listenerType)))
			listeners.add(jsAttributeName);

		//			element.setAttribute(jsAttributeName, "_ed.onEvent()");
	}
}
