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

import com.dragome.helpers.DragomeEntityManager;

public class ReferenceHolder
{
	protected String id;
	protected Class<?> type;
	private String value;
	private Boolean booleanValue;

	public Boolean getBooleanValue()
	{
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue)
	{
		this.booleanValue= booleanValue;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value= value;
	}

	public ReferenceHolder()
	{
	}

	public ReferenceHolder(String id, Class<?> type, String value, Boolean booleanValue)
	{
		this.id= id;
		this.type= type;
		this.value= value;
		this.booleanValue= booleanValue;
	}

	public ReferenceHolder(Object instance)
	{
		if (instance != null)
		{
			if (instance instanceof Boolean)
				booleanValue= (Boolean) instance;
			else if (instance instanceof String)
				value= (String) instance;
			else if (instance instanceof JavascriptReference)
			{
				JavascriptReference javascriptReference= (JavascriptReference) instance;
				id= javascriptReference.getJsId() + "";
			}
			else
			{
				id= DragomeEntityManager.add(instance);
			}
			type= instance.getClass();
		}
	}

	public ReferenceHolder(boolean booleanValue)
	{
		this.booleanValue= new Boolean(booleanValue);
		type= boolean.class;
	}

	public String getId()
	{
		return id;
	}

	public Class getType()
	{
		return type;
	}

	public void setId(String id)
	{
		this.id= id;
	}

	public void setType(Class type)
	{
		this.type= type;
	}
}
