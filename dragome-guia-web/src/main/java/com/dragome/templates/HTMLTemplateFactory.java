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
package com.dragome.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.RegExp;
import com.dragome.web.html.dom.w3c.ElementExtension;

public class HTMLTemplateFactory
{
	public static final String DATA_TEMPLATE= "data-template";

	public Template createTemplate(Element fromElement, String aTemplateName)
	{
		return fasterCreateTemplate(fromElement, aTemplateName);

		//		Template template= createTemplate(aTemplateName);
		//		template.setFiringEvents(false);
		//
		//		Element mainElement= fromElement;
		//		String attributeValue= fromElement.getAttribute(DATA_TEMPLATE);
		//
		//		if (attributeValue.equals(""))
		//			mainElement= (Element) getTemplateElements(fromElement, aTemplateName, false).get(0);
		//
		//		List<Element> subTemplates= getTemplateElements(mainElement, ".+", false);
		//
		//		for (int i= 0; i < subTemplates.size(); i++)
		//		{
		//			Element childTemplateElement= subTemplates.get(i);
		//			String childTemplateName= childTemplateElement.getAttribute(DATA_TEMPLATE);
		//			template.addChild(this.createTemplate(childTemplateElement, childTemplateName));
		//		}
		//
		//		template.setContent(new ContentImpl<Element>(mainElement));
		//		template.setInner(mainElement.getAttribute(DATA_TEMPLATE).indexOf("*") != -1);
		//		//mainElement.removeAttribute(DATA_TEMPLATE);
		//		mainElement.setAttribute(DATA_TEMPLATE, "replaced: " + aTemplateName);
		//
		//		template.setFiringEvents(true);
		//
		//		return template;
	}

	private Template fasterCreateTemplate(Element fromElement, String aTemplateName)
	{
		Template result= null;

		List<Element> foundElements= new ArrayList<>();
		Map<Node, Template> templates= new HashMap<>();
		putTemplate(foundElements, templates, fromElement, aTemplateName);

		ElementExtension elementExtension= JsCast.castTo(fromElement, ElementExtension.class);
		NodeList querySelector= elementExtension.querySelectorAll("[" + DATA_TEMPLATE + "]");
		for (int i= 0; i < querySelector.getLength(); i++)
		{
			Node node= (Node) querySelector.item(i);
			Element element= (Element) node;
			String attribute= element.getAttribute(DATA_TEMPLATE);
			putTemplate(foundElements, templates, element, attribute);
		}

		for (Element element : foundElements)
		{
			Element parent= findParentInList(element, foundElements);
			Template subTemplate= templates.get(element);

			String name= subTemplate.getName();
			setReplacedName(element, name);

			if (parent != null)
			{
				Template parentTemplate= templates.get(parent);
				parentTemplate.setFiringEvents(false);
				parentTemplate.addChild(subTemplate);
				parentTemplate.setFiringEvents(true);
			}
			else
				result= subTemplate;
		}

		return result;
	}

	public static void setReplacedName(Element element, String name)
	{
		String replace= name.replace("replaced: ", "");
		element.setAttribute(DATA_TEMPLATE, "replaced: " + replace);
	}

	public void putTemplate(List<Element> foundElements, Map<Node, Template> templates, Element element, String name)
	{
		foundElements.add(element);
		Template subTemplate= createTemplate(name);
		subTemplate.updateContent(new ContentImpl<Element>(element));
		subTemplate.setInner(name.indexOf("*") != -1);
		templates.put(element, subTemplate);
	}

	private Element findParentInList(Element element, List<Element> result)
	{
		Node parent= element.getParentNode();
		while (parent != null)
		{
			for (Element element2 : result)
			{
				if (element2.isSameNode(parent))
					return element2;
			}

			parent= parent.getParentNode();
		}
		return null;
	}

	public static List getTemplateElements(Element fromNode, String aNameRegexp, boolean deepSearch)
	{
		List result= new ArrayList();

		//		ElementExtension elementExtension= JsCast.castTo(fromNode, ElementExtension.class);
		//		NodeList querySelector= elementExtension.querySelectorAll("["+DATA_TEMPLATE+"]");
		//		for (int i= 0; i < querySelector.getLength(); i++)
		//		{
		//			Node node= (Node) querySelector.item(i);
		//			result.add(node);
		//			String attribute= ((Element) node).getAttribute(DATA_TEMPLATE);
		//			System.out.println(attribute);
		//		}

		NodeList childs= fromNode.getChildNodes();

		for (int i= 0; i < childs.getLength(); i++)
		{
			Node node= (Node) childs.item(i);

			if (node instanceof Element)
			{
				Element child= (Element) node;

				String attribute= child.getAttribute(DATA_TEMPLATE);

				boolean isTemplate= attribute != null && new RegExp(aNameRegexp).test(attribute);

				NamedNodeMap attributes= child.getAttributes();
				for (int j= 0; j < attributes.getLength(); j++)
				{
					Node item2= attributes.item(j);
					if (item2 instanceof Attr)
					{
						Attr item= (Attr) item2;
						if (item.getName().startsWith("data-attribute-template-"))
						{
							String value= item.getValue().substring(item.getValue().indexOf("${template:") + 11, item.getValue().indexOf("}"));
							String name= item.getName().substring(item.getName().indexOf("data-attribute-template-") + 24);
							child.setAttribute(DATA_TEMPLATE, value);
							child.setAttribute("data-attribute-name", name);

							isTemplate= true;
							result.addAll(getTemplateElements(child, aNameRegexp, deepSearch));
						}
					}
				}

				if (isTemplate)
					result.add(child);

				if (!isTemplate || deepSearch)
					result.addAll(getTemplateElements(child, aNameRegexp, deepSearch));
			}
		}

		return result;
	}

	public static Template createTemplate()
	{
		Template template= new TemplateImpl();

		return template;
	}

	public static Template createTemplate(String name)
	{
		Template template= new TemplateImpl(name);

		return template;
	}
}
