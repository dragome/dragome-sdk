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
package com.dragome.commons.javascript;


public final class ScriptHelper
{
	public static ScriptHelperInterface scriptHelperInterface;

	public static void put(String s, Object value, Object callerInstance)
	{
		scriptHelperInterface.put(s, value, callerInstance);
	}

	public static void put(String s, boolean value, Object callerInstance)
	{
		scriptHelperInterface.put(s, value, callerInstance);
	}

	public static void put(String s, double value, Object callerInstance)
	{
		scriptHelperInterface.put(s, value, callerInstance);
	}

	public static Object eval(String script, Object callerInstance)
	{
		return scriptHelperInterface.eval(script, callerInstance);
	}

	public static int evalInt(String jsCode, Object callerInstance)
	{
		return scriptHelperInterface.evalInt(jsCode, callerInstance);
	}

	public static long evalLong(String jsCode)
	{
		return scriptHelperInterface.evalLong(jsCode);
	}

	public static float evalFloat(String jsCode, Object callerInstance)
	{
		return scriptHelperInterface.evalFloat(jsCode);
	}

	public static double evalDouble(String jsCode, Object callerInstance)
	{
		return scriptHelperInterface.evalDouble(jsCode);
	}

	public static char evalChar(String jsCode, Object callerInstance)
	{
		return scriptHelperInterface.evalChar(jsCode);
	}

	public static boolean evalBoolean(String jsCode, Object callerInstance)
	{
		return scriptHelperInterface.evalBoolean(jsCode, callerInstance);
	}

	public static void evalNoResult(String script, Object callerInstance)
	{
		scriptHelperInterface.evalNoResult(script, callerInstance);
	}
}
