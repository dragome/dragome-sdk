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
/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Takes the input of an {@link InputStream} and writes it to the given output
 * stream. Useful if e.g. the stream comes from a process.
 */
public class InputReaderThread extends Thread
{
	private BufferedReader in;
	private PrintStream out;
	private String prefix;

	/**
	 * Instantiates a new InputReaderThread.
	 * 
	 * @param inputStream
	 *            the stream to read from
	 * @param outStream
	 *            the stream to write to
	 * @param linePrefix
	 *            a line prefix prepended to the output of each line to identify
	 *            the process
	 */
	public InputReaderThread(InputStream inputStream, PrintStream outStream, String linePrefix)
	{
		in= new BufferedReader(new InputStreamReader(inputStream));
		out= outStream;
		prefix= linePrefix;
	}

	public void run()
	{
		String line;
		try
		{
			while ((line= in.readLine()) != null)
			{
				out.println(prefix + " > " + line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
