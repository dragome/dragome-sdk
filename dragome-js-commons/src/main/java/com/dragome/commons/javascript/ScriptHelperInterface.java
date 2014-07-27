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

public interface ScriptHelperInterface
{
	public void put(String s, Object value, Object caller);

	public void put(String s, boolean value, Object caller);

	public void put(String s, double value, Object caller);

	public Object eval(String script, Object caller);

	public int evalInt(String jsCode, Object caller);

	public long evalLong(String jsCode);

	public float evalFloat(String jsCode);

	public double evalDouble(String jsCode);

	public char evalChar(String jsCode);

	public boolean evalBoolean(String jsCode, Object caller);

	public void evalNoResult(String script, Object callerInstance);
}
