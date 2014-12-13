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

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public abstract class OutputStream
{
	private boolean isClosed= false;

	public OutputStream()
	{
	}

	public void write(int b) throws IOException
	{
		
	}

	public void write(String s) throws IOException
	{
		
	}

	public void write(byte[] b) throws IOException
	{
		write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException
	{
		for (int i= 0; i < len; i++)
		{
			write(b[i + off]);
		}
	}

	public void flush() throws IOException
	{
		ensureOpen();
	}

	public void close() throws IOException
	{
		isClosed= true;
	}

	private void ensureOpen() throws IOException
	{
		if (isClosed)
		{
			throw new IOException("Stream is closed");
		}
	}
}
