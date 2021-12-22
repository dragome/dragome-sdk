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

public class JavascriptReference implements Serializable
{
	private static final long serialVersionUID= 526944876267959879L;
	private String jsId;
	private String className;

	public String getJsId()
	{
		return jsId;
	}

	public void setJsId(String jsId)
	{
		this.jsId= jsId;
	}

	public JavascriptReference()
	{
	}

	public JavascriptReference(String jsId)
	{
		this.jsId= jsId;
	}

	public JavascriptReference(String className, String jsId)
	{
		this(jsId);
		this.setClassName(className);
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className= className;
	}
}
