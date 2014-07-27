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

import java.util.List;

import com.dragome.model.VisualRadioButton;

public interface VisualComponentFactory
{
	VisualButton createButton(String aName, String aCaption);
	VisualLabel createLabel(String aName, String aText);
	VisualPanel createPanel(String aName);
	VisualTextField createTextField(String aName, String aText);
	VisualCheckbox createCheckbox(String aName, String aCaption);
	VisualRadioButton createRadioButton(String aName, String aCaption);
	HasRenderer createList(String aName, List<String> elements);
}
