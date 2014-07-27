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

import com.dragome.commons.javascript.ScriptHelper;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public class Runtime
{

	/**
	 * Returns the runtime object associated with the current Java application.
	 */
	public static Runtime getRuntime()
	{
		return new Runtime();
	}

	/**
	 * Enables/Disables tracing of method calls.
	 */
	public void traceMethodCalls(boolean on)
	{
		ScriptHelper.put("tracing", on, this);
		ScriptHelper.eval("dragomeJs.tracing = tracing", this);
	}

}
