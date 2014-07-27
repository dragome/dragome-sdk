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
package com.dragome.debugging;

public class CrossExecutionCommandImpl implements CrossExecutionCommand
{
	protected String methodName;
	private ReferenceHolder callerReferenceHolder;

	public ReferenceHolder getCallerReferenceHolder()
	{
		return callerReferenceHolder;
	}

	public void setCallerReferenceHolder(ReferenceHolder callerReferenceHolder)
	{
		this.callerReferenceHolder= callerReferenceHolder;
	}

	public CrossExecutionCommandImpl()
	{
	}

	public CrossExecutionCommandImpl(ReferenceHolder callerHolder, String methodName)
	{
		this.callerReferenceHolder= callerHolder;
		this.methodName= methodName;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName= methodName;
	}

	public boolean returnsValue()
	{
		return true;
	}

}
