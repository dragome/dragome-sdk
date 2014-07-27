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

import java.util.Collection;
import java.util.List;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.XmlvmResource.XmlvmMethod;

/**
 * Process to delete duplicate synthetic methods
 * 
 * javac will sometimes generate two methods that only differ in their return
 * type. This can happen e.g. with type erasures. This class will determine if
 * the given method is a duplicate.
 * 
 * A method is a duplicate if it is (1) synthetic, (2) a method with the same
 * name exists in the class, and (3) signatures only differ in their return
 * types.
 * 
 * This example will lead to a duplicate method clone in AbstractStruct as
 * defined above
 * 
 * <code>
 * package testpackage2;
 * 
 * public abstract class AbstractStruct {
 *   public AbstractStruct clone() {
 *       return null;
 *   }
 * }
 * 
 * public abstract class PrimitiveStruct<T> extends AbstractStruct {
 * 
 *  public abstract PrimitiveStruct<T> clone(); }
 * 
 *           </code>
 * 
 */
public class RemoveDuplicateMethodsOutputProcess extends XmlvmProcessImpl
{
	private final String TAG= RemoveDuplicateMethodsOutputProcess.class.getName();

	public RemoveDuplicateMethodsOutputProcess(Arguments arguments)
	{
		super(arguments);
		addSupportedInput(RecursiveResourceLoadingProcess.class);
	}

	public boolean processPhase1(BundlePhase1 bundle)
	{
		stripDuplicateMethods(bundle.getResources());
		return true;
	}

	public boolean processPhase2(BundlePhase2 bundle)
	{
		stripDuplicateMethods(bundle.getResources());
		return true;
	}

	private void stripDuplicateMethods(Collection<XmlvmResource> xmlvmResources)
	{
		for (XmlvmResource xmlvmResource : xmlvmResources)
		{
			if (xmlvmResource != null)
			{
				stripDuplicateMethods(xmlvmResource);
			}
		}
	}

	/**
	 * Go through all methods of the xmlvm resource and check if the method is
	 * synthetic and duplicated. If yes, delete the method
	 * 
	 * @param xmlvm
	 */
	private void stripDuplicateMethods(XmlvmResource xmlvm)
	{
		List<XmlvmMethod> methods= xmlvm.getMethods();
		for (XmlvmMethod search : methods)
		{
			if (search.isSynthetic())
			{
				for (XmlvmMethod each : methods)
				{
					if (each.getName().equals(search.getName()) && each.doesOverrideMethod(search) && each != search)
					{
						Log.debug(TAG, "Removing duplicate method " + search.getName() + " in " + xmlvm.getFullName());
						xmlvm.removeMethod(search);
						break;
					}
				}
			}
		}
	}
}
