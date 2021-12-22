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

import com.dragome.commons.javascript.JsHelper;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualCheckbox;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.Layout;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.WebServiceLocator;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;

public class HTMLCheckboxRenderer extends AbstractHTMLComponentRenderer<VisualCheckbox>
{
	public Canvas<Element> render(final VisualCheckbox checkbox)
	{
		final String id= DragomeEntityManager.add(checkbox);
		final Element radioButtonElement;

		Layout layout= checkbox.getLayout();

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
			setupElement(checkbox, inputElementContent.getValue());
			Element value= textElementContent.getValue();
			Node parentNode= value.getParentNode();
			Text optionElement= WebServiceLocator.getInstance().getDomHandler().getDocument().createTextNode(checkbox.getCaption());
			parentNode.replaceChild( optionElement, value);
		}
		else
		{

			Document document= WebServiceLocator.getInstance().getDomHandler().getDocument();
			radioButtonElement= document.createElement("input");
			setupElement(checkbox, radioButtonElement);
		}
		
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();
		canvas.setContent(radioButtonElement);
		return canvas;
	}
	

	public void setupElement(final VisualCheckbox checkbox, final Element button1)
	{
		button1.setAttribute("type", "checkbox");
		button1.setAttribute("value", checkbox.getCaption());
		updateChecked(checkbox, button1);

		checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
		{
			public void onValueChange(ValueChangeEvent<Boolean> event)
			{
				updateChecked(checkbox, button1);
			}
		});
		
		checkbox.addListener(ClickListener.class, new ClickListener()
		{
			public void clickPerformed(VisualComponent aVisualComponent)
			{
				ScriptHelper.put("e", JsHelper.getFinalElement(button1), this);
				boolean value= ScriptHelper.evalBoolean("e.node.checked", this);
				checkbox.setValue(value);
			}
		});
		
		addListeners(checkbox, button1);
	}

	private void updateChecked(final VisualCheckbox checkbox, Element button1)
	{
		boolean checked= checkbox.getValue() != null && checkbox.getValue();
		String isChecked= checked ? "true" : "false";

		ScriptHelper.put("checked", isChecked, this);
		ScriptHelper.put("button1", JsHelper.getFinalElement(button1), this);
		ScriptHelper.evalNoResult("button1.node.checked= (checked == 'true')", this);
		//				
		//				if (checked)
		//					button1.setAttribute("checked", "checked");
		//				else
		//					button1.removeAttribute("checked");
	}
}
