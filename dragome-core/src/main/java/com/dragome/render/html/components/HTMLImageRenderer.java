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

import com.dragome.model.interfaces.VisualImage;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.ServiceLocator;

public class HTMLImageRenderer extends AbstractHTMLComponentRenderer<VisualImage>
{
    public HTMLImageRenderer()
    {
    }

    public Canvas<Element> render(final VisualImage visualImage)
    {
	Canvas<Element> canvas= ServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

	canvas.setContent(new MergeableElement()
	{
	    public void mergeWith(Element element)
	    {
		String id= DragomeEntityManager.add(visualImage);
		if (visualImage.getValue() != null)
		    element.setAttribute("src", visualImage.getValue());
		element.setAttribute(COMPONENT_ID_ATTRIBUTE, id);

		addListeners(visualImage, element);
	    }
	});

	return canvas;
    }

}
