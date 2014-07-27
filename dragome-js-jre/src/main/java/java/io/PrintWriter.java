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
