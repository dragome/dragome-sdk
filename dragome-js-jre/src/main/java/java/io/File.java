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

import java.net.URI;

public class File implements Serializable, Comparable<File>
{
	public File(String uri)
	{
		// TODO Auto-generated constructor stub
	}

	public File(URI uri)
	{
		// TODO Auto-generated constructor stub
	}

	public File(String home, String string)
	{
		// TODO Auto-generated constructor stub
	}

	public int compareTo(File another)
	{
		return 0;
	}

	public Object toURI()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public File[] listFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDirectory()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAbsolutePath()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean exists()
	{
		return false;
	}

	public boolean canRead()
    {
	    return false;
    }
}
