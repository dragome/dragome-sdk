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
package java.util;

public class Hashtable<K, V> extends HashMap<K, V>
{
	public synchronized boolean contains(V value)
	{
		return containsValue(value);
	}

	public synchronized Enumeration elements()
	{
		return Collections.enumeration(values());
	}

	public synchronized Enumeration keys()
	{
		return Collections.enumeration(keySet());
	}
}
