/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.templates;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
			Element child= (Element) childs.item(i);
			boolean isTemplate= new RegExp(aNameRegexp).test(child.getAttribute(DATA_TEMPLATE));

			NamedNodeMap attributes= child.getAttributes();
			for (int j= 0; j < attributes.getLength(); j++)
			{
				Attr item= (Attr) attributes.item(j);
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

			if (isTemplate)
				result.add(child);

			if (!isTemplate || deepSearch)
				result.addAll(getTemplateElements(child, aNameRegexp, deepSearch));
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
