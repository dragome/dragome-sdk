/* Copyright (c) 2008, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.io;

public class StringWriter extends Writer
{
	private final StringBuffer buf;

	public StringWriter()
	{
		buf= new StringBuffer();
	}

	public StringWriter(int initialCapacity)
	{
		buf= new StringBuffer(initialCapacity);
	}

	public StringBuffer getBuffer()
	{
		return buf;
	}

	public void write(char[] b, int offset, int length) throws IOException
	{
		buf.append(b, offset, length);
	}

	public String toString()
	{
		return buf.toString();
	}

	public void flush() throws IOException
	{
	}

	public void close() throws IOException
	{
	}

	public void write(String str) throws IOException
	{
		buf.append(str);
	}

	public void write(String value, int start, int i) throws IOException
	{
		buf.append(value, start, i);
	}

	public Writer append(CharSequence csq) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Writer append(CharSequence csq, int start, int end) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Writer append(char c) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
