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
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.GuiaServiceLocator;

public class HTMLImageRenderer extends AbstractHTMLComponentRenderer<VisualImage>
{
	public HTMLImageRenderer()
	{
	}

	public Canvas<Element> render(final VisualImage visualImage)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(Element element)
			{
				if (visualImage.getValue() != null)
					element.setAttribute("src", visualImage.getValue());

				addListeners(visualImage, element);
			}
		});

		return canvas;
	}

}
