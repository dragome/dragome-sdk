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

import com.dragome.model.interfaces.VisualPanel;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.HTMLCanvas;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;

public class HTMLPanelRenderer extends AbstractHTMLComponentRenderer<VisualPanel>
{
	public HTMLPanelRenderer()
	{
	}

	public Canvas<Element> render(VisualPanel visualPanel)
	{
		Template template= ((TemplateLayout) visualPanel.getLayout()).getTemplate();

		if (template != null)
		{
			Element element= ((Content<Element>) template.getContent()).getValue(); 

			addListeners(visualPanel, element);
//			if (visualPanel.hasListener(ClickListener.class))
//			{
//				element.setAttribute("onclick", "_ed.onEvent(event)");
//				element.setAttribute("onmousedown", "stopEvent(event);");
//			}
			
			final String id= DragomeEntityManager.add(visualPanel);
			element.setAttribute(COMPONENT_ID_ATTRIBUTE, id);
			
			return new HTMLCanvas(element);
		}

		return null;
	}
}
