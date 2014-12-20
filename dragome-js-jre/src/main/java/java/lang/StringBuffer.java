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

import com.dragome.commons.javascript.ScriptHelper;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public class StringBuffer
{

	public StringBuffer()
	{
		ScriptHelper.eval("this.buffer = new Array()", this);
	}

	public StringBuffer(String s)
	{
		this();
		append(s);
	}

	public StringBuffer(int length)
	{
		this();
	}

	public int length()
	{
		consolidate();
		return ScriptHelper.evalInt("this.buffer[0].length", this);
	}

	public void ensureCapacity(int minimumCapacity)
	{
	}

	/**
	 * Sets the length of this string buffer.
	 */
	public void setLength(int newLength)
	{
		int currentLength= length();
		if (newLength < currentLength)
		{
			delete(newLength, currentLength);
		}
		else if (newLength > currentLength)
		{
			for (int i= currentLength; i < newLength; i++)
			{
				append('\u0000');
			}
		}
	}

	public char charAt(int index)
	{
		consolidate();
		ScriptHelper.put("index", index, this);
		return ScriptHelper.evalChar("this.buffer[0].charCodeAt(index)", this);
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)
	{
		throw new UnsupportedOperationException();
	}

	public void setCharAt(int index, char ch)
	{
		throw new UnsupportedOperationException();
	}

	public StringBuffer append(Object obj)
	{
		return append(String.valueOf(obj));
	}

	public StringBuffer append(String s)
	{
		if (s == null)
			s= "null";
		ScriptHelper.put("s", s, this);
		ScriptHelper.eval("this.buffer.push(s)", this);
		return this;
	}

	public StringBuffer append(char[] str)
	{
		return append(str, 0, str.length);
	}

	public StringBuffer append(char[] str, int offset, int len)
	{
		for (int i= 0; i < len; i++)
		{
			append(str[i + offset]);
		}
		return this;
	}

	public StringBuffer append(boolean b)
	{
		append(String.valueOf(b));
		return this;
	}

	public StringBuffer append(char c)
	{
		append(String.valueOf(c));
		return this;
	}

	public StringBuffer append(int i)
	{
		append(String.valueOf(i));
		return this;
	}

	public StringBuffer append(long l)
	{
		append(String.valueOf(l));
		return this;
	}

	public StringBuffer append(float f)
	{
		append(String.valueOf(f));
		return this;
	}

	public StringBuffer append(double d)
	{
		append(String.valueOf(d));
		return this;
	}

	public StringBuffer delete(int start, int end)
	{
		consolidate();
		ScriptHelper.put("start", start, this);
		ScriptHelper.put("end", end, this);
		ScriptHelper.eval("s = this.buffer[0]; this.buffer[0] = s.substring(0, start) + s.substring(end)", this);
		return this;
	}

	public StringBuffer deleteCharAt(int index)
	{
		return delete(index, index + 1);
	}

	public StringBuffer insert(int offset, Object obj)
	{
		return insert(offset, String.valueOf(obj));
	}

	public StringBuffer insert(int offset, String str)
	{
		consolidate();
		ScriptHelper.put("offset", offset, this);
		ScriptHelper.put("str", str, this);
		ScriptHelper.eval("var os = offset; s = this.buffer[0]; this.buffer[0] = s.substring(0, os) + str + s.substring(os)", this);
		return this;
	}

	public StringBuffer insert(int offset, char[] str)
	{
		return insert(offset, String.valueOf(str));
	}

	public StringBuffer insert(int offset, boolean b)
	{
		return insert(offset, String.valueOf(b));
	}

	public StringBuffer insert(int offset, char c)
	{
		return insert(offset, String.valueOf(c));
	}

	public java.lang.StringBuffer insert(int offset, int i)
	{
		return insert(offset, String.valueOf(i));
	}

	public java.lang.StringBuffer insert(int offset, long l)
	{
		return insert(offset, String.valueOf(l));
	}

	public java.lang.StringBuffer insert(int offset, float f)
	{
		return insert(offset, String.valueOf(f));
	}

	public java.lang.StringBuffer insert(int offset, double d)
	{
		return insert(offset, String.valueOf(d));
	}

	public java.lang.StringBuffer reverse()
	{
		consolidate();
		StringBuffer sb= new StringBuffer();
		int length= this.length();
		for (int i= length - 1; i >= 0; i--)
		{
			sb.append(charAt(i));
		}
		setLength(0);
		append(sb.toString());
		return this;
	}

	public String toString()
	{
		consolidate();
		return (String) ScriptHelper.eval("this.buffer[0]", this);
	}

	private void consolidate()
	{
		ScriptHelper.eval("var s = this.buffer.join(''); this.buffer = new Array(); this.buffer.push(s)", this);
	}

	public void append(String value, int start, int end)
	{//TODO revisar
		append(value.toCharArray(), start, end);
	}

}
