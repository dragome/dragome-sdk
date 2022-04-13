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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLInputElement;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.VisualRadioButton;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.Layout;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.WebServiceLocator;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;

public class HTMLRadioButtonRenderer extends AbstractHTMLComponentRenderer<VisualRadioButton>
{
	public Canvas<Element> render(final VisualRadioButton radioButton)
	{
		final String id= DragomeEntityManager.add(radioButton);
		final Element radioButtonElement;

		Layout layout= radioButton.getLayout();

		if (layout instanceof TemplateLayout)
		{
			TemplateLayout templateLayout= (TemplateLayout) layout;

			Template template= templateLayout.getTemplate();
			Content<Element> content= (Content<Element>) template.getContent();
			radioButtonElement= content.getValue();

			Template inputTemplate= template.getChild("input");
			Template textTemplate= template.getChild("text");

			Content<Element> inputElementContent= (Content<Element>) inputTemplate.getContent();
			Content<Element> textElementContent= (Content<Element>) textTemplate.getContent();
			setupElement(radioButton, inputElementContent.getValue());
			Element value= textElementContent.getValue();
			Node parentNode= value.getParentNode();
			Text optionElement= WebServiceLocator.getInstance().getDomHandler().getDocument().createTextNode(radioButton.getCaption());
			parentNode.replaceChild(optionElement, value);
		}
		else
		{

			Document document= WebServiceLocator.getInstance().getDomHandler().getDocument();
			radioButtonElement= document.createElement("input");

			setupElement(radioButton, radioButtonElement);

		}
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();
		canvas.setContent(radioButtonElement);
		return canvas;
	}

	public void setupElement(final VisualRadioButton radioButton, final Element radioButtonElement)
	{
		radioButton.addListener(ClickListener.class, new ClickListener()
		{
			public void clickPerformed(VisualComponent aVisualComponent)
			{
				HTMLInputElement htmlInputElement= JsCast.castTo(radioButtonElement, HTMLInputElement.class);
				boolean value= htmlInputElement.getChecked();
				radioButton.setValue(value);
			}
		});

		radioButtonElement.setAttribute("type", "radio");
		radioButtonElement.setAttribute("value", radioButton.getCaption());
		radioButtonElement.setAttribute("name", radioButton.getButtonGroup());
		if (radioButton.getValue())
			radioButtonElement.setAttribute("checked", "checked");

		addListeners(radioButton, radioButtonElement);
	}
	
	public boolean isTemplateCompatible(Template child)
	{
		Element element= (Element) child.getContent().getValue();
		String tagName= element.getTagName();
		return tagName.equalsIgnoreCase("input") && "radio".equals(element.getAttribute("type"));
	}
}
