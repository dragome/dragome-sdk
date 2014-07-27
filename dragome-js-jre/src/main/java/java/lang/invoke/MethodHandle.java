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
package java.lang.invoke;

import java.lang.invoke.MethodHandles.Lookup;

import com.dragome.commons.javascript.ScriptHelper;

public class MethodHandle
{
	private Object x;
	private Lookup lookup;

	public MethodHandle(Lookup lookup)
	{
		this.lookup= lookup;
	}

	public MethodHandle bindTo(Object x)
	{
		this.x= x;
		return this;
	}

	public Object invokeWithArguments(Object... arguments) throws Throwable
	{
		ScriptHelper.put("type", lookup.getSpecialCaller(), this);
		ScriptHelper.put("args", arguments, this);
		ScriptHelper.put("proxy", x, this);
		ScriptHelper.put("method", lookup.getMethod(), this);
		Object o= ScriptHelper.eval("type.$$$nativeClass.$$members[method.$$$signature].apply(proxy, args)", this);
		return o;
	}
}
