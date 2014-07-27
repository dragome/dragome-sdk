/**
 * Copyright 2007 Charlie Hubbard and Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package flexjson;

import java.io.IOException;
import java.io.Writer;

public class WriterOutputHandler implements OutputHandler
{

	private Writer out;

	public WriterOutputHandler(Writer out)
	{
		this.out= out;
	}

	public OutputHandler write(String value)
	{
		try
		{
			out.write(value);
		}
		catch (IOException e)
		{
			throw new JSONException("There was a problem writing output to the Writer.", e);
		}
		return this;
	}

	public int write(String value, int start, int end, String append)
	{
		try
		{
			out.write(value, start, end - start);
			out.write(append);
			return end + 1;
		}
		catch (IOException e)
		{
			throw new JSONException("There was a problem writing output to the Writer.", e);
		}
	}

	public int write(String value, int start, int end)
	{
		try
		{
			out.write(value, start, end - start);
			return end;
		}
		catch (IOException e)
		{
			throw new JSONException("There was a problem writing output to the Writer.", e);
		}
	}

	public String toString()
	{
		return out.toString();
	}
}
