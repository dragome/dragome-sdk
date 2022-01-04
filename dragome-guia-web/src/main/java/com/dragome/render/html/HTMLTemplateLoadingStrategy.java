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

import java.util.List;

import org.cobraparser.html.domimpl.HTMLDivElementImpl;
import org.w3c.dom.Element;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.render.html.renderers.AbstractHTMLComponentRenderer;
import com.dragome.services.WebServiceLocator;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.ElementExtension;

public class HTMLTemplateLoadingStrategy implements TemplateLoadingStrategy
{
	protected String templateContent;
	private int templateNumber= 0;

	public HTMLTemplateLoadingStrategy()
	{
	}

	public Element getContainerElement()
	{
		return (Element) (WebServiceLocator.getInstance().getDomHandler().getDocument().getElementsByTagName("body").item(0));
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
		return createTemplateFromHtml(templateName + "_" + aContainerId, templateContent);
	}

	public Template createTemplateFromHtml(String name, String templateContent)
	{
		Element element= WebServiceLocator.getInstance().getDomHandler().getDocument().createElement("div");
		element.setAttribute("class", "dragome-hide");

		Element childElement= WebServiceLocator.getInstance().getDomHandler().getDocument().createElement("div");
		element.appendChild(childElement);

		getContainerElement().getParentNode().appendChild(element);
		
		ElementExtension elementExtension= JsCast.castTo(childElement, ElementExtension.class);
		elementExtension.setInnerHTML(templateContent);

		String aTemplateName= "loaded-template-" + name;
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
		AbstractHTMLComponentRenderer.setElementInnerHTML(getContainerElement(), templateContent);
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

	public List<Template> findAllTemplates()
	{
		return null;
	}
}
