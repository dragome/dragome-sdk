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

public class JsMethodReferenceCreationInMethod extends CrossExecutionCommandImpl
{
	private String name;
	private String methodSignature;
	private String declaringClassName;

	public JsMethodReferenceCreationInMethod()
	{
	}

	public JsMethodReferenceCreationInMethod(ReferenceHolder callerHolder, String name, String methodSignature, String callerMethodName, String declaringClassName)
	{
		super(callerHolder, callerMethodName);
		this.name= name;
		this.declaringClassName= declaringClassName;
		this.methodSignature= methodSignature;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name= name;
	}

	public boolean returnsValue()
	{
		return false;
	}

	public String getMethodSignature()
	{
		return methodSignature;
	}

	public void setMethodSignature(String methodReferenceName)
	{
		this.methodSignature= methodReferenceName;
	}

	public String getDeclaringClassName()
	{
		return declaringClassName;
	}
}
