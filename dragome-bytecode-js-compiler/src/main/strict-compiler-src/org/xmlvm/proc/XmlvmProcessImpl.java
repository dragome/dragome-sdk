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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.out.OptimizationOutputProcess;
import org.xmlvm.proc.out.XmlvmToXmlvmProcess;

/**
 * A collection of possible process IDs.
 */
enum XmlvmProcessId
{
	XMLVM_JVM("xmlvmjvm"), XMLVM_CLR("xmlvmclr"), XMLVM_CLR_DFA("xmlvmclrdfa"), CLASS("class"), EXE("exe"), JS("js"), JAVA("java"), OBJC("objc"), CPP("cpp"), PYTHON("python"), IPHONE("iphone"), QOOXDOO("qooxdoo");
	String name;

	private XmlvmProcessId(String name)
	{
		this.name= name;
	}

	public String toString()
	{
		return name;
	}
}

/**
 * Common implementation for all XMLVM Processes. Actual processes extend this
 * class.
 */
public abstract class XmlvmProcessImpl implements XmlvmProcess
{

	/** A tag used for logging. */
	private static final String TAG= XmlvmProcessImpl.class.getSimpleName();

	/** The list of process instances that get executed BEFORE this process. */
	private List<XmlvmProcess> preprocesses= new ArrayList<XmlvmProcess>();

	/** The list of process instances that get executed AFTER this process. */
	private Set<XmlvmProcess> postProcesses= new HashSet<XmlvmProcess>();

	/** This list contains all the supported input processes. */
	protected List<Class<XmlvmProcess>> supportedInputs= new ArrayList<Class<XmlvmProcess>>();

	protected Arguments arguments;

	protected boolean isProcessed= false;

	/** Whether this process is the target process. */
	protected boolean isTargetProcess= false;

	public XmlvmProcessImpl(Arguments arguments)
	{
		//        Log.debug("Instantiated: " + this.getClass().getName());
		this.arguments= arguments;
	}

	public boolean postProcess()
	{
		return true;
	}

	public List<Class<XmlvmProcess>> getSupportedInputs()
	{
		return supportedInputs;
	}

	@SuppressWarnings("unchecked")
	protected void addSupportedInput(Class<?> inputProcessClass)
	{
		try
		{
			supportedInputs.add((Class<XmlvmProcess>) inputProcessClass);
		}
		catch (ClassCastException ex)
		{
			ex.printStackTrace();
			Log.error("You tried to add a supported input that is not of the same type as the " + "generic type you've specified in the process.");
		}
	}

	/**
	 * Adds all processes that emit XMLVM as potential input processes.
	 */
	protected void addAllXmlvmEmittingProcessesAsInput()
	{
		if (!arguments.option_use_jvm())
		{
			addSupportedInput(OptimizationOutputProcess.class);
		}
		else
		{
			//            addSupportedInput(ClassToXmlvmProcess.class);
		}
		//        addSupportedInput(ExeToXmlvmProcess.class);
		addSupportedInput(XmlvmToXmlvmProcess.class);
	}

	public List<XmlvmProcess> createInputInstances()
	{
		List<XmlvmProcess> result= new ArrayList<XmlvmProcess>();
		for (Class<XmlvmProcess> supportedClass : getSupportedInputs())
		{
			try
			{
				XmlvmProcess process= (XmlvmProcess) supportedClass.getConstructor(Arguments.class).newInstance(arguments);
				result.add(process);
				// Add this process to the list of pre-processes.
				addPreprocess(process);
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				// This is ok. InputProcesses e.g. don't have such a
				// constructor, so we don't want to create an instance.
				Log.debug(TAG, "Not creating input instance of: " + supportedClass.getName());
			}
		}
		return result;
	}

	public boolean supportsAsInput(XmlvmProcess process)
	{
		for (Class<XmlvmProcess> supportedClass : getSupportedInputs())
		{
			if (isOfType(process.getClass(), supportedClass))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether this process is a sub-class of a direct or indirect class
	 * of the type given.
	 */
	public boolean isOfSuperType(Class<?> type)
	{
		return isOfType(this.getClass(), type);
	}

	/**
	 * Returns true, if 'b' is either a direct or indirect superclass of 'a'.
	 */
	private static boolean isOfType(Class<?> a, Class<?> b)
	{
		if (a == null)
		{
			return false;
		}
		if (a.equals(b))
		{
			return true;
		}
		else
		{
			return isOfType(a.getSuperclass(), b);
		}
	}

	public void addPreprocess(XmlvmProcess xmlvmProcess)
	{
		Log.debug("Adding preprocess " + xmlvmProcess.getClass().getName() + " to process " + this.getClass().getName());
		preprocesses.add(xmlvmProcess);
		xmlvmProcess.addPostProcess(this);
	}

	public void addPostProcess(XmlvmProcess xmlvmProcess)
	{
		postProcesses.add(xmlvmProcess);
	}

	public boolean forwardOrProcessPhase1(BundlePhase1 resources)
	{
		for (XmlvmProcess process : preprocesses)
		{
			if (!isProcessPreOfCorrectType(process))
			{
				return false;
			}

			if (process.isActive())
			{
				process.forwardOrProcessPhase1(resources);
			}
		}
		return processPhase1(resources);
	}

	public boolean forwardOrProcessPhase2(BundlePhase2 resources)
	{
		for (XmlvmProcess process : preprocesses)
		{
			if (!isProcessPreOfCorrectType(process))
			{
				return false;
			}

			if (process.isActive())
			{
				process.forwardOrProcessPhase2(resources);
			}
		}
		return processPhase2(resources);
	}

	public boolean postProcessPreProcesses()
	{
		for (XmlvmProcess process : preprocesses)
		{
			if (process.isActive())
			{
				if (!process.postProcessPreProcesses())
				{
					return false;
				}
			}
		}
		return postProcess();
	}

	public boolean isActive()
	{
		// A process is active only when at least one of his preprocesses is
		// active.
		for (XmlvmProcess preprocess : preprocesses)
		{
			if (preprocess.isActive())
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasCachedOutput(String inputResourceName, long lastModified)
	{
		return false;
	}

	public boolean isProcessingRequired(String inputResourceName, long lastModified)
	{
		// If we have it caches, we don't need to process.
		if (hasCachedOutput(inputResourceName, lastModified))
		{
			return false;
		}

		// If this process doesn't have it cached and there are not
		// post-processes, this process needs to process the resource.
		if (postProcesses.size() == 0)
		{
			return true;
		}

		// If this process doesn't have it cached and one of the post processes
		// doesn't, then this process needs to process.
		for (XmlvmProcess postProcess : postProcesses)
		{
			if (postProcess.isProcessingRequired(inputResourceName, lastModified))
			{
				return true;
			}
		}

		// If this process doesn't have the resource cached, and we have
		// post-processes and all post-processes have it cached, then we don't
		// have to process the resource.
		return false;
	}

	public void setIsTargetProcess(boolean isTargetProcess)
	{
		this.isTargetProcess= isTargetProcess;
	}

	private boolean isProcessPreOfCorrectType(XmlvmProcess process)
	{
		// We test whether the pre-process type is a sub-type of the given
		// generic type of the process.
		try
		{
			process.toString();
			return true;
		}
		catch (ClassCastException ex)
		{
			Log.error("A preprocess is not of the given generic type: " + process.getClass().getName());
			return false;
		}
	}
}
