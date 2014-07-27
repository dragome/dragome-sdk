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

package org.xmlvm.util.comparators;

import java.util.Comparator;

/**
 * Sort Java classes first by priority packages: "java.*", "org.apache.*" and
 * "org.xmlvm.*", then by full name. This is often done to sort core/priority
 * classes first, granting them lower indices in a sorted list. This is more
 * ideal because core libraries are less likely to change. So if a change is
 * made to a class that is not in the core library, then the beginning of the
 * list, which contains the core library, will remain unchanged.
 */
public class ClassNameComparator implements Comparator<String>
{
	private static String[] PRIORITY_PREFIXES= new String[] { "java.", "org.apache.", "org.xmlvm." };

	/**
	 * Compare two class names including their package name with certain
	 * packages being given priority. This is not an alphabetic comparator.
	 * 
	 * @param fullClassName1
	 *            the first full class name
	 * @param fullClassName2
	 *            the second full class name
	 * @return a negative integer, zero, or a positive integer as the first
	 *         class name has a higher, equal, or lower priority than the second
	 *         class name
	 */

	public int compare(String fullClassName1, String fullClassName2)
	{
		int result= 0;

		int c1= getFirstCompareValue(fullClassName1);
		int c2= getFirstCompareValue(fullClassName2);
		result= c1 - c2;
		if (result == 0)
		{
			result= fullClassName1.compareTo(fullClassName2);
		}
		return result;
	}

	/**
	 * Determine priority of the name by comparing it to special prefixes
	 * @param fullName the full name of the class including package name
	 * @return a priority number based on priority prefixes
	 */
	private static int getFirstCompareValue(String fullName)
	{
		int c= Integer.MAX_VALUE;
		int i= 0;
		while (i < PRIORITY_PREFIXES.length && c > i)
		{
			if (fullName.startsWith(PRIORITY_PREFIXES[i++]))
			{
				c= i;
			}
		}
		return c;
	}
}
