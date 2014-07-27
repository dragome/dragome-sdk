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

package org.xmlvm.proc.out;

import java.io.FileInputStream;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.in.InputProcess;
import org.xmlvm.proc.in.InputProcess.XmlvmInputProcess;

/**
 * This {@link InputProcess} can read .xmlvm files as input.
 * 
 * TODO(Sascha): This produces {@link XmlvmResource}s with wrong name, type and
 * super type name.
 */
public class XmlvmToXmlvmProcess extends XmlvmProcessImpl
{

	public XmlvmToXmlvmProcess(Arguments arguments)
	{
		super(arguments);
		addSupportedInput(XmlvmInputProcess.class);
	}

	public boolean processPhase1(BundlePhase1 bundle)
	{
		for (OutputFile file : bundle.getOutputFiles())
		{
			Document doc= null;
			SAXBuilder builder= new SAXBuilder();
			FileInputStream in;
			try
			{
				in= new FileInputStream(file.getFullPath());
				doc= builder.build(in);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			XmlvmResource resource= new XmlvmResource(org.xmlvm.proc.XmlvmResource.Type.DEX, doc);
			bundle.addResource(resource);
		}
		return false;
	}

	public boolean processPhase2(BundlePhase2 bundle)
	{
		return true;
	}
}
