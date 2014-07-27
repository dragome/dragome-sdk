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

import com.dragome.model.interfaces.Style;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.listeners.ClickListener;
import com.dragome.model.listeners.DoubleClickListener;
import com.dragome.model.listeners.FocusListener;
import com.dragome.model.listeners.KeyUpListener;
import com.dragome.model.listeners.StyleChangedListener;
import com.dragome.render.html.components.DefaultStyleChangedListener;

public class AbstractVisualComponent extends DefaultEventProducer implements VisualComponent
{
	protected String name;
	protected VisualPanel parent;
	protected Style style= new DefaultStyle(this);

	protected static DefaultStyleChangedListener styleChangedListener= new DefaultStyleChangedListener();

	public AbstractVisualComponent()
	{
		initListeners();
	}

	private void initListeners()
	{
		if (!hasListener(StyleChangedListener.class))
			addListener(StyleChangedListener.class, styleChangedListener);
	}

	public AbstractVisualComponent(String name)
	{
		super();
		initListeners();
		this.name= name;
	}

	public String getName()
	{
		return name;
	}

	public VisualPanel getParent()
	{
		return parent;
	}

	public Style getStyle()
	{
		return style;
	}

	public VisualComponent setName(String name)
	{
		this.name= name;
		return this;
	}

	public void setParent(VisualPanel parent)
	{
		this.parent= parent;
	}

	public void setStyle(Style style)
	{
		this.style= style;
	}

	public void addClickListener(ClickListener clickListener)
	{
		addListener(ClickListener.class, clickListener);
	}
	
	public void addDoubleClickListener(DoubleClickListener doubleClickListener)
	{
		addListener(DoubleClickListener.class, doubleClickListener);
	}
	
	public void addKeyListener(KeyUpListener keyUpListener)
	{
		addListener(KeyUpListener.class, keyUpListener);
	}
	
	public void setVisible(boolean visible)
    {
		getStyle().setVisible(visible);
    }
	
	public boolean isVisible()
	{
	    return getStyle().isVisible();
	}
	
	public void focus()
    {
		if (hasListener(FocusListener.class))
			getListener(FocusListener.class).focusGained(this);
    }

}
