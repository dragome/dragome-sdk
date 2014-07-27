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

}
