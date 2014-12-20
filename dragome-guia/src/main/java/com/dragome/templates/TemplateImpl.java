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

import com.dragome.guia.components.DefaultEventProducer;
import com.dragome.render.html.HTMLTemplateChangedListener;
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

	protected String name;
	protected Map<String, Template> children= new HashMap<String, Template>();
	protected Content<?> templateContent;
	protected Boolean inner= Boolean.FALSE;
	private Template parent;
	protected List<Template> childrenList= new ArrayList<Template>();
	protected TemplateListener templateListener= new HTMLTemplateChangedListener();

	protected TemplateImpl()
	{
	}

	protected TemplateImpl(String aName)
	{
		this();
		name= aName;
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
		Template templateElement= children.get(anAlias);
		if (templateElement == null)
			throw new RuntimeException("Cannot find template element '" + anAlias + "' in '" + name + "'");

		return templateElement;
	}

	public Map<String, Template> getChildrenMap()
	{
		return children;
	}
	public void setChildrenMap(Map<String, Template> templateElements)
	{
		this.children= templateElements;
		for (Entry<String, Template> entries : templateElements.entrySet())
		{
			childrenList.add(entries.getValue());
		}
	}

	public void setName(String name)
	{
		this.name= name;
	}

	public void setContent(Content<?> templateContent)
	{
		templateListener.contentChanged(this.templateContent, templateContent);

		this.templateContent= templateContent;
	}

	public Template setChild(String anAlias, Template templateElement)
	{
		return children.put(anAlias, templateElement);
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
		return children.get(id).getContent();
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
		templateListener.insertAfter(newChild, referenceChild, children, childrenList, this);
	}

	public void remove(Template child)
	{
		childrenList.remove(child);
		children.remove(child.getName());
		child.setParent(null);
		templateListener.childRemoved(child);
	}

	public void insertBefore(Template newChild, Template referenceChild)
	{
		templateListener.insertBefore(newChild, referenceChild, children, childrenList, this);
	}

	public void addChild(Template template)
	{
		Template previous= children.put(template.getName(), template);
		if (previous != null)
		{
			previous.setParent(null);
			childrenList.set(childrenList.indexOf(previous), template);
		}
		else
		{
			childrenList.add(template);
		}

		template.setParent(this);

		if (previous != null)
			templateListener.childReplaced(this, previous, template);
		else
			templateListener.childAdded(this, template);
	}

	public String toString()
	{
		return getName() + ": " + children;
	}

	public int hashCode()
	{
		return getName().hashCode();
	}

	public boolean equals(Object obj)
	{
		return obj != null && obj instanceof Template && getName().equals(((Template) obj).getName());
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
		Template previous= children.remove(childName);
		if (previous != child)
			throw new RuntimeException("This is not a child of this template!");

		children.put(aName, child);
	}

	public boolean hasChild(String aName)
	{
		Template templateElement= children.get(aName);
		return templateElement != null;
	}

	public void removeAll()
	{
		ArrayList<Template> childrenCopy= new ArrayList<Template>(childrenList);
		for (Template child : childrenCopy)
			remove(child);
	}

	public void accept(TemplateVisitor templateVisitor)
	{
		templateVisitor.visitTemplate(this);
		for (Template template : children.values())
			template.accept(templateVisitor);
	}
}
