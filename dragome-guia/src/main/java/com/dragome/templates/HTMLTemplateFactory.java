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
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dragome.html.dom.RegExp;
import com.dragome.templates.interfaces.Template;

public class HTMLTemplateFactory
{
	public static final String DATA_TEMPLATE= "data-template";

	public Template createTemplate(Element fromElement, String aTemplateName)
	{
		Template template= createTemplate(aTemplateName);
		template.setFiringEvents(false);

		Element mainElement= fromElement;
		String attributeValue= fromElement.getAttribute(DATA_TEMPLATE);

		if (attributeValue.equals(""))
			mainElement= (Element) getTemplateElements(fromElement, aTemplateName, false).get(0);

		List<Element> subTemplates= getTemplateElements(mainElement, ".+", false);

		for (int i= 0; i < subTemplates.size(); i++)
		{
			Element childTemplateElement= subTemplates.get(i);
			String childTemplateName= childTemplateElement.getAttribute(DATA_TEMPLATE);
			template.addChild(this.createTemplate(childTemplateElement, childTemplateName));
		}

		template.setContent(new ContentImpl<Element>(mainElement));
		template.setInner(mainElement.getAttribute(DATA_TEMPLATE).indexOf("*") != -1);
		//mainElement.removeAttribute(DATA_TEMPLATE);
		mainElement.setAttribute(DATA_TEMPLATE, "replaced: " + aTemplateName);

		template.setFiringEvents(true);

		return template;
	}

	public static List getTemplateElements(Element fromNode, String aNameRegexp, boolean deepSearch)
	{
		List result= new ArrayList();
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
