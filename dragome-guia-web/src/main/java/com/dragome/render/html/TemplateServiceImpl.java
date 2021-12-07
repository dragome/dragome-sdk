package com.dragome.render.html;

import org.w3c.dom.Element;

import com.dragome.templates.ContentImpl;
import com.dragome.templates.HTMLTemplateFactory;
import com.dragome.templates.interfaces.Template;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.ElementExtension;

public class TemplateServiceImpl implements TemplateService
{
	public Template cloneTemplate(Template template)
	{
		Element node= (Element) template.getContent().getValue();

		Element cloneNode= JsCast.elementRepository.cloneElement(node);
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
	

	public Template clone(Template mainPanel)
	{
		return cloneTemplate(mainPanel);
	}
}
