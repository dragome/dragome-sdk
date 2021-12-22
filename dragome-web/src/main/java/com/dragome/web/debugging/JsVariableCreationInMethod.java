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
package com.dragome.web.debugging;

import java.io.Serializable;

public class JsVariableCreationInMethod extends CrossExecutionCommandImpl implements Serializable
{
	private static final long serialVersionUID= 8758308069708855643L;
	private String name;
	private ReferenceHolder valueReferenceHolder;

	public JsVariableCreationInMethod()
	{
	}

	public JsVariableCreationInMethod(ReferenceHolder callerHolder, String name, ReferenceHolder valueReferenceHolder, String methodName)
	{
		super(callerHolder, methodName);
		this.name= name;
		this.valueReferenceHolder= valueReferenceHolder;
	}

	public String getName()
	{
		return name;
	}

	public ReferenceHolder getValueReferenceHolder()
	{
		return valueReferenceHolder;
	}

	public void setName(String name)
	{
		this.name= name;
	}

	public void setValueReferenceHolder(ReferenceHolder valueReferenceHolder)
	{
		this.valueReferenceHolder= valueReferenceHolder;
	}

	public boolean returnsValue()
	{
		return false;
	}
}
