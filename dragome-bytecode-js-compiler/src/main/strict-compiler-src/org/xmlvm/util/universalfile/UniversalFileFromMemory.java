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

package org.xmlvm.util.universalfile;

import java.io.UnsupportedEncodingException;

/**
 * A {@link UniversalFile} created from memory.
 */
public class UniversalFileFromMemory extends UniversalFile
{
	private final String absoluteName;
	private final byte[] data;
	private final long lastModified;

	UniversalFileFromMemory(String absoluteName, byte[] data)
	{
		this(absoluteName, data, System.currentTimeMillis());
	}

	UniversalFileFromMemory(String absoluteName, byte[] data, long lastModified)
	{
		this.absoluteName= absoluteName;
		this.data= data;
		this.lastModified= lastModified;
	}

	public String getAbsolutePath()
	{
		return absoluteName;
	}

	public boolean isDirectory()
	{
		return false;
	}

	public boolean isFile()
	{
		return true;
	}

	public boolean exists()
	{
		return data != null;
	}

	public UniversalFile[] listFiles()
	{
		return new UniversalFile[0];
	}

	public byte[] getFileAsBytes()
	{
		return data;
	}

	public String getFileAsString()
	{
		try
		{
			return new String(data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			return new String(data);
		}
	}

	public long getLastModified()
	{
		return lastModified;
	}
}
