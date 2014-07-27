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
package com.dragome.model;

import java.util.ArrayList;
import java.util.List;

import com.dragome.model.interfaces.Layout;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.listeners.PanelListener;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;

public class VisualPanelImpl extends AbstractVisualComponent implements VisualPanel
{
	protected List<VisualComponent> children= new ArrayList<VisualComponent>();
	protected Layout layout;

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
		layout.setAssociatedPanel(this);
	}

	public VisualPanelImpl(Layout layout)
	{
		initLayout(layout);
	}

	public void initLayout(Layout layout)
    {
	    setLayout(layout);
		layout.setAssociatedPanel(this);
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

	public void setLayout(Layout layout)
	{
		this.layout= layout;
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

	public void setChildren(List<? extends VisualComponent> elements)
	{
		children.clear();
		for (VisualComponent child : elements)
			addChild(child);
	}

	public Layout getLayout()
	{
		return layout;
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
