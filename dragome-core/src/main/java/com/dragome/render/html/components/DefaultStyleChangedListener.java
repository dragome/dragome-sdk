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

import com.dragome.model.interfaces.Style;
import com.dragome.model.listeners.StyleChangedListener;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.services.ServiceLocator;

public final class DefaultStyleChangedListener implements StyleChangedListener
{
	public DefaultStyleChangedListener()
	{
	}

	public void styleChanged(Style style)
	{
		String entityId= DragomeEntityManager.getEntityId(style.getVisualComponent());
		Element element= ServiceLocator.getInstance().getDomHandler().getElementBySelector("[" + AbstractHTMLComponentRenderer.COMPONENT_ID_ATTRIBUTE + "=\"" + entityId + "\"]");
		if (element != null)
		{
			if (style.getName() == null)
				style.setName(element.getAttribute("class"));
			else
			{
				if (style.getName().trim().length() > 0)
					element.setAttribute("class", style.getName());
				else
					element.removeAttribute("class");

				if (style.isEnabled())
					element.removeAttribute("disabled");
				else
					element.setAttribute("disabled", "disabled");
				//			if (style.isVisible())
				//				element.removeAttribute("style");
				//			else
				//				element.setAttribute("style", "display:none;");
			}
		}
	}

	public void boundsChanged(Style style)
	{
		String entityId= DragomeEntityManager.getEntityId(style.getVisualComponent());
		Element element= ServiceLocator.getInstance().getDomHandler().getElementBySelector("[" + AbstractHTMLComponentRenderer.COMPONENT_ID_ATTRIBUTE + "=\"" + entityId + "\"]");
		if (element != null)
		{
		    String styleString= "position: relative; left: ${left}px;top: ${top}px;";
		    styleString= styleString.replace("${left}", style.getBounds().getX()+"");
		    styleString= styleString.replace("${top}", style.getBounds().getY()+"");
		    element.setAttribute("style", styleString);
		}
	}
}
