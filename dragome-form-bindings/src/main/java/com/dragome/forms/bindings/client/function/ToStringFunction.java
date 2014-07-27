/*
 * Copyright 2010 Andrew Pietsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.dragome.forms.bindings.client.function;

/**
 * A simple Function that converts any object to a String using it's toString method.
 */
public class ToStringFunction implements Function<String, Object>
{
	private String nullValue= "";

	public ToStringFunction()
	{
	}

	/**
	 * Creates a new instance that uses the specified string when the value to
	 * be formatted is <code>null</code>
	 *
	 * @param nullValue the value to return when the source value is null.
	 */
	public ToStringFunction(String nullValue)
	{
		this.nullValue= nullValue;
	}

	public String compute(Object value)
	{
		return value != null ? value.toString() : nullValue;
	}
}
