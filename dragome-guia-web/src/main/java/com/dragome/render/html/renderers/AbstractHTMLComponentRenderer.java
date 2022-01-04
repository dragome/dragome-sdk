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
import com.dragome.render.DomHelper;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.TemplateChangeListener;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.ElementExtension;

public abstract class AbstractHTMLComponentRenderer<T extends VisualComponent> implements ComponentRenderer<Element, T>
{
	public static final String COMPONENT_ID_ATTRIBUTE= "data-component-id";

	private Set<String> listeners= new HashSet<String>();

	public AbstractHTMLComponentRenderer()
	{
	}

	public static void setElementInnerHTML(Element label1, String aText)
	{
		ElementExtension elementExtension= JsCast.castTo(label1, ElementExtension.class);
		elementExtension.setInnerHTML(aText);

		//		
		//		ScriptHelper.put("element", label1, null);
		//		ScriptHelper.put("value", aText, null);
		//		ScriptHelper.evalNoResult("element.node.innerHTML= value", null);
	}

	public static String getElementInnerHTML(Element label1)
	{
		ElementExtension elementExtension= JsCast.castTo(label1, ElementExtension.class);
		return elementExtension.getInnerHTML();

		//		ScriptHelper.put("element", label1, null);
		//		return (String) ScriptHelper.eval("element.node.innerHTML", null);
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

		visualComponent.getStyle().setName(element.getAttribute("class"));
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

		ElementExtension eventTarget= JsCast.castTo(element, ElementExtension.class);

//		for (String listener : listeners)
//			eventTarget.addEventListener(listener, new MultipleEventListener<T>(visualComponent), false);
	}

	protected void addListener(final VisualComponent visualComponent, final Element element, Class<? extends EventListener> listenerType, String jsAttributeName, Class<? extends EventListener> expectedType)
	{
		if (visualComponent.hasListener(listenerType) && (expectedType == null || expectedType.equals(listenerType)))
			listeners.add(jsAttributeName);

		//			element.setAttribute(jsAttributeName, "_ed.onEvent()");
	}

	public boolean matches(T aVisualComponent, Template child)
	{
		boolean compatible= isTemplateCompatible(child);

		if (!compatible)
		{
			for (Template c : child.getChildren())
				compatible|= isTemplateCompatible(c);
		}

		return compatible;
	}

	public Canvas<Element> render(T aVisualComponent)
	{

		if (aVisualComponent.getLayout() != null)
		{
			TemplateLayout templateLayout= (TemplateLayout) aVisualComponent.getLayout();
			if (templateLayout.getTemplate() != null)
			{
				Element element= (Element) templateLayout.getTemplate().getContent().getValue();
				boolean isRendered= element.getAttribute("rendered") != null;

				if (!isRendered)
				{
					templateLayout.addTemplateChangeListener(new TemplateChangeListener()
					{
						public void changingTemplate(VisualComponent aComponent, Template currentTemplate, Template newTemplate)
						{
							clearRendering(aComponent, currentTemplate);
						}
					});

					element.setAttribute("rendered", "true");
				}
			}
		}

		return null;
	}

	protected void clearRendering(VisualComponent aComponent, Template currentTemplate)
	{
		if (currentTemplate != null)
		{
			Element value= (Element) currentTemplate.getContent().getValue();
			if (value.getParentNode() != null)
			{
				String attribute= value.getAttribute("data-cloned-element");
				value.getParentNode().removeChild(value);
				DomHelper.makeOriginalClonedVisible(attribute);
			}
		}

	}

	public boolean isTemplateCompatible(Template child)
	{
		return false;
	}
}
