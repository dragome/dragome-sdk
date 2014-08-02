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

package org.xmlvm.util.analytics.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds the hierarchical dependencies between types, such as an inheritance
 * hierarchy.
 */
public class TypeHierarchy
{
	/**
	 * Super-type -> direct sub-types.
	 */
	private Map<String, Set<String>> directSubTypes= new HashMap<String, Set<String>>();

	/**
	 * Sub-type -> direct super-type. Reverse mapping of directSubTypes.
	 */
	private Map<String, Set<String>> directSuperTypes= new HashMap<String, Set<String>>();

	/**
	 * Super-type -> direct and indirect sub-types.
	 */
	private Map<String, Set<String>> subTypes= new HashMap<String, Set<String>>();

	/**
	 * Sub-type -> direct and indirect super-types.
	 */
	private Map<String, Set<String>> superTypes= new HashMap<String, Set<String>>();

	/**
	 * This is set to true, whenever the data is changed, so that the maps
	 * containing direct and indirect types is updated before being accessed.
	 */
	private boolean refreshNeeded= true;

	/**
	 * Adds subType as a direct sub-type of superType.
	 */
	public void addDirectSubType(String subType, String superType)
	{
		refreshNeeded= true;

		// Add direct sub-type.
		if (!directSubTypes.containsKey(superType))
		{
			directSubTypes.put(superType, new HashSet<String>());
		}
		directSubTypes.get(superType).add(subType);

		// Add direct super-type reverse mapping.
		if (!directSuperTypes.containsKey(subType))
		{
			directSuperTypes.put(subType, new HashSet<String>());
		}
		directSuperTypes.get(subType).add(superType);
	}

	/**
	 * Returns a set of all direct sub-types of the given super-type.
	 */
	public Set<String> getDirectSubTypes(String superType)
	{
		return directSubTypes.get(superType);
	}

	/**
	 * Returns whether subType is a sub-type of superType. This may be a direct
	 * or indirect sub-type dependency.
	 */
	public boolean isSubTypeOf(String subType, String superType)
	{
		refreshIfNeeded();
		if (!subTypes.containsKey(superType))
		{
			return false;
		}
		return subTypes.get(superType).contains(subType);
	}

	/**
	 * Returns whether superType is a super-type of subType. This may be a
	 * direct or indirect sub-type dependency.
	 */
	public boolean isSuperTypeOf(String superType, String subType)
	{
		refreshIfNeeded();
		if (!superTypes.containsKey(subType))
		{
			return false;
		}
		return superTypes.get(subType).contains(superType);
	}

	/**
	 * Returns all (direct and indirect) super-types of the given sub-type.
	 */
	private Set<String> getAllSuperTypes(String subType)
	{
		Set<String> result= new HashSet<String>();
		if (!directSuperTypes.containsKey(subType))
		{
			return result;
		}

		for (String superType : directSuperTypes.get(subType))
		{
			result.add(superType);
			result.addAll(getAllSuperTypes(superType));
		}
		return result;
	}

	/**
	 * Returns all (direct and indirect) sub-types of the given super-type.
	 */
	private Set<String> getAllSubTypes(String superType)
	{
		Set<String> result= new HashSet<String>();
		if (!directSubTypes.containsKey(superType))
		{
			return result;
		}

		for (String subType : directSubTypes.get(superType))
		{
			result.add(subType);
			result.addAll(getAllSubTypes(subType));
		}
		return result;
	}

	/**
	 * Based on the direct dependencies added to this TypeHierachy so far, the
	 * map of all direct and indirect sub-types is calculated.
	 */
	private void calculateSubTypeMap()
	{
		subTypes.clear();
		for (String subType : directSuperTypes.keySet())
		{
			for (String superType : directSuperTypes.get(subType))
			{

				// First add sub-type to direct super-type.
				if (!subTypes.containsKey(superType))
				{
					subTypes.put(superType, new HashSet<String>());
				}
				subTypes.get(superType).add(subType);

				// Add subType to all super-types of superType.
				for (String superSuperType : getAllSuperTypes(superType))
				{
					if (!subTypes.containsKey(superSuperType))
					{
						subTypes.put(superSuperType, new HashSet<String>());
					}
					subTypes.get(superSuperType).add(subType);
				}
			}
		}
	}

	/**
	 * Based on the direct dependencies added to this TypeHierachy so far, the
	 * map of all direct and indirect super-types is calculated.
	 */
	private void calculateSuperTypeMap()
	{
		superTypes.clear();
		for (String superType : directSubTypes.keySet())
		{
			for (String subType : directSubTypes.get(superType))
			{

				// First add super-type to direct sub-type.
				if (!superTypes.containsKey(subType))
				{
					superTypes.put(subType, new HashSet<String>());
				}
				superTypes.get(subType).add(superType);

				// Add superType to all sub-types of subType.
				for (String subSubType : getAllSubTypes(subType))
				{
					if (!superTypes.containsKey(subSubType))
					{
						superTypes.put(subSubType, new HashSet<String>());
					}
					superTypes.get(subSubType).add(superType);
				}
			}
		}
	}

	/**
	 * Refreshes the direct and indirect maps.
	 */
	private void refreshIfNeeded()
	{
		if (refreshNeeded)
		{
			calculateSubTypeMap();
			calculateSuperTypeMap();
		}
		refreshNeeded= false;
	}

	public String toString()
	{
		refreshIfNeeded();
		StringBuilder output= new StringBuilder();
		output.append("*****************************************\n");
		output.append("Super-Type -> direct & indirect sub-types\n");
		output.append("*****************************************\n");
		for (String superType : subTypes.keySet())
		{
			output.append("--> " + superType + "\n");
			for (String subType : subTypes.get(superType))
			{
				output.append("   |- " + subType + "\n");
			}
		}
		output.append("*****************************************\n");
		output.append("Sub-Type -> direct & indirect super-types\n");
		output.append("*****************************************\n");
		for (String subType : superTypes.keySet())
		{
			output.append("--> " + subType + "\n");
			for (String superType : superTypes.get(subType))
			{
				output.append("   |- " + superType + "\n");
			}
		}
		return output.toString();
	}
}
