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
package com.dragome.render.canvas;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.ConcatCanvas;
import com.dragome.templates.HTMLTemplateFactory;

public class HTMLCanvas implements Canvas<Element>
{
	protected Element content;

	public HTMLCanvas(Element content)
	{
		this.content= content;
	}

	public HTMLCanvas()
	{
	}

	public Element getContent()
	{
		return content;
	}

	public void setContent(Element object)
	{
		this.content= object;
	}

	public void replaceSection(String anAlias, Canvas<?> aCanvas)
	{
		List<Element> foundElement= getElementsByClassName("replaced: " + anAlias, getContent());
		Element elementToReplace= foundElement.get(0);
		Element parentNode= (Element) elementToReplace.getParentNode();

		replaceElementByCanvas(aCanvas, elementToReplace, parentNode, false);
	}

	public static void replaceElementByCanvas(Canvas<?> aCanvas, Element elementToReplace, Element parentNode, boolean removeReplacements)
	{
		Node grandParentNode= parentNode.getParentNode();

		Node cloneNode= parentNode.cloneNode(false);

		if (grandParentNode != null)
			grandParentNode.replaceChild(cloneNode, parentNode);

		if (aCanvas instanceof ConcatCanvas)
		{
			ConcatCanvas<Element> concatCanvas= (ConcatCanvas<Element>) aCanvas;

			List<Element> content2= concatCanvas.getContent();
			if (content2.size() > 0)
			{
				Object element= content2.get(content2.size() - 1);
				if (element instanceof Element)
				{
					Element replacement= (Element) ((Element) element).cloneNode(true);
					replacement.removeAttribute(HTMLTemplateFactory.DATA_TEMPLATE);
					parentNode.replaceChild(replacement, elementToReplace);

					elementToReplace= replacement;
					for (int i= content2.size() - 2; i >= 0; i--)
					{
						replacement= (Element) content2.get(i).cloneNode(true);
						replacement.removeAttribute(HTMLTemplateFactory.DATA_TEMPLATE);
						parentNode.insertBefore(replacement, elementToReplace);
						elementToReplace= replacement;
					}
				}
			}
		}
		else
		{
			Element replacement= (Element) ((Node) aCanvas.getContent()).cloneNode(true);
			replacement.removeAttribute(HTMLTemplateFactory.DATA_TEMPLATE);
			parentNode.replaceChild(replacement, elementToReplace);
		}

		if (removeReplacements)
			GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().getCanvasHelper().removeReplacedElements(parentNode);

		if (grandParentNode != null)
			grandParentNode.replaceChild(parentNode, cloneNode);
	}

	public List<Element> getElementsByClassName(String classname, Element node)
	{
		List<Element> result= new ArrayList<Element>();

		NodeList nodeList= node.getElementsByTagName("*");

		//	DomHandler domHandler= ServiceLocator.getInstance().getRemoteObjectsService().createRemoteService(DomHandler.class);
		//	NodeList nodeList= domHandler.getElementsByTagNameOf("*", node);

		for (int i= 0; i < nodeList.getLength(); i++)
		{
			Element item= (Element) nodeList.item(i);
			String classAttribute= item.getAttribute(HTMLTemplateFactory.DATA_TEMPLATE);
			if (classAttribute != null && classAttribute.contains(classname))
				result.add(item);
		}

		return result;
	}

}
