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

public final class Short extends Number
{
	public static final short MIN_VALUE= -32768;

	/**
	 * A constant holding the maximum value a {@code short} can
	 * have, 2<sup>15</sup>-1.
	 */
	public static final short MAX_VALUE= 32767;

	public static final Class<Short> TYPE= Class.getType("short");

	private short value;

	/**
	 * Constructs a newly allocated Byte object that represents the specified byte value.
	 */
	public Short(short value)
	{
		this.value= value;
	}
	
	public Short(String aString)
	{
		this(Short.parseShort(aString));
	}

	public double doubleValue()
	{
		return (double) value;
	}

	public float floatValue()
	{
		return (float) value;
	}

	public int intValue()
	{
		return (int) value;
	}

	public long longValue()
	{
		return (long) value;
	}

	public static short parseShort(String string) throws NumberFormatException
	{
		return (short) Integer.parseInt(string);
	}

	public static Short valueOf(short s)
	{
		return new Short(s);
	}

	public static String toString(short s)
	{
		return Integer.toString((int) s, 10);
	}
	
	public String toString()
	{
		return toString(value);
	}

}
