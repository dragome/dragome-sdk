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

package org.xmlvm.util.comparators;

import java.util.Comparator;

import org.xmlvm.proc.XmlvmResource.XmlvmMethod;
import org.xmlvm.util.ObjectHierarchyHelper;

/**
 * Compare XmlvmMethods by name and parameters, but not by class name
 */
public class XmlvmMethodComparator implements Comparator<XmlvmMethod>
{

	public int compare(XmlvmMethod m1, XmlvmMethod m2)
	{
		String parameterString1= ObjectHierarchyHelper.getParameterString(m1.getParameterTypes());
		String parameterString2= ObjectHierarchyHelper.getParameterString(m2.getParameterTypes());
		String str1= m1.getName() + "__" + parameterString1;
		String str2= m2.getName() + "__" + parameterString2;
		return str1.compareTo(str2);
	}
}
