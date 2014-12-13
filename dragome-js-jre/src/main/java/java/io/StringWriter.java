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

public class StringWriter extends Writer
{
	private StringBuffer buffer= new StringBuffer();

	public StringBuffer getBuffer()
	{
		return buffer;
	}

	public void write(String str) throws IOException
	{
		buffer.append(str);
	}

	public void write(String value, int start, int i) throws IOException
	{
		buffer.append(value, start, i);
	}

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Appendable append(char c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
