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

import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.VisualLink;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.HTMLTemplateRenderer;

public class HTMLLinkRenderer extends AbstractHTMLComponentRenderer<VisualLink>
{
	public HTMLLinkRenderer()
	{
	}

	public Canvas<Element> render(final VisualLink visualLink)
	{
		Canvas<Element> canvas= ServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(final Element linkElement)
			{
				String id= DragomeEntityManager.add(visualLink);
				if (visualLink.getUrl() != null)
					linkElement.setAttribute("href", visualLink.getUrl());

				linkElement.setAttribute(COMPONENT_ID_ATTRIBUTE, id);
				setInnerText(visualLink, linkElement);

				addListeners(visualLink, linkElement);
			}

			private void setInnerText(VisualLink visualLink, Element label1)
			{
				if (visualLink.getValue() != null)
				{
					Renderer<String> renderer= visualLink.getRenderer();
					String aText= renderer.render(visualLink.getValue());
					label1.setAttribute(HTMLTemplateRenderer.INNER_HTML, aText == null ? "null" : aText);
				}
			}
		});

		return canvas;
	}

}
