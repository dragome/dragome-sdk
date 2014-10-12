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
package com.dragome.render;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DomHelper
{
	public static void removeClassName(Element element, String className)
	{
		String classes= element.getAttribute("class");
		if (classes == null)
			classes= "";
		classes= classes.replace(className, "").trim();
		element.setAttribute("class", classes);
	}

	public static void addClassName(Element element, String className)
	{
		String classes= element.getAttribute("class");
		if (!classes.contains(className))
		{
			classes+= " " + className;
			element.setAttribute("class", classes.trim());
		}
	}

	public static void removeFromParent(Element element)
	{
		Node parentNode= element.getParentNode();
		if (parentNode != null)
			parentNode.removeChild(element);
	}
}
