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


public final class Byte extends Number
{
    public static final byte   MIN_VALUE = -128;
    public static final byte   MAX_VALUE = 127;

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
