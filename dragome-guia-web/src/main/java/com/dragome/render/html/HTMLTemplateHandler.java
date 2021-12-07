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
import com.dragome.services.WebServiceLocator;
import com.dragome.services.interfaces.ServiceFactory;
import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;

public class HTMLTemplateHandler implements TemplateHandler
{
	static ServiceFactory serverSideServiceFactory= WebServiceLocator.getInstance().getServerSideServiceFactory();
	public static TemplateService templateService= serverSideServiceFactory.createSyncService(TemplateService.class);

	
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
		Template parent= mainPanel.getParent();
		mainPanel.setParent(null);
		Template cloneTemplate= templateService.cloneTemplate(mainPanel);
		mainPanel.setParent(parent);
		return cloneTemplate;
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
