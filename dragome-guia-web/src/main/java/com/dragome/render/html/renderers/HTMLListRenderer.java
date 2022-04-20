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
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualListBox;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.WebServiceLocator;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.ElementExtension;

public class HTMLListRenderer extends AbstractHTMLComponentRenderer<VisualListBox<Object>>
{
	public HTMLListRenderer()
	{
	}

	public Canvas<Element> render(final VisualListBox<Object> visualList)
	{
		super.render(visualList);

		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement(this)
		{
			public void mergeWith(final Element selectElement)
			{

				setElementInnerHTML(selectElement, "");

				visualList.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						renderInnerContent(visualList, selectElement);
					}
				});

				visualList.addListener(ClickListener.class, new ClickListener()
				{
					public void clickPerformed(VisualComponent aVisualComponent)
					{
						updateSelected(visualList, selectElement);
					}

				});

				if (!selectElement.hasAttribute("size"))
					selectElement.setAttribute("size", getSelectElementSize() + "");

				if (visualList.isMultipleItems())
					selectElement.setAttribute("multiple", "multiple");
				else
					selectElement.removeAttribute("multiple");

				renderInnerContent(visualList, selectElement);

				ElementExtension eventTarget= JsCast.castTo(selectElement, ElementExtension.class);
				eventTarget.removeEventListeners("change");
				eventTarget.addEventListener("change", new EventListener()
				{
					public void handleEvent(Event evt)
					{
						updateSelected(visualList, selectElement);
					}
				}, false);

				addListeners(visualList, selectElement);
			}

			public void renderInnerContent(final VisualListBox<Object> visualList, final Element selectElement)
			{
				ElementExtension selectExtension= JsCast.castTo(selectElement, ElementExtension.class);

				for (Object element : visualList.getAcceptableValues())
				{
					Renderer<Object> renderer= visualList.getRenderer();
					String rendered= renderer.render(element);
					Object value= visualList.getValue();

					boolean isSelected= visualList.isMultipleItems() && visualList.getSelectedValues().contains(element);
					isSelected|= !visualList.isMultipleItems() && element == value;

					String elementValue= createElementValue(element);
					Element querySelector= selectExtension.querySelector("option[value='" + elementValue + "']");
					if (querySelector != null)
					{
						if (isSelected)
							querySelector.setAttribute("selected", "selected");
						else
							querySelector.removeAttribute("selected");

					}
					else
					{

						Element optionElement= WebServiceLocator.getInstance().getDomHandler().getDocument().createElement("option");
						if (isSelected)
							optionElement.setAttribute("selected", "selected");

						optionElement.setAttribute("value", elementValue);
						setElementInnerHTML(optionElement, rendered);

						selectExtension.appendChild(optionElement);
					}
				}

				NodeList options= selectExtension.querySelectorAll("option");
				for (int i= 0; i < options.getLength(); i++)
				{
					Node item= options.item(i);
					Element element2= (Element) item;
					String value2= element2.getAttribute("value");
					boolean found= false;
					for (Object element : visualList.getAcceptableValues())
					{
						Renderer<Object> renderer= visualList.getRenderer();
						String rendered= renderer.render(element);

						String elementValue= createElementValue(element);

						if (value2.equals(elementValue))
							found= true;
					}

					if (!found)
						selectElement.removeChild(element2);
				}
			}

		});

		return canvas;

	}
	private String createElementValue(Object element)
	{
		return System.identityHashCode(element) + "";
	}
	protected int getSelectElementSize()
	{
		return 5;
	}
	private void updateSelected(final VisualListBox<Object> visualList, final Element selectElement)
	{
		ElementExtension selectExtension= JsCast.castTo(selectElement, ElementExtension.class);

		NodeList options= selectExtension.querySelectorAll("option");

		ArrayList<Object> selectedItems= new ArrayList<>();
		for (int i= 0; i < options.getLength(); i++)
		{
			Node item= options.item(i);
			Element element= (Element) item;

			HTMLOptionElement htmlOptionElement= JsCast.castTo(element, HTMLOptionElement.class);
			boolean isSelected= htmlOptionElement.getSelected();
			String value= element.getAttribute("value");
			Object selectedObject= findSelectedItem(visualList, value);

			if (isSelected)
				if (visualList.isMultipleItems())
					selectedItems.add(selectedObject);
				else
				{
					visualList.setValue(selectedObject);
					return;
				}
		}

		visualList.setSelectedValues(selectedItems);
	}

	public Object findSelectedItem(final VisualListBox<Object> visualList, String value)
	{
		Renderer<Object> renderer= visualList.getRenderer();

		Collection<Object> acceptableValues= visualList.getAcceptableValues();
		for (Object object : acceptableValues)
		{
			String render= renderer.render(object);
			String elementValue= createElementValue(object);

			if (elementValue.equals(value))
				return object;
		}
		return null;
	}

	public boolean matches(Template child)
	{
		Element element= ((Content<Element>) child.getContent()).getValue();
		String nodeName= element.getNodeName();
		return "select".equalsIgnoreCase(nodeName);
	}
}
