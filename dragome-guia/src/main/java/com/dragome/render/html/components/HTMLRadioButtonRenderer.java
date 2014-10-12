/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.render.html.components;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.model.VisualRadioButton;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.listeners.ClickListener;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.GuiaServiceLocator;
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
