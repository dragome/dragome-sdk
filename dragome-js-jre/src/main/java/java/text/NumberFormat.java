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
package java.text;

public class NumberFormat extends Format
{
	private static Format format= new NumberFormat();

	public static Format getInstance()
	{
		return format;
	}

	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
	{
		return toAppendTo.append(obj + "");
	}
}
