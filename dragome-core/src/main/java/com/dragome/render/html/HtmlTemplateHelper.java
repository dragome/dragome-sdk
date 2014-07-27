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
package com.dragome.render.html;

import java.util.List;

import org.w3c.dom.Element;

import com.dragome.render.DomHelper;
import com.dragome.services.ServiceLocator;

public class HtmlTemplateHelper
{
	public static void removeElementsMatchingAttribute(Element root, String name, String value)
	{
		List<Element> elements= HTMLSearchUtils.findElementsForClass(root, name, value);
		for (Element element : elements)
			DomHelper.removeFromParent(element);
	}

	public static String getHtmlPart(String templateName, String id)
	{
		String templateContent= ServiceLocator.getInstance().getRequestExecutor().executeSynchronousRequest("" + templateName, null);
		return HTMLHelper.getTemplatePart(templateContent, id);
	}
}
