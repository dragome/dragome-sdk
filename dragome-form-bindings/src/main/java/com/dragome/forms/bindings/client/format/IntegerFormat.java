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

/**
 *
 */
public class IntegerFormat implements Format<Integer>
{
	public String format(Integer value)
	{
		return value != null ? Integer.toString(value) : null;
	}

	public Integer parse(String text) throws FormatException
	{
		try
		{
			if (text != null && text.trim().length() > 0)
			{
				int integer= Integer.parseInt(text.trim());

				validateInteger(integer);

				return integer;
			}
			else
			{
				return null;
			}
		}
		catch (NumberFormatException e)
		{
			throw toFormatException(e, text);
		}
	}

	/**
	 * This method is called to create a FormatException whenever a NumberFormatException is
	 * thrown.  Subclasses can override this method to create more meaningful messages.
	 *
	 * @param e    the exectption that was thrown.
	 * @param text the text the was being parsed.
	 * @return a new FormatException that will be thrown by the parse method.
	 *
	 * @deprecated override {@link #toNumberFormatMessage(String, NumberFormatException)} instead.
	 */
	protected final FormatException toFormatException(NumberFormatException e, String text)
	{
		return new FormatException(toNumberFormatMessage(text, e), e);
	}

	/**
	 * This method is called to create the message for the format exception thrown when
	 * <code>Integer.parseInt</code> throws a {@link NumberFormatException}.
	 *
	 * @param text the text the was being parsed.
	 * @param e    the exectption that was thrown.
	 * @return a new FormatException that will be thrown by the parse method.
	 */
	protected String toNumberFormatMessage(String text, NumberFormatException e)
	{
		return "'" + text + "' is not a valid integer";
	}

	/**
	 * This method is invoked once the text has been successfully parsed into an integer.  Subclasses
	 * can override this method to apply additional constraints to the integer value by throwing a
	 * FormatException if the value is outside any desired limits.
	 * <p/>
	 * The default implementation does nothing.
	 *
	 * @param integer the integer value
	 * @throws FormatException if the value violates a constraint.
	 */
	protected void validateInteger(int integer) throws FormatException
	{
		// subclasses can hook in here
	}
}