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

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dragome.render.html.components.Mergeable;
import com.dragome.templates.TemplateImpl;
import com.dragome.templates.TemplateListener;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;

public final class HTMLTemplateChangedListener implements TemplateListener
{
	private boolean enabled= true;

	private boolean isInvokingEvents()
	{
		return enabled;
	}

	public void insertBefore(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template)
	{
		if (isInvokingEvents())
		{
			Element referenceElement= (Element) referenceChild.getContent().getValue();

			int index= childrenList.indexOf(referenceChild) - 1;
			if (index < 0)
				index= 0;
			childrenList.add(index, newChild);

			children.put(newChild.getName(), newChild);

			Element newElement= (Element) newChild.getContent().getValue();
			referenceElement.getParentNode().insertBefore(newElement, referenceElement);
			newChild.setParent(template);
		}
	}

	public void insertAfter(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template)
	{
		if (isInvokingEvents())
		{
			Element referenceElement= (Element) referenceChild.getContent().getValue();
			childrenList.add(childrenList.indexOf(referenceChild) + 1, newChild);
			children.put(newChild.getName(), newChild);
			Node nextSibling= referenceElement.getNextSibling();
			Element newElement= (Element) newChild.getContent().getValue();
			if (nextSibling != null)
			{
				referenceElement.getParentNode().insertBefore(newElement, nextSibling);
			}
			else
				referenceElement.getParentNode().appendChild(newElement);

			newChild.setParent(template);
		}
	}

	public void contentChanged(Content<?> oldTemplateContent, Content<?> newTemplateContent)
	{
		if (isInvokingEvents())
		{
			Element element= (Element) (oldTemplateContent != null ? oldTemplateContent.getValue() : null);
			if (element != null)
			{
				if (newTemplateContent.getValue() instanceof Mergeable)
				{
					Mergeable<Element> mergeable= (Mergeable<Element>) newTemplateContent.getValue();
					mergeable.mergeWith(element);
				}
				else
				{
					Node parentNode= element.getParentNode();
					if (parentNode != null)
					{
						String className= element.getAttribute("class");
						Element newValue= (Element) newTemplateContent.getValue();
						newValue.setAttribute("class", className);
						parentNode.replaceChild(newValue, element);
					}
//					else
//						System.out.println("sdgasdg");
				}
			}
		}
	}

	public void childRemoved(Template referenceChild)
	{
		if (isInvokingEvents())
		{
			Element referenceElement= (Element) referenceChild.getContent().getValue();
			if (referenceElement.getParentNode() != null)
				referenceElement.getParentNode().removeChild(referenceElement);
		}
	}

	public void childAdded(Template parent, Template child)
	{
		if (isInvokingEvents())
		{
			Element parentElement= (Element) parent.getContent().getValue();
			Element childElement= (Element) child.getContent().getValue();
			parentElement.appendChild(childElement);
		}
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled= enabled;
	}

	public void childReplaced(TemplateImpl parent, Template previousChild, Template newChild)
	{
		if (isInvokingEvents())
		{
			Element previousElement= (Element) previousChild.getContent().getValue();
			Element newElement= (Element) newChild.getContent().getValue();
			previousElement.getParentNode().replaceChild(newElement, previousElement);
		}
	}
}
