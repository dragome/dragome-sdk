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
import java.util.Map.Entry;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.render.canvas.interfaces.CanvasHelper;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.HTMLTemplateFactory;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;

public class HTMLCanvasHelper implements CanvasHelper
{
	public void hideTemplateIds(Template template)
	{
		String key= null;
		String result= "";

		Document document= ServiceLocator.getInstance().getDomHandler().getDocument();
		for (Entry<String, Template> entry : template.getChildrenMap().entrySet())
		{
			if (key != null)
				result+= ",";

			key= entry.getKey();
			Template value= entry.getValue();

			Attr id= ((Content<Element>) value.getContent()).getValue().getAttributeNode("id");
			String value2= id.getValue();
			document.getElementById(value2).setAttribute("id", System.identityHashCode(template) + "_" + value2);
		}
	}

	public void restoreTemplateIds(Template template)
	{
	}

	public void removeReplacedElements(Element element)
	{
		List<Element> elementByClassMatching= HTMLTemplateFactory.getTemplateElements(element, ".+", true);
		for (Element element2 : elementByClassMatching)
		{
			String attribute= element2.getAttribute("data-rendered");
			if ("".equals(attribute))
				element2.getParentNode().removeChild(element2);
		}
	}

}
