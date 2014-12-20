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

public abstract class InputStream implements Closeable
{
	public abstract int read() throws IOException;

	public int read(byte[] buffer, int i, int bufferSize) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int read(byte[] chunk) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public long skip(long n) throws IOException
	{
		return 0;
	}

	public int available() throws IOException
	{
		return 0;
	}

	public synchronized void mark(int readlimit)
	{
	}

	public synchronized void reset() throws IOException
	{
	}

	public boolean markSupported()
	{
		return false;
	}
}
