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

	public static Boolean valueOf(String aBoolean)
	{
		return "true".equalsIgnoreCase(aBoolean);
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

//	public boolean equals(Object obj)
//	{
//		if (obj instanceof Boolean)
//		{
//			return value == ((Boolean) obj).booleanValue();
//		}
//		return false;
//	}
}
