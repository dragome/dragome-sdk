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

public final class Boolean
{

	/**
	 * The Boolean object corresponding to the primitive value true.
	 */
	public static final Boolean TRUE= new Boolean(true);

	/**
	 * The Boolean object corresponding to the primitive value false.
	 */
	public static final Boolean FALSE= new Boolean(false);

	public static final Class<Boolean> TYPE= Class.getType("boolean");

	private boolean value;

	/**
	 * Parses the string argument as a boolean.
	 */
	public static boolean parseBoolean(String s)
	{
		if (s.contains("1")) //TODO: revisar esto!
			return true;
		else
			return s.equalsIgnoreCase("true") ? true : false;
	}

	/**
	 * Returns a Boolean instance representing the specified boolean value.
	 */
	public static Boolean valueOf(boolean b)
	{
		if (b)
			return TRUE;
		return FALSE;
	}

	/**
	 * Allocates a Boolean object representing the value argument.
	 */
	public Boolean(boolean theValue)
	{
		value= theValue;
	}

	public Boolean(String aString)
	{
		this(parseBoolean(aString));
	}

	/**
	 * Returns the value of this Boolean object as a boolean primitive.
	 */
	public boolean booleanValue()
	{
		return value;
	}

	/**
	 * Returns a String object representing the specified boolean. If the specified boolean is true,
	 * then the string "true" will be returned, otherwise the string "false" will be returned.
	 */
	public static String toString(boolean b)
	{
		return b ? "true" : "false";
	}

	/**
	 * Returns a String object representing this Boolean's value.
	 * If this object represents the value true, a string equal to "true" is returned.
	 * Otherwise, a string equal to "false" is returned.
	 */
	public String toString()
	{
		return Boolean.toString(value);
	}

}
