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
package com.dragome.guia.components;

import java.util.ArrayList;
import java.util.List;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.events.listeners.interfaces.PanelListener;
import com.dragome.model.interfaces.Layout;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;

public class VisualPanelImpl extends AbstractVisualComponent implements VisualPanel
{
	protected List<VisualComponent> children= new ArrayList<VisualComponent>();
	public VisualPanelImpl()
	{
		children= new ArrayList<VisualComponent>();
	}

	public VisualPanelImpl(String aName)
	{
		this();
		setName(aName);
	}

	public VisualPanelImpl(String aName, Layout aLayout)
	{
		this(aName);
		setLayout(aLayout);
		layout.setAssociatedComponent(this);
	}

	public VisualPanelImpl(Layout layout)
	{
		initLayout(layout);
	}

	public VisualPanelImpl(String aName, Template template)
	{
		this(template);
		setName(aName);
	}

	public VisualPanelImpl(Template template)
	{
		this(new TemplateLayout(template));
	}

	public VisualPanel addChild(VisualComponent visualComponent)
	{
		children.add(visualComponent);
		visualComponent.setParent(this);
		if (hasListener(PanelListener.class))
			getListener(PanelListener.class).componentAdded(visualComponent);
		return this;
	}

	public List<? extends VisualComponent> getChildren()
	{
		return children;
	}

	public void setChildren(List<? extends VisualComponent> children)
	{
		this.children= (List<VisualComponent>) children;
	}
	
	public void updateChildren(List<? extends VisualComponent> elements)
	{
		children.clear();
		for (VisualComponent child : elements)
			addChild(child);
	}

	public void removeChild(VisualComponent aVisualComponent)
	{
		children.remove(aVisualComponent);
		aVisualComponent.setParent(null);
		if (hasListener(PanelListener.class))
			getListener(PanelListener.class).componentRemoved(aVisualComponent);
	}

	public VisualComponent getChildByName(String aName)
	{
		for (VisualComponent child : children)
		{
			if (aName.equals(child.getName()))
				return child;
		}
		return null;
	}

	public void replaceChild(VisualComponent child)
	{
		VisualComponent childByName= getChildByName(child.getName());
		children.remove(childByName);
		childByName.setParent(null);
		children.add(child);
		child.setParent(this);
		if (hasListener(PanelListener.class))
			getListener(PanelListener.class).childReplaced(childByName, child);
	}

	public void addOrReplaceChild(VisualComponent component)
	{
		if (getChildByName(component.getName()) != null)
			replaceChild(component);
		else
			addChild(component);
	}

	public void replaceChild(VisualComponent newChild, VisualComponent oldChild)
	{
		int indexOf= children.indexOf(oldChild);
		if (indexOf != -1)
		{
			children.set(indexOf, newChild);
			oldChild.setParent(null);
			newChild.setParent(this);
			if (hasListener(PanelListener.class))
				getListener(PanelListener.class).childReplaced(oldChild, newChild);
		}
	}
}
