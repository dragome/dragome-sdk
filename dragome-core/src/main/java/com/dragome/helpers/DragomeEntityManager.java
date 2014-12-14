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

import java.util.Hashtable;
import java.util.Map;

public class DragomeEntityManager
{
	protected static Map<String, Object> entities= new Hashtable<String, Object>();

	public static String add(Object entity)
	{
		String identityHashCode= getEntityId(entity);
		entities.put(identityHashCode, entity);
		return identityHashCode;
	}

	public static Object get(String id)
	{
		return entities.get(id);
	}

	public static String getEntityId(Object object)
	{
		return System.identityHashCode(object) + "";
	}

	public static void clear()
	{
		entities.clear();
	}
}
