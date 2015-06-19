/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
