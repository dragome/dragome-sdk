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
package com.dragome.helpers;

import java.util.Iterator;

public class NullIterator<T> implements Iterator<T>
{
	public NullIterator()
	{
	}

	public boolean hasNext()
	{
		return false;
	}

	public T next()
	{
		return null;
	}

	public void remove()
	{
	}
}
