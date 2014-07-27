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

import java.io.IOException;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.in.InputProcess.ClassInputProcess;
import org.xmlvm.proc.in.file.ClassFile;

import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;

/**
 * This process takes Java Bytecode and turns it into the DEX format.
 */
public class DexOutputProcess extends XmlvmProcessImpl
{

	private static final String DEX_ENDING= ".dex";

	public DexOutputProcess(Arguments arguments)
	{
		super(arguments);
		addSupportedInput(ClassInputProcess.class);
		addSupportedInput(JavaByteCodeOutputProcess.class);
	}

	public boolean processPhase1(BundlePhase1 bundle)
	{
		return true;
	}

	public boolean processPhase2(BundlePhase2 bundle)
	{
		for (OutputFile preOutputFile : bundle.getOutputFiles())
		{
			OutputFile outputFile= generateDexFile(preOutputFile);
			if (outputFile == null)
			{
				return false;
			}
			bundle.removeOutputFile(preOutputFile);
			bundle.addOutputFile(outputFile);
		}
		return true;
	}

	private OutputFile generateDexFile(OutputFile classFile)
	{
		if (!classFile.getFullPath().startsWith(arguments.option_out()))
		{
			Log.error("DexOutputProcess: Something is wrong with the class output path.");
			return null;
		}
		String relativePath= classFile.getFullPath().substring(arguments.option_out().length() + 1);

		// Remove a starting slash or backslash.
		if (relativePath.startsWith("/") || relativePath.startsWith("\\"))
		{
			relativePath= relativePath.substring(1);
		}
		Log.debug("DExing:" + relativePath);

		CfOptions options= new CfOptions();
		options.strictNameCheck= false;
		ClassDefItem item= CfTranslator.translate(relativePath, classFile.getDataAsBytes(), options);
		DexFile dexFile= new DexFile();
		dexFile.add(item);
		try
		{
			byte[] rawDex= dexFile.toDex(null, false);
			OutputFile result= new OutputFile(rawDex);
			result.setLocation(classFile.getLocation());
			result.setFileName(classFile.getFileName().replace(ClassFile.CLASS_ENDING, DEX_ENDING));
			return result;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.error("Could not generate DEX file.");
		}
		return null;
	}
}
