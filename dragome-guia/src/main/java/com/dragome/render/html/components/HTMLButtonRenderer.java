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

import org.w3c.dom.Element;

import com.dragome.model.interfaces.VisualButton;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.GuiaServiceLocator;

public class HTMLButtonRenderer extends AbstractHTMLComponentRenderer<VisualButton>
{
	public HTMLButtonRenderer()
	{
	}

	public Canvas<Element> render(final VisualButton visualButton)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(Element element)
			{
				String id= DragomeEntityManager.add(visualButton);

				//		Element button1= ServiceLocator.getInstance().getDomHandler().getDocument().createElement("input");
				element.setAttribute("type", "button");
				element.setAttribute("value", visualButton.getCaption());

				addListeners(visualButton, element);
			}
		});

		return canvas;
	}

}
