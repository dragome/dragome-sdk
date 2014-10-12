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

import java.util.List;

import com.dragome.model.interfaces.HasRenderer;
import com.dragome.model.interfaces.VisualButton;
import com.dragome.model.interfaces.VisualCheckbox;
import com.dragome.model.interfaces.VisualComponentFactory;
import com.dragome.model.interfaces.VisualLabel;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.interfaces.VisualTextField;

public class DefaultVisualComponentFactory implements VisualComponentFactory
{
	public VisualButton createButton(String aName, String aCaption)
	{
		return new VisualButtonImpl(aName, aCaption);
	}

	public VisualLabel createLabel(String aName, String aText)
	{
		return new VisualLabelImpl(aName, aText);
	}

	public VisualPanel createPanel(String aName)
	{
		return new VisualPanelImpl(aName);
	}

	public VisualTextField createTextField(String aName, String aText)
	{
		return new VisualTextFieldImpl(aName, aText);
	}

	public VisualCheckbox createCheckbox(String aName, String aCaption)
	{
		return new VisualCheckboxImpl(aName, aCaption, Boolean.FALSE);
	}

	public VisualRadioButton createRadioButton(String aName, String aCaption)
	{
		return new VisualRadioButtonImpl(aName, aCaption);
	}

	public HasRenderer createList(String aName, List<String> elements)
	{
		return new VisualListBoxImpl();
	}
}
