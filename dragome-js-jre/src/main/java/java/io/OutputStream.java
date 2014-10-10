/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
