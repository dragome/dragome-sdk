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

import com.dragome.model.listeners.ClickListener;
import com.dragome.model.listeners.DoubleClickListener;
import com.dragome.model.listeners.KeyUpListener;

public interface VisualComponent extends EventProducer, HasVisible
{
	public String getName();
	public VisualComponent setName(String name);

	public Style getStyle();
	public void setStyle(Style style);

	public VisualPanel getParent();
	public void setParent(VisualPanel parent);

	public void addClickListener(ClickListener clickListener);
	public void addDoubleClickListener(DoubleClickListener doubleClickListener);
	public void addKeyListener(KeyUpListener keyUpListener);
	void focus();
}
