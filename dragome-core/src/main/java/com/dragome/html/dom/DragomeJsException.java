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
package com.dragome.debugging.execution;

public class DragomeJsException extends RuntimeException
{
	private String message;
	private Exception originalException;

	public DragomeJsException()
	{
	}

	public Exception getOriginalException()
	{
		return originalException;
	}

	public void setOriginalException(Exception originalException)
	{
		this.originalException= originalException;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message= message;
	}

	public DragomeJsException(Exception cause, String message)
	{
		originalException= cause;
		this.message= message;
	}

}
