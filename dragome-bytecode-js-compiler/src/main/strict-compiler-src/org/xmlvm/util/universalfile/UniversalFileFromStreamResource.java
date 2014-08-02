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

import org.xmlvm.util.FileUtil;

import java.io.InputStream;

/**
 * A {@link UniversalFile} that is based on an {@link InputStream};
 */
public class UniversalFileFromStreamResource extends UniversalFile
{
	private final String absoluteName;
	private final InputStream stream;
	private final long lastModified;

	private byte[] cachedBytes= null;
	private String cachedString= null;

	UniversalFileFromStreamResource(String absoluteName, InputStream stream, long lastModified)
	{
		this.absoluteName= absoluteName;
		this.stream= stream;
		this.lastModified= lastModified;
	}

	public String getAbsolutePath()
	{
		return absoluteName;
	}

	public byte[] getFileAsBytes()
	{
		if (cachedBytes == null)
		{
			initContent();
		}
		return cachedBytes;
	}

	public String getFileAsString()
	{
		if (cachedString == null)
		{
			initContent();
		}
		return cachedString;
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
		return true;
	}

	public UniversalFile[] listFiles()
	{
		return new UniversalFile[0];
	}

	private void initContent()
	{
		cachedBytes= FileUtil.readBytesFromStream(stream);
		cachedString= new String(cachedBytes);
	}

	public long getLastModified()
	{
		return lastModified;
	}
}
