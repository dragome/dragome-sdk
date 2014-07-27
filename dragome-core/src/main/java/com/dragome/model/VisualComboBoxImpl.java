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

import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.VisualComboBox;

public class VisualComboBoxImpl<T> extends VisualListBoxImpl<T> implements VisualComboBox<T>
{
	public VisualComboBoxImpl()
	{
		super();
	}

	public VisualComboBoxImpl(String name, Iterable<T> acceptableValues)
	{
		super(name, acceptableValues);
	}

	public VisualComboBoxImpl(String name, Renderer<T> renderer, Iterable<T> acceptableValues)
	{
		super(name, renderer, acceptableValues);
	}

	public VisualComboBoxImpl(String name, Iterable<T> acceptableValues, T selectedValue)
	{
		this(name, acceptableValues);
		setValue(selectedValue);
	}
}
