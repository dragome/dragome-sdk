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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.DefaultEventProducer;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateListener;
import com.dragome.templates.interfaces.TemplateVisitor;

public class TemplateImpl extends DefaultEventProducer implements Template
{

	public static Template findTemplate(Template templateElement, final String name)
	{
		final Template[] foundTemplate= new Template[1];

		templateElement.accept(new TemplateVisitor()
		{
			public void visitTemplate(Template aTemplate)
			{
				if (aTemplate.getName().equals(name))
					foundTemplate[0]= aTemplate;
			}
		});

		return foundTemplate[0];
	}

	public static Template getTemplateElementInDepth(Template templateElement, String aDeepAlias)
	{
		String[] aliases= aDeepAlias.split("\\.");
		Template currentTemplateElement= templateElement;

		for (int i= 0; i < aliases.length; i++)
		{
			if (currentTemplateElement instanceof Template)
				currentTemplateElement= ((Template) currentTemplateElement).getChild(aliases[i]);
			else
				break;
		}

		return currentTemplateElement;
	}

	private String name;
	protected Map<String, Template> childrenMap= new HashMap<String, Template>();
	private Content<?> templateContent;
	protected Boolean inner= Boolean.FALSE;
	private Template parent;
	private List<Template> children= new ArrayList<Template>();
	protected TemplateListener templateListener= GuiaServiceLocator.getInstance().getTemplateListener();

	public TemplateImpl()
	{
	}

	public TemplateImpl(String aName)
	{
		this();
		setName(aName);
	}

	public String getName()
	{
		return name;
	}

	public Content<?> getContent()
	{
		return templateContent;
	}

	public Template getChild(String anAlias)
	{
		Template templateElement= childrenMap.get(anAlias);
		if (templateElement == null)
			throw new RuntimeException("Cannot find template element '" + anAlias + "' in '" + getName() + "'");

		return templateElement;
	}

	public Map<String, Template> getChildrenMap()
	{
		return childrenMap;
	}
	public void setChildrenMap(Map<String, Template> templateElements)
	{
		this.childrenMap= templateElements;
		//		for (Entry<String, Template> entries : templateElements.entrySet())
		//		{
		//			getChildren().add(entries.getValue());
		//		}
	}

	public void updateName(String name)
	{
		this.setName(name);

		templateListener.nameChanged(this, name);
	}

	public void updateContent(Content<?> templateContent)
	{
		templateListener.contentChanged(this, this.templateContent, templateContent);

		if (!(templateContent.getValue() instanceof Mergeable))
			this.templateContent= templateContent;
	}

	public Template setChild(String anAlias, Template templateElement)
	{
		return childrenMap.put(anAlias, templateElement);
	}

	public void setInner(Boolean inner)
	{
		this.inner= inner;
	}

	public Boolean isInner()
	{
		return inner;
	}

	public Content<?> getElementById(String id)
	{
		return childrenMap.get(id).getContent();
	}

	public Template getParent()
	{
		return parent;
	}

	public void setParent(Template parent)
	{
		this.parent= parent;
	}

	public void insertAfter(Template newChild, Template referenceChild)
	{
		Template previous= addToChildren(newChild);

		templateListener.insertAfter(newChild, referenceChild, childrenMap, getChildren(), this);
	}

	public void remove(Template child)
	{
		getChildren().remove(child);
		childrenMap.remove(child.getName());
		child.setParent(null);
		templateListener.childRemoved(child);
	}

	public void insertBefore(Template newChild, Template referenceChild)
	{
		Template previous= addToChildren(newChild);

		templateListener.insertBefore(newChild, referenceChild, childrenMap, getChildren(), this);
	}

	public void addChild(Template template)
	{
		Template previous= addToChildren(template);

		if (previous != null)
			templateListener.childReplaced(this, previous, template);
		else
			templateListener.childAdded(this, template);
	}

	public Template addToChildren(Template template)
	{
		Template previous= childrenMap.put(template.getName(), template);
		if (previous != null)
		{
			previous.setParent(null);
			getChildren().set(getChildren().indexOf(previous), template);
		}
		else
		{
			getChildren().add(template);
		}

		template.setParent(this);
		return previous;
	}

	public String toString()
	{
		StringBuilder result= new StringBuilder();
		for (Template template : children)
		{
			String childString= template.toString();
			result.append(childString + "//");
		}
		return getName() + ": (" + result.toString() + ")";
	}

	public int hashCode()
	{
		return getName().hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof Template)
		{
			Template other= (Template) obj;
			boolean childrenEqual= children.size() == other.getChildren().size();
			return name.equals(other.getName()) && childrenEqual;
		}
		else
			return false;
	}

	public void removeChild(String name)
	{
		if (hasChild(name))
			remove(getChild(name));
	}

	public void setFiringEvents(boolean firingEvents)
	{
		this.templateListener.setEnabled(firingEvents);
	}

	public void renameChild(Template child, String aName)
	{
		String childName= child.getName();
		Template previous= childrenMap.remove(childName);
		if (previous != child)
			throw new RuntimeException("This is not a child of this template!");

		childrenMap.put(aName, child);
	}

	public boolean hasChild(String aName)
	{
		Template templateElement= childrenMap.get(aName);
		return templateElement != null;
	}

	public void removeAll()
	{
		ArrayList<Template> childrenCopy= new ArrayList<Template>(getChildren());
		for (Template child : childrenCopy)
			remove(child);
	}

	public void accept(TemplateVisitor templateVisitor)
	{
		templateVisitor.visitTemplate(this);
		for (Template template : childrenMap.values())
			template.accept(templateVisitor);
	}

	public List<Template> getChildren()
	{
		return children;
	}

	public boolean isActive()
	{
		return templateListener.isActive(this);
	}

	public void setChildren(List<Template> children)
	{
		this.children= children;
	}

	@Override
	public void setName(String name)
	{
		this.name= name;
	}

	public void setContent(Content<?> templateContent)
	{
		this.templateContent= templateContent;
	}

	@Override
	public boolean contains(Template template)
	{
	    if (children.contains(template))
		return true;
	    else
		return children.stream().anyMatch(t-> t.contains(template));
	}
}
