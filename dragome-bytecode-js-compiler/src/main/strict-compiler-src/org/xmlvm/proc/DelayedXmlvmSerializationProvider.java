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
package org.xmlvm.proc;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xmlvm.Log;
import org.xmlvm.proc.out.OutputFile.DelayedDataProvider;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * A delayed data provider that generates XML test from an XML document.
 */
public class DelayedXmlvmSerializationProvider implements DelayedDataProvider
{
	private static final String TAG= DelayedXmlvmSerializationProvider.class.getSimpleName();
	private final Document document;

	/**
	 * Initializes this provider with the given document.
	 * 
	 * @param document
	 *            The document to serialize.
	 */
	public DelayedXmlvmSerializationProvider(Document document)
	{
		this.document= document;
	}

	public UniversalFile getData()
	{
		String data= documentToString(document);
		try
		{
			return UniversalFileCreator.createFile("", data.getBytes("UTF-8"), System.currentTimeMillis());
		}
		catch (UnsupportedEncodingException e)
		{
			Log.error(TAG, e.getMessage());
		}
		return null;
	}

	/**
	 * Converts a {@link Document} into XML text.
	 */
	private String documentToString(Document document)
	{
		XMLOutputter outputter= new XMLOutputter(Format.getPrettyFormat());
		StringWriter writer= new StringWriter();
		try
		{
			outputter.output(document, writer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return "";
		}
		return writer.toString();
	}
}
