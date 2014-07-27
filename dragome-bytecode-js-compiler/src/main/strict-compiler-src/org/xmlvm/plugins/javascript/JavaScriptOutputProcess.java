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

package org.xmlvm.plugins.javascript;

import java.util.HashMap;
import java.util.Map;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.out.OutputFile;
import org.xmlvm.proc.out.RecursiveResourceLoadingProcess;

/**
 * This process takes XMLVM and turns it into JavaScript.
 */
public class JavaScriptOutputProcess extends XmlvmProcessImpl
{

	private class JavaScriptTranslationThread extends Thread
	{
		private final XmlvmResource[] allResources;
		private final int start;
		private final int end;
		private final BundlePhase2 resources;

		public JavaScriptTranslationThread(XmlvmResource[] allResources, int start, int end, BundlePhase2 resources)
		{
			this.allResources= allResources;
			this.start= start;
			this.end= end;
			this.resources= resources;
		}

		public void run()
		{
			for (int i= start; i <= end; ++i)
			{
				XmlvmResource resource= allResources[i];
				if (resource == null)
				{
					continue;
				}
				Log.debug("JavaScriptOutputProcess: Processing " + resource.getName());
				OutputFile file= generateJavaScript(resource);
				file.setLocation(arguments.option_out());
				String packageName= resource.getPackageName().replace('.', '_');
				String resourceName= resource.getName();
				Log.debug("RESOURCE NAME: " + resourceName);

				String fileName= resourceName + JS_EXTENSION;
				if (!packageName.isEmpty())
				{
					fileName= packageName + '_' + fileName;
				}
				file.setFileName(fileName);
				resources.addOutputFile(file);
			}
		}
	}

	private static final String JS_EXTENSION= ".js";
	private static final String TAG= JavaScriptOutputProcess.class.getSimpleName();

	public JavaScriptOutputProcess(Arguments arguments)
	{
		super(arguments);
		addSupportedInput(RecursiveResourceLoadingProcess.class);
	}

	public boolean processPhase1(BundlePhase1 bundle)
	{
		return true;
	}

	public boolean processPhase2(BundlePhase2 bundle)
	{
		Map<String, XmlvmResource> mappedResources= new HashMap<String, XmlvmResource>();
		for (XmlvmResource resource : bundle.getResources())
		{
			mappedResources.put(resource.getFullName(), resource);
		}

		long startTime= System.currentTimeMillis();

		XmlvmResource[] allResources= mappedResources.values().toArray(new XmlvmResource[0]);
		int threadCount= Runtime.getRuntime().availableProcessors();
		int itemsPerThread= (int) Math.ceil(allResources.length / (float) threadCount);
		Log.debug(TAG, "Threads: " + threadCount);
		Log.debug(TAG, "Items per thread: " + itemsPerThread);
		JavaScriptTranslationThread[] threads= new JavaScriptTranslationThread[threadCount];

		// Divide work and start the threads.
		for (int i= 0; i < threadCount; ++i)
		{
			int start= i * itemsPerThread;
			int end= Math.min(start + itemsPerThread - 1, allResources.length - 1);
			threads[i]= new JavaScriptTranslationThread(allResources, start, end, bundle);
			threads[i].start();
		}

		// Wait for threads to finish.
		for (int i= 0; i < threadCount; ++i)
		{
			try
			{
				threads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return false;
			}
		}

		long endTime= System.currentTimeMillis();
		Log.debug(TAG, "JS Processing took: " + (endTime - startTime) + " ms.");
		return true;
	}

	protected OutputFile generateJavaScript(XmlvmResource xmlvm)
	{
		return XsltRunner.runXSLT("xmlvm2js.xsl", xmlvm.getXmlvmDocument());
	}
}
