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
package com.dragome.render;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dragome.services.WebServiceLocator;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.ElementExtension;

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
		if (classes == null)
			classes= "";

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

	public static boolean hasClass(Element element, String aName)
	{
		String classes= element.getAttribute("class");
		if (classes == null)
			return false;
		else
			return classes.contains(aName);
	}

	public static void makeOriginalClonedVisible(String cloneNumber)
	{
		if (cloneNumber != null)
		{
			Document document= WebServiceLocator.getInstance().getDomHandler().getDocument();

			ElementExtension elementExtension= JsCast.castTo(document.getDocumentElement(), ElementExtension.class);

			NodeList nodeList= elementExtension.querySelectorAll("[data-cloned-element=\"" + cloneNumber + "\"]");

			if (nodeList.getLength() == 1)
			{
				Element element= JsCast.castTo(nodeList.item(0), Element.class);

				DomHelper.removeClassName(element, "dragome-hide");
			}
		}
	}
}
