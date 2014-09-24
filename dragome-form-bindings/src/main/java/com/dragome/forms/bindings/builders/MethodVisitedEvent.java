/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.forms.bindings.builders;

public class MethodVisitedEvent
{
	private Object instance;
	private String name;

	public MethodVisitedEvent(Object instance, String name)
	{
		this.instance= instance;
		this.name= name;
	}

	public Object getInstance()
	{
		return instance;
	}

	public String getName()
	{
		return name;
	}

	public int hashCode()
	{
		return name.hashCode();
	}

	public boolean equals(Object obj)
	{
		MethodVisitedEvent methodVisitedEvent= (MethodVisitedEvent) obj;
		return instance == methodVisitedEvent.instance && name.equals(methodVisitedEvent.name);
	}

	private static String getPropertyName(String methodName)
	{
		if (methodName.length() > 3 &&(methodName.startsWith("get") || methodName.startsWith("set")))
			return methodName.toLowerCase().charAt(3) + methodName.substring(4);
		else if (methodName.length() > 2 && methodName.startsWith("is"))
			return methodName.toLowerCase().charAt(2) + methodName.substring(3);

		return "";
	}

	public boolean isSameProperty(MethodVisitedEvent event)
	{
		return getPropertyName(name).equals(getPropertyName(event.name)) && instance == event.instance;
	}

}
