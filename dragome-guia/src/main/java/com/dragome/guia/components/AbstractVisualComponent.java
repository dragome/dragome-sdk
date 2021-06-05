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

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.guia.events.listeners.interfaces.DoubleClickListener;
import com.dragome.guia.events.listeners.interfaces.FocusListener;
import com.dragome.guia.events.listeners.interfaces.KeyUpListener;
import com.dragome.guia.events.listeners.interfaces.StyleChangedListener;
import com.dragome.model.interfaces.Layout;
import com.dragome.model.interfaces.Style;

public class AbstractVisualComponent extends DefaultEventProducer implements VisualComponent
{
	protected String name;
	protected VisualPanel parent;
	protected Style style= new DefaultStyle(this);
	protected Layout layout;

	protected static StyleChangedListener styleChangedListener= GuiaServiceLocator.getInstance().getStyleChangeListener();
	
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

	public void initLayout(Layout layout)
	{
	    setLayout(layout);
		layout.setAssociatedComponent(this);
	}

	public void setLayout(Layout layout)
	{
		this.layout= layout;
	}

	public Layout getLayout()
	{
		return layout;
	}

}
