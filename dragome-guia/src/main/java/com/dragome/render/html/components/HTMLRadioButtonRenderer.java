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
package com.dragome.render.html.components;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.VisualRadioButton;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.listeners.ClickListener;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.ServiceLocator;

public class HTMLRadioButtonRenderer extends AbstractHTMLComponentRenderer<VisualRadioButton>
{
	public Canvas<Element> render(final VisualRadioButton radioButton)
	{
		final String id= DragomeEntityManager.add(radioButton);

		Document document= ServiceLocator.getInstance().getDomHandler().getDocument();
		final Element radioButtonElement= document.createElement("input");

		radioButton.addListener(ClickListener.class, new ClickListener()
		{
			public void clickPerformed(VisualComponent aVisualComponent)
			{
				ScriptHelper.put("e", radioButtonElement, this);
				boolean value= ScriptHelper.evalBoolean("e.node.checked", this);
				radioButton.setValue(value);
			}
		});

		radioButtonElement.setAttribute("type", "radio");
		radioButtonElement.setAttribute("value", radioButton.getCaption());
		radioButtonElement.setAttribute("name", radioButton.getButtonGroup());
		if (radioButton.getValue())
			radioButtonElement.setAttribute("checked", "checked");

		addListeners(radioButton, radioButtonElement);

		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();
		canvas.setContent(radioButtonElement);

		return canvas;
	}
}
