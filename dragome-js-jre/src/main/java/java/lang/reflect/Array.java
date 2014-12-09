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
package java.lang.reflect;

import com.dragome.commons.javascript.ScriptHelper;

public final class Array
{
	public static Object newInstance(Class<?> componentType, int length)
	{
		ScriptHelper.put("l", length, null);
		Object eval= ScriptHelper.eval("new Array(l)", null);
		return eval;
	}

	public static void set(Object array, int i, Object v)
	{
		ScriptHelper.put("anArray", array, null);
		ScriptHelper.put("i", i, null);
		ScriptHelper.put("aValue", v, null);
		ScriptHelper.eval("anArray[i]=v;", null);
		//TODO revisar
	}

	public static int getLength(Object array)
	{
		ScriptHelper.put("anArray", array, null);
		return ScriptHelper.evalInt("anArray.length", null);
	}

	public static Object get(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		ScriptHelper.put("anArray", array, null);
		ScriptHelper.put("i", index, null);
		return ScriptHelper.eval("anArray[i]", null);
	}
}
