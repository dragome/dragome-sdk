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


public final class Byte extends Number
{

	public static final Class<Byte> TYPE= Class.getType("byte");

	private byte value;

	/**
	 * Constructs a newly allocated Byte object that represents the specified byte value.
	 */
	public Byte(byte value)
	{
		this.value= value;
	}

	public Byte(String string)
    {
		this.value= Byte.parseByte(string);
    }

	/**
	 * Compares this object to the specified object.
	 */
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Byte))
			return false;
		return ((Byte) obj).value == value;
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

	/**
	 * Returns a String object representing this Byte's value.
	 */
	public String toString()
	{
		return Integer.toString((int) value);
	}

	public static Byte valueOf(byte b)
	{
		return new Byte(b);
	}

	public static byte parseByte(String s, int radix)
	{
		return (byte) Integer.parseInt(s, radix);
	}

	public static byte parseByte(String s) throws NumberFormatException
	{
		return parseByte(s, 10);
	}
}
