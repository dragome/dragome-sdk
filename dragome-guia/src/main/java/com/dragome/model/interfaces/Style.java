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
package com.dragome.model.interfaces;

public interface Style
{
	public Style invert();
	public abstract VisualBorder getBorder();
	public abstract void setBorder(VisualBorder border);
	public VisualColor getBackgroundColor();
	public VisualColor getForegroundColor();
	public void setBackgroundColor(VisualColor backgroundColor);
	public void setForegroundColor(VisualColor foregroundColor);
	public VisualBounds getBounds();
	public void setBounds(VisualBounds aBounds);
	public void setInverted(boolean isInverted);
	public boolean isInverted();
	public VisualColor getDefaultBackgroundColor();
	public boolean isVisible();
	public void setVisible(boolean isVisible);
	public boolean isEnabled();
	public void setEnabled(boolean isEnabled);
	public String getName();
	public void setName(String name);
	public abstract void setVisualComponent(VisualComponent visualComponent);
	public abstract VisualComponent getVisualComponent();
	public void addClass(String name);
	public void removeClass(String styleName);
	boolean isSynchronized();
	void setSynchronized(boolean styleSynchronized);
	void fireStyleChanged();
}
