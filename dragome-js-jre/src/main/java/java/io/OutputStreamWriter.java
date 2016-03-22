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

public class OutputStreamWriter extends Writer
{

	private OutputStream out;

	/**
	 * Create an OutputStreamWriter that uses the default character encoding.
	 */
	public OutputStreamWriter(OutputStream theOut)
	{
		out= theOut;
	}

	public void write(String str) throws IOException
	{
		out.write(str);
	}

	public void write(String value, int start, int i) throws IOException
	{//TODO revisar
		if (i >= 0)
		{ // other cases tested by getChars()
			char buf[]= new char[i];
			value.getChars(start, start + i, buf, 0);

			write(new String(buf));
		}
		else
		{
			throw new StringIndexOutOfBoundsException();
		}
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public Writer append(CharSequence csq) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Writer append(CharSequence csq, int start, int end)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Writer append(char c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(char[] buf, int offset, int count) throws IOException
	{
		// TODO Auto-generated method stub

	}

}
