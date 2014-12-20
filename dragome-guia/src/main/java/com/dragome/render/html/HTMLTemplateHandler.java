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

import com.dragome.render.DomHelper;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.ContentImpl;
import com.dragome.templates.HTMLTemplateFactory;
import com.dragome.templates.interfaces.Template;

public class HTMLTemplateHandler implements TemplateHandler
{
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
		Element cloneNode= (Element) node.cloneNode(true);
		cloneNode.setAttribute("data-template-cloning", "true");
		node.getParentNode().appendChild(cloneNode);

		String path= "[data-template-cloning=\"true\"]  ";
		Template clonedTemplate= cloneChildren(template, path, cloneNode);

		cloneNode.removeAttribute("data-template-cloning");
		node.getParentNode().removeChild(cloneNode);

		return clonedTemplate;
	}

	private Template cloneChildren(Template template, String aSelector, Element cloneNode)
	{
		cloneNode.removeAttribute("data-debug-id");
		Template clonedTemplate= HTMLTemplateFactory.createTemplate(template.getName());
		clonedTemplate.setFiringEvents(false);
		clonedTemplate.setContent(new ContentImpl<Element>(cloneNode));

		for (Template child : template.getChildrenMap().values())
		{
			String childName= child.getName();
			String selector= aSelector + " " + createSelector(childName);
			Element clonedElement= ServiceLocator.getInstance().getDomHandler().getElementBySelector(selector);
			Template clonedChild= cloneChildren(child, selector, clonedElement);
			clonedTemplate.addChild(clonedChild);
		}

		clonedTemplate.setFiringEvents(true);

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

}
