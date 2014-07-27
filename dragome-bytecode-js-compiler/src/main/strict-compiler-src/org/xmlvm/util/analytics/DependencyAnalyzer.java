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

package org.xmlvm.util.analytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is able to analyze dependencies between classes.
 */
public class DependencyAnalyzer
{
	final static Map<String, Map<String, Set<String>>> sourceDataRaw= new HashMap<String, Map<String, Set<String>>>();
	final static Map<String, List<String>> sourceData= new HashMap<String, List<String>>();
	final static Map<String, Set<String>> result= new HashMap<String, Set<String>>();
	final static Map<String, Integer> outgoingIndex= new HashMap<String, Integer>();

	/**
	 * This main just executes a little test.
	 */
	public static void main(String[] args)
	{
		// addTestData("java.lang.String", "java.lang.Object");
		// addTestData("java.lang.Foo1", "java.lang.Foo2", "java.lang.Foo3");

		addTestData("A", "K1");
		addTestData("K1", "Foo", "K2");
		addTestData("K2", "K3");
		addTestData("K3", "K4");
		addTestData("K4", "E", "K1");
		addTestData("E", "E1", "E2", "E3");
		prepareSourceData();

		for (String className : sourceData.keySet())
		{
			System.out.println(">> " + className);
			calculate(className, new HashSet<String>());
		}
		printResult();
	}

	public static void calculate(String className, Set<String> addDepsTo)
	{
		Set<String> origAddDepsToSet= new HashSet<String>(addDepsTo);
		List<String> dependencies= sourceData.get(className);

		if (dependencies == null || dependencies.size() == 0)
		{
			return;
		}
		// Add direct dependencies.
		addTo(className, dependencies);

		// Add sub-dependencies.
		addDepsTo.add(className);
		int nextIndex;
		while ((nextIndex= getNextOutgoingIndexFor(className)) < dependencies.size())
		{
			calculate(dependencies.get(nextIndex), addDepsTo);
		}
		for (String addTo : origAddDepsToSet)
		{
			addTo(addTo, result.get(className));
		}
	}

	public static int getNextOutgoingIndexFor(String className)
	{
		if (!outgoingIndex.containsKey(className))
		{
			outgoingIndex.put(className, -1);
		}
		outgoingIndex.put(className, outgoingIndex.get(className) + 1);
		return outgoingIndex.get(className);
	}

	/**
	 * Adds the dependencies to the result set of dependencies for the given
	 * class.
	 */
	public static void addTo(String className, Collection<String> deps)
	{
		if (!result.containsKey(className))
		{
			result.put(className, new HashSet<String>());
		}
		result.get(className).addAll(deps);
	}

	/**
	 * Takes the raw source data, and produces a map that contains a list of
	 * dependencies for each class. It is, however, guaranteed that the list has
	 * no duplicates.
	 */
	private static void prepareSourceData()
	{
		for (String className : sourceDataRaw.keySet())
		{
			Set<String> deps= new HashSet<String>();
			if (sourceDataRaw.containsKey(className))
			{
				Map<String, Set<String>> methods= sourceDataRaw.get(className);
				for (String key : methods.keySet())
				{
					deps.addAll(methods.get(key));
				}
			}

			// Turn set into List so we can use an index to point to an outgoing
			// link.
			List<String> depsList= new ArrayList<String>(deps);
			sourceData.put(className, depsList);
		}
	}

	private static void printResult()
	{
		for (String className : result.keySet())
		{
			System.out.println("Class: " + className);
			for (String dependency : result.get(className))
			{
				System.out.println("  --> " + dependency);
			}
		}
	}

	private static void addTestData(String className, String... deps)
	{
		Map<String, Set<String>> methodMap= new HashMap<String, Set<String>>();
		Set<String> depsSet= new HashSet<String>();
		depsSet.addAll(Arrays.asList(deps));
		methodMap.put("", depsSet);
		sourceDataRaw.put(className, methodMap);
	}
}
