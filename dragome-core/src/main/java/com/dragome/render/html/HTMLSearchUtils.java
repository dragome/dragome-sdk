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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class HTMLSearchUtils
{
	public static List<Element> findElementsForClass(Element element, String attributeName, String value)
	{
		ArrayList<Element> result= new ArrayList<Element>();
		recElementsForClass(result, element, attributeName, value);
		return result;
	}

	private static void recElementsForClass(ArrayList<Element> res, Element element, String attributeName, String value)
	{
		String c;

		if (element == null)
		{
			return;
		}

		c= element.getAttribute(attributeName);

		if (c != null)
		{
			String[] p= c.split(" ");

			for (int x= 0; x < p.length; x++)
			{
				if (p[x].equals(value))
				{
					res.add(element);
				}
			}
		}

		NodeList childNodes= element.getChildNodes();
		for (int i= 0; i < childNodes.getLength(); i++)
		{
			Element child= (Element) childNodes.item(i);
			recElementsForClass(res, child, attributeName, value);
		}
	}
}
