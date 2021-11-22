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
import org.w3c.dom.Node;

import com.dragome.render.DomHelper;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.ContentImpl;
import com.dragome.templates.HTMLTemplateFactory;
import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.ElementExtension;

public class HTMLTemplateHandler implements TemplateHandler
{
	private static int i;

	public void makeVisible(Template clonedChild)
	{
		Element element= (Element) clonedChild.getContent().getValue();
		DomHelper.removeClassName(element, "dragome-hide");
		//		element.removeAttribute("style");
	}

	public void makeInvisible(Template clonedChild)
	{
		Element element= (Element) clonedChild.getContent().getValue();
		//		element.setAttribute("style", "display:none;");
		DomHelper.addClassName(element, "dragome-hide");
	}

	public Template clone(Template mainPanel)
	{
		return cloneTemplate(mainPanel);
	}

	private Template cloneTemplate(Template template)
	{
		Element node= (Element) template.getContent().getValue();

		String clonedNumber= node.getAttribute("data-cloned-element");

		if (clonedNumber == null)
			node.setAttribute("data-cloned-element", "" + i++);

		Element cloneNode= (Element) node.cloneNode(true);
		//		node.getParentNode().appendChild(cloneNode);

		Template clonedTemplate= cloneChildren(template, cloneNode);

		//		node.getParentNode().removeChild(cloneNode);

		return clonedTemplate;
	}

	private Template cloneChildren(Template template, Element cloneNode)
	{
		Template clonedTemplate= null;
		if (cloneNode != null)
		{
			cloneNode.removeAttribute("data-debug-id");
			clonedTemplate= HTMLTemplateFactory.createTemplate(template.getName());
			clonedTemplate.setFiringEvents(false);
			clonedTemplate.updateContent(new ContentImpl<Element>(cloneNode));

			for (Template child : template.getChildrenMap().values())
			{
				String childName= child.getName();

				ElementExtension elementExtension= JsCast.castTo(cloneNode, ElementExtension.class);

				Element clonedElement= elementExtension.querySelector(createSelector(childName));
				Template clonedChild= cloneChildren(child, clonedElement);
				if (clonedChild != null)
					clonedTemplate.addChild(clonedChild);
			}

			clonedTemplate.setFiringEvents(true);
		}

		return clonedTemplate;
	}

	private String createSelector(String childName)
	{
		return "[data-template=\"replaced: " + childName + "\"]";
	}

	public void markWith(Template child, String name)
	{
		((Element) child.getContent().getValue()).setAttribute("data-result", name);
	}
	public void releaseTemplate(Template clonedChild)
	{
		((Element) clonedChild.getContent().getValue()).removeAttribute("data-template");
	}

	public List<Template> cloneTemplates(List<Template> templates)
	{
		List<Template> clonedTemplates= new ArrayList<Template>();
		for (Template childTemplate : templates)
			clonedTemplates.add(clone(childTemplate));

		return clonedTemplates;
	}

	public boolean isConnected(Template aTemplate)
	{
		boolean bodyNodeFound= false;
		Node parentNode= (Node) aTemplate.getContent().getValue();

		while (parentNode != null && !bodyNodeFound)
		{
			parentNode= parentNode.getParentNode();
			if (parentNode != null && parentNode.getAttributes() != null)
			{
				Element e1= JsCast.castTo(parentNode, Element.class);
				bodyNodeFound= "body".equals(e1.getAttribute("id"));
			}
		}

		return bodyNodeFound;
	}

}
