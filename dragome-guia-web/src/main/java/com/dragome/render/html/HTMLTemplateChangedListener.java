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
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.dragome.render.html.renderers.AbstractHTMLComponentRenderer;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.templates.HTMLTemplateFactory;
import com.dragome.templates.TemplateImpl;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateListener;

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

//			int index= childrenList.indexOf(referenceChild) - 1;
//			if (index < 0)
//				index= 0;
//			childrenList.add(index, newChild);
//
//			children.put(newChild.getName(), newChild);

			Element newElement= (Element) newChild.getContent().getValue();
			referenceElement.getParentNode().insertBefore(newElement, referenceElement);
//			newChild.setParent(template);
		}
	}

	public void insertAfter(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template)
	{
		if (isInvokingEvents())
		{
			Element referenceElement= (Element) referenceChild.getContent().getValue();
//			childrenList.add(childrenList.indexOf(referenceChild) + 1, newChild);
//			children.put(newChild.getName(), newChild);
			Node nextSibling= findNextSiblingElement(referenceElement);
			Element newElement= (Element) newChild.getContent().getValue();
			Node parentNode= referenceElement.getParentNode();
			if (nextSibling != null)
			{
				parentNode.replaceChild(newElement, referenceElement);
				parentNode.insertBefore(referenceElement, newElement);

			}
			else
				parentNode.appendChild(newElement);

//			newChild.setParent(template);
		}
	}

	public Node findNextSiblingElement(Node referenceElement)
	{
		Node nextSibling= referenceElement.getNextSibling();

		if (nextSibling == null || nextSibling.getNodeType() == Node.ELEMENT_NODE)
			return nextSibling;
		else
			return findNextSiblingElement(nextSibling);
	}

	public void contentChanged(Template template, Content<?> oldTemplateContent, Content<?> newTemplateContent)
	{
		if (isInvokingEvents())
		{
			Element element= (Element) (oldTemplateContent != null ? oldTemplateContent.getValue() : null);
			if (element != null)
			{
				if (newTemplateContent.getValue() instanceof Mergeable)
				{
					Mergeable<Element> mergeable= (Mergeable<Element>) newTemplateContent.getValue();
					mergeable.mergeWith(template, element);
				}
				else
				{
					Node parentNode= element.getParentNode();
					if (parentNode != null)
					{
						String className= element.getAttribute("class");
						Element newValue= (Element) newTemplateContent.getValue();
						newValue.setAttribute("class", className);
						if (newValue != element)
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

	public void childReplaced(Template parent, Template previousChild, Template newChild)
	{
		if (isInvokingEvents())
		{
			Element previousElement= (Element) previousChild.getContent().getValue();
			Element newElement= (Element) newChild.getContent().getValue();
			if (previousElement != newElement)
				previousElement.getParentNode().replaceChild(newElement, previousElement);
		}
	}

	public void nameChanged(Template template, String name)
	{
		Content<?> content= template.getContent();
		if (content != null)
		{
			Element element= (Element) content.getValue();
			HTMLTemplateFactory.setReplacedName(element, name);
		}
	}

	public boolean isActive(Template template)
	{
		return ((Element) template.getContent().getValue()).hasAttribute(AbstractHTMLComponentRenderer.COMPONENT_ID_ATTRIBUTE);
	}
}
