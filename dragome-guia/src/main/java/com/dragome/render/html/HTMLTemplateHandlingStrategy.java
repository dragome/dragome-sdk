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

import org.w3c.dom.Element;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateHandlingStrategy;

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
		return GuiaServiceLocator.getInstance().getTemplateHandler().clone(template);
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

		Template createTemplate= GuiaServiceLocator.getInstance().getTemplateManager().createTemplate(aTemplateName);
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
		return GuiaServiceLocator.getInstance().getTemplateManager().createTemplate("body");
	}

	public Template loadTemplate(String templateName)
	{
	    return loadTemplate(templateName, "");
	}
}
