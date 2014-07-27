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
package com.dragome.render.canvas;

import java.util.List;

import org.w3c.dom.Element;

import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.CanvasRenderer;
import com.dragome.services.ServiceLocator;

public class HTMLCanvasRenderer implements CanvasRenderer<Element>
{
	public void render(Canvas<Element> canvas, String aPlaceAlias)
	{
		ServiceLocator.getInstance().getTimeCollector().registerStart("show canvas");
		Element element= ServiceLocator.getInstance().getDomHandler().getDocument().getElementById(aPlaceAlias);
		Object content= canvas.getContent();
		if (content instanceof Element)
		{
			Element element2= (Element) content;
			//ServiceLocator.getInstance().getCanvasFactory().getCanvasHelper().removeReplacedElements(element2);

			element.appendChild(element2);
		}
		else
		{
			List<Element> contents= (List<Element>) content;
			for (Element element2 : contents)
			{
				//ServiceLocator.getInstance().getCanvasFactory().getCanvasHelper().removeReplacedElements(element2);
				element.appendChild(element2);
			}
		}

		ServiceLocator.getInstance().getTimeCollector().registerEnd();
	}
}
