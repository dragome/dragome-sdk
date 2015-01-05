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
package java.io;

public class PrintWriter
{
	protected Writer out;
	private boolean errorState= false;

	public PrintWriter(Writer theOut)
	{
		out= theOut;
	}

	public boolean checkError()
	{
		return errorState;
	}

	public void print(String str)
	{
		try
		{
			out.write(str);
		}
		catch (IOException e)
		{
			setError();
		}
	}

	public void println(String str)
	{
		print(str);
		print("\n");
	}

	protected void setError()
	{
		errorState= true;
	}

	public void flush()
	{
	}
}
