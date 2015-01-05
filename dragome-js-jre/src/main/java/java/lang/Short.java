/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
