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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateVisitor;

public class TemplateImpl extends AbstractTemplate
{
	private TemplateHandler templateHandler= GuiaServiceLocator.getInstance().getTemplateHandler();

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

	protected Map<String, Template> childrenMap= new HashMap<String, Template>();
	private Content<?> templateContent;
	protected Boolean inner= Boolean.FALSE;
	Template parent;
	protected List<Template> children= new ArrayList<Template>();

	public TemplateImpl()
	{
		super();
	}

	public TemplateImpl(String aName)
	{
		this();
		setName(aName);
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
		Template previous= addToChildren(newChild, t -> {
			int indexOf= children.indexOf(referenceChild);
			children.add(indexOf + 1, t);
		});

		templateListener.insertAfter(newChild, referenceChild, new HashMap<String, Template>(childrenMap), new ArrayList<Template>(children), this);
	}

	public void remove(Template child)
	{
		children.remove(child);
		childrenMap.remove(child.getName());
		child.setParent(null);
		templateListener.childRemoved(child);
	}

	public void insertBefore(Template newChild, Template referenceChild)
	{
		Template previous= addToChildren(newChild, t -> {
			int indexOf= children.indexOf(referenceChild);
			children.add(indexOf, t);
		});

		templateListener.insertBefore(newChild, referenceChild, new HashMap<String, Template>(childrenMap), new ArrayList<Template>(children), this);
	}

	public void addChild(Template template)
	{
		Template previous= addToChildren(template, t -> children.add(t));

		if (previous != null)
			templateListener.childReplaced(this, previous, template);
		else
			templateListener.childAdded(this, template);
	}

	public Template addToChildren(Template template, Consumer<Template> templateInserter)
	{
		Template previous= null;
		String templateName= template.getName();
		if (templateName != null)
		{
			if (hasChild(templateName))
			{
				Template child= getChild(templateName);
				children.remove(child);
			}
			previous= childrenMap.put(templateName, template);
			if (previous != null)
				previous.setParent(null);
		}

		Consumer<Template> consumer= templateInserter;

		consumer.accept(template);

		if (template.getParent() != null)
		{
			Template parent2= template.getParent();
			parent2.remove(template);
		}

		template.setParent(this);

		return previous;
	}

	//	public int hashCode()
	//	{
	//		return getName().hashCode() + children.hashCode();
	//	}
	//
	//	public boolean equals(Object obj)
	//	{
	//		if (obj instanceof Template)
	//		{
	//			Template other= (Template) obj;
	//			boolean childrenEqual= children.size() == other.getChildren().size();
	//			return name.equals(other.getName()) && childrenEqual;
	//		}
	//		else
	//			return false;
	//	}

	public void removeChild(String name)
	{
		if (hasChild(name))
			remove(getChild(name));
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

	public List<Template> getChildren()
	{
		return Collections.unmodifiableList(children);
	}

	public void setChildren(List<Template> children)
	{
		this.children= children;
	}

	public void setContent(Content<?> templateContent)
	{
		this.templateContent= templateContent;
	}

	public void hide()
	{
		templateHandler.makeInvisible(this);
	}

	public void show()
	{
		templateHandler.makeVisible(this);
	}

	public Template createClone()
	{
		return templateHandler.clone(this);
	}
}
