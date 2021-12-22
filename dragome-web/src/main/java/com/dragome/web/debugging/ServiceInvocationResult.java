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
import java.util.List;

import com.dragome.services.ServiceInvocation;

public class ServiceInvocationResult implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID= 4751276683327600386L;
	private Object objectResult;

	public Object getObjectResult()
	{
		return objectResult;
	}

	public void setObjectResult(Object objectResult)
	{
		this.objectResult= objectResult;
	}

	private String id;
	private List<?> listResult;

	public List<?> getListResult()
	{
		return listResult;
	}

	public void setListResult(List<?> listResult)
	{
		this.listResult= listResult;
	}

	public ServiceInvocationResult()
	{
	}

	public ServiceInvocationResult(ServiceInvocation serviceInvocation, Object result)
	{
		if (result instanceof List)
			this.listResult= (List<?>) result;
		else
			this.objectResult= result;

		this.id= serviceInvocation.getId();
	}

	public String getId()
	{
		return id;
	}

	public Object obtainRealResult()
	{
		return listResult != null ? listResult : objectResult;
	}

	public void setId(String id)
	{
		this.id= id;
	}
}
