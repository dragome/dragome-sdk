/*
 * Copyright 2009 Andrew Pietsch 
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

package com.dragome.forms.bindings.client.format;

import java.util.List;

/**
 * This class isn't i18n safe.
 */
public class CollectionToStringFormat<T> implements ListDisplayFormat<T>
{
	private static final String DEFAULT_JOIN_STRING= ", ";

	public static final CollectionToStringFormat<Object> DEFAULT_INSTANCE= new CollectionToStringFormat<Object>();

	private DisplayFormat<? super T> valueFormat;
	private String joinString;

	public CollectionToStringFormat()
	{
		this(ToStringFormat.defaultInstance(), DEFAULT_JOIN_STRING);
	}

	public CollectionToStringFormat(DisplayFormat<? super T> valueFormat, String joinString)
	{
		if (valueFormat == null)
		{
			throw new NullPointerException("format is null");
		}

		if (joinString == null)
		{
			throw new NullPointerException("joinString is null");
		}
		this.setValueFormat(valueFormat);
		this.joinString= joinString;
	}

	public String format(List<? extends T> values)
	{
		StringBuilder buf= null;
		for (T value : values)
		{
			if (buf == null)
			{
				buf= new StringBuilder();
			}
			else
			{
				buf.append(joinString);
			}
			buf.append(getValueFormat().format(value));
		}

		return buf != null ? buf.toString() : "";
	}

	public void setJoinString(String joinString)
	{
		if (joinString == null)
		{
			throw new NullPointerException("joinString is null");
		}
		this.joinString= joinString;
	}

	public DisplayFormat<? super T> getValueFormat()
	{
		return valueFormat;
	}

	public void setValueFormat(DisplayFormat<? super T> valueFormat)
	{
		this.valueFormat= valueFormat;
	}
}