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

import com.dragome.web.debugging.interfaces.CrossExecutionResult;

public class CrossExecutionResultImpl implements CrossExecutionResult, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID= -4124521121973981016L;
	private String result;

	public CrossExecutionResultImpl()
	{
	}

	public CrossExecutionResultImpl(String result)
	{
		this.result= result;
	}

	public String getResult()
	{
		return result;
	}

}
