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
package java.lang;

public class ThreadLocal<T>
{
	private T value;

	public ThreadLocal()
	{
		value= initialValue();
	}

	public void remove()
	{
		value= null;
	}

	public T get()
	{
		if (value == null)
			value= initialValue();

		return value;
	}

	protected T initialValue()
	{
		return value;
	}

	public void set(T value)
	{
		this.value= value;
	}
}
