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

public class VisualRadioButtonImpl extends SelectableButtonComponent implements VisualRadioButton
{
	protected String buttonGroupId;

	public String getButtonGroup()
	{
		return buttonGroupId;
	}

	public void setButtonGroup(String buttonGroupId)
	{
		this.buttonGroupId= buttonGroupId;
	}

	public VisualRadioButtonImpl(String aName, String aCaption)
	{
		super(aName, aCaption);
	}

	public VisualRadioButtonImpl(String aGroupId, String aName, String aCaption)
	{
		super(aName, aCaption);
		this.buttonGroupId= aGroupId;
	}
}
