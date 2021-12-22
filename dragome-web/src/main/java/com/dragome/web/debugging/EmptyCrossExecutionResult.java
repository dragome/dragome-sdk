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

public class EmptyCrossExecutionResult implements CrossExecutionResult, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID= 6629412530763766075L;

	public EmptyCrossExecutionResult()
	{
	}

	public String getResult()
	{
		return "";
	}

}
