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
package com.dragome.templates;

import org.w3c.dom.Element;

import com.dragome.debugging.execution.TemplateHandlingStrategy;
import com.dragome.render.canvas.HTMLCanvasFactory;
import com.dragome.render.canvas.interfaces.CanvasFactory;
import com.dragome.render.html.HTMLTemplateHandler;
import com.dragome.render.html.HTMLTemplateHandlingStrategy;
import com.dragome.render.html.components.HTMLComponentRenderer;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateManager;

public class HTMLTemplateManager implements TemplateManager
{
	public TemplateHandler getTemplateHandler()
	{
		return new HTMLTemplateHandler();
	}

	public TemplateHandlingStrategy getTemplateHandlingStrategy()
	{
		return new HTMLTemplateHandlingStrategy();
	}

	public CanvasFactory<Element> getCanvasFactory()
	{
		return new HTMLCanvasFactory();
	}

	public Template createTemplate(String aTemplateName)
	{
		Element element= ServiceLocator.getInstance().getDomHandler().getDocument().getElementById(aTemplateName);
		return new HTMLTemplateFactory().createTemplate(element, aTemplateName);
	}

	public Template createTemplateFromElement(Element containerElement, String aName)
	{//TODO arreglar esto!!!!
		return new HTMLTemplateFactory().createTemplate(containerElement, aName);
	}

	public ComponentRenderer getComponentRenderer()
	{
		return new HTMLComponentRenderer();
	}
}
