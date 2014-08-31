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

import org.w3c.dom.Element;

import com.dragome.debugging.execution.TemplateHandlingStrategy;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.interfaces.Template;

public class HTMLTemplateHandlingStrategy implements TemplateHandlingStrategy
{
	protected String templateContent;
	private int templateNumber= 0;

	public HTMLTemplateHandlingStrategy()
	{
	}

	public Element getContainerElement()
	{
		return (Element) (ServiceLocator.getInstance().getDomHandler().getDocument().getElementsByTagName("body").item(0));
	}

	public void loadMainTemplate(String templateName)
	{
		templateContent= HtmlTemplateHelper.getHtmlPart(templateName + ".html", null);
	}

	public Template loadTemplateCloned(String templateName, String aContainerId)
	{
		Template template= loadTemplate(templateName, aContainerId);
		return ServiceLocator.getInstance().getTemplateHandler().clone(template);
	}
	
	public Template loadTemplate(String templateName, String aContainerId)
	{
		String templateContent= HtmlTemplateHelper.getHtmlPart(templateName + ".html", aContainerId);
		return createTemplateFromHtml(templateContent);
	}

	public Template createTemplateFromHtml(String templateContent)
    {
	    Element element= ServiceLocator.getInstance().getDomHandler().getDocument().createElement("div");
		element.setAttribute("class", "dragome-hide");

		Element childElement= ServiceLocator.getInstance().getDomHandler().getDocument().createElement("div");
		element.appendChild(childElement);

		getContainerElement().getParentNode().appendChild(element);
		childElement.setAttribute("innerHTML", templateContent);

		String aTemplateName= "loaded-template-" + templateNumber++;
		childElement.setAttribute("data-template", aTemplateName);
		childElement.setAttribute("id", aTemplateName);

		Template createTemplate= ServiceLocator.getInstance().getTemplateManager().createTemplate(aTemplateName);
		//	Template createTemplate= new HTMLTemplateFactory().createTemplate(element, aTemplateName);
		return createTemplate;
    }

	public void hideContainer()
	{
		getContainerElement().setAttribute("style", "display:none;");
	}

	public void setupContainer()
	{
		getContainerElement().setAttribute("innerHTML", templateContent);
	}

	public void showContainer()
	{
		getContainerElement().setAttribute("style", "display:block;");
	}

	public Template getMainTemplate()
	{
		getContainerElement().setAttribute("data-template", "body");
		getContainerElement().setAttribute("id", "body");
		return ServiceLocator.getInstance().getTemplateManager().createTemplate("body");
	}

	public Template loadTemplate(String templateName)
	{
	    return loadTemplate(templateName, "");
	}
}
