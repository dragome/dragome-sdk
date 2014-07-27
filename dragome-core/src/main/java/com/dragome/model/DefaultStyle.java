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
import com.dragome.model.interfaces.VisualBorder;
import com.dragome.model.interfaces.VisualBounds;
import com.dragome.model.interfaces.VisualColor;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.listeners.StyleChangedListener;

public class DefaultStyle implements Style
{
	public static final VisualColor WHITE= new DefaultColor(255, 255, 255);
	public static final VisualColor BLACK= new DefaultColor(0, 0, 0);
	public static final VisualColor RED= new DefaultColor(255, 0, 0);
	public static final VisualColor GREY= new DefaultColor(155, 155, 155);
	public static final VisualColor BLUE= new DefaultColor(0, 0, 255);

	protected VisualComponent visualComponent;
	protected VisualBorder border;
	protected VisualColor backgroundColor= WHITE;
	protected VisualColor foregroundColor= BLACK;
	protected VisualBounds bounds= new DefaultBounds(0, 0, 0, 0);
	protected boolean inverted;
	protected boolean visible= true;
	protected boolean enabled= true;
	protected String name;
	protected boolean styleChangedFired= false;

	public DefaultStyle()
	{
	}

	public DefaultStyle(String aName)
	{
		name= aName;
	}

	public DefaultStyle(VisualComponent visualComponent)
	{
		this.visualComponent= visualComponent;
	}

	public Style invert()
	{
		if (!backgroundColor.equals(WHITE))
			setBackgroundColor(WHITE);
		else
			setBackgroundColor(GREY);

		return this;
	}

	public void fireStyleChanged()
	{
		if (!styleChangedFired)
		{
			styleChangedFired= true;

			if (visualComponent.hasListener(StyleChangedListener.class))
				visualComponent.getListener(StyleChangedListener.class).styleChanged(this);

			styleChangedFired= false;
		}
	}

	public static String rgb2html(int red, int green, int blue)
	{
		int result= red + 256 * green + 65536 * blue;
		return Integer.toHexString(result);
	}

	public VisualBorder getBorder()
	{
		return border;
	}

	public void setBorder(VisualBorder border)
	{
		this.border= border;
	}

	public VisualColor getBackgroundColor()
	{
		return backgroundColor;
	}
	public VisualColor getForegroundColor()
	{
		return foregroundColor;
	}

	public void setBackgroundColor(VisualColor backgroundColor)
	{
		this.backgroundColor= backgroundColor;
		fireStyleChanged();
	}
	public void setForegroundColor(VisualColor foregroundColor)
	{
		this.foregroundColor= foregroundColor;
		fireStyleChanged();
	}

	public VisualBounds getBounds()
	{
		return bounds;
	}

	public void setBounds(VisualBounds aBounds)
	{
		bounds= aBounds;
		fireStyleChanged();
	}

	public boolean isInverted()
	{
		return inverted;
	}

	public void setInverted(boolean inverted)
	{
		this.inverted= inverted;
		fireStyleChanged();
	}

	public VisualColor getDefaultBackgroundColor()
	{
		return new DefaultColor(255, 255, 255);
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean isVisible)
	{
		this.visible= isVisible;
		String show= "dragome-show";
		String hide= "dragome-hide";

		if (isVisible)
		{
			addClass(show);
			removeClass(hide);
		}
		else
		{
			addClass(hide);
			removeClass(show);
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean isEnabled)
	{
		this.enabled= isEnabled;
		fireStyleChanged();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (this.name == null)
			fireStyleChanged();

		if (!name.trim().equals(this.name))
		{
			this.name= name.trim();
			fireStyleChanged();
		}
	}

	public VisualComponent getVisualComponent()
	{
		return visualComponent;
	}

	public void setVisualComponent(VisualComponent visualComponent)
	{
		this.visualComponent= visualComponent;
	}

	public void addClass(String name)
	{
		fireStyleChanged();
		String componentStyleName= getName();
		if (componentStyleName == null)
			componentStyleName= "";

		name= name.trim();
		componentStyleName= componentStyleName.trim();

		if (!isClassPresent(name, componentStyleName))
			setName(componentStyleName + " " + name);
	}

	private boolean isClassPresent(String name, String componentStyleName)
	{
		return componentStyleName.equals(name.trim()) || componentStyleName.indexOf(name + " ") == 0 || componentStyleName.indexOf(" " + name) == componentStyleName.length() - name.length() - 1 || componentStyleName.indexOf(" " + name + " ") > -1;
	}

	public void removeClass(String styleName)
	{
		fireStyleChanged();

		String componentStyleName= getName();
		if (componentStyleName == null)
			componentStyleName= "";

		if (isClassPresent(styleName, componentStyleName))
			setName(componentStyleName.replace(styleName, ""));
	}
}
