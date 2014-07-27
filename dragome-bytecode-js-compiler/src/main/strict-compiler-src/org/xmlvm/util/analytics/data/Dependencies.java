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

package org.xmlvm.util.analytics.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Convenient class to make access to the dependency graph easier.
 */
public class Dependencies implements Serializable
{
	private static final long serialVersionUID= 1L;

	/**
	 * Contains the dependencies of one class.
	 */
	public static class ClassDeps implements Serializable
	{
		private static final long serialVersionUID= 1L;

		// Method -> MethodDeps
		private Map<String, MethodDeps> data= new HashMap<String, Dependencies.MethodDeps>();
		private String className;

		public ClassDeps(String className)
		{
			this.className= className;
		}

		/**
		 * Returns the name of the class, for which this instance holds the
		 * dependencies.
		 */
		public String getClassName()
		{
			return className;
		}

		/**
		 * Returns a set with all method names of this class that have
		 * dependencies.
		 */
		public Set<String> methodSet()
		{
			return data.keySet();
		}

		/**
		 * Use this to add new dependencies.
		 * 
		 * @param method
		 *            the method identifier, for which to add dependencies.
		 * @return A {@link MethodDeps} instance that you can use to add new
		 *         dependencies.
		 */
		public MethodDeps getMethodDeps(String method)
		{
			if (!data.containsKey(method))
			{
				MethodDeps deps= new MethodDeps();
				data.put(method, deps);
				return deps;
			}
			else
			{
				return data.get(method);
			}
		}

		/**
		 * Returns a set of all classes that this class depends on.
		 */
		public Set<String> getAllDeps()
		{
			Set<String> result= new HashSet<String>();
			for (MethodDeps methodDeps : data.values())
			{
				result.addAll(methodDeps.classSet());
			}
			return result;
		}
	}

	/**
	 * Contains all the dependencies from one method outwards.
	 */
	public static class MethodDeps implements Serializable
	{
		private static final long serialVersionUID= 1L;
		// Class -> Method
		private Map<String, Set<String>> data= new HashMap<String, Set<String>>();

		/**
		 * Adds a new dependency.
		 * 
		 * @param className
		 *            the name of the class to which the dependency points to
		 * @param method
		 *            the method identifier to which the dependency points to
		 */
		public void addDependency(String className, String method)
		{
			className= checkType(className);
			if (className == null)
			{
				return;
			}

			Set<String> methods;
			if (!data.containsKey(className))
			{
				methods= new HashSet<String>();
				data.put(className, methods);
			}
			else
			{
				methods= data.get(className);
			}
			methods.add(method);
		}

		public Set<String> classSet()
		{
			return data.keySet();
		}

		public Set<String> getMethods(String className)
		{
			return data.get(className);
		}
	}

	// ClassName -> ClassDeps
	private Map<String, ClassDeps> data= new HashMap<String, Dependencies.ClassDeps>();

	public Dependencies()
	{
	}

	/**
	 * Use this to add dependencies to a class.
	 * 
	 * @param className
	 *            the name of the class, for which to define an outward
	 *            dependency
	 * @return A {@link ClassDeps} instance that you can use to add the
	 *         dependencies
	 */
	public ClassDeps getDepsForClass(String className)
	{
		className= checkType(className);
		if (className == null)
		{
			return null;
		}

		if (data.containsKey(className))
		{
			return data.get(className);
		}
		else
		{
			ClassDeps deps= new ClassDeps(className);
			data.put(className, deps);
			return deps;
		}
	}

	public Set<String> keySet()
	{
		return data.keySet();
	}

	/**
	 * Returns the number of classes, for which we have dependencies.
	 */
	public int size()
	{
		return data.size();
	}

	public Set<String> getAllDepsForClass(String className)
	{
		return getDepsForClass(className).getAllDeps();
	}

	/**
	 * Makes sure the given type uses dots as package separator. Also return
	 * <code>null</code>, if this is not a usable type. E.g. basic types will
	 * return <code>null</code>
	 * <p>
	 * IMPORTANT: The way this code is written, classed without a package will
	 * also return <code>null</code>.
	 * 
	 * @param type
	 *            the rough type name
	 * @return the cleaned type name or <code>null</code>.
	 */
	private static String checkType(String type)
	{
		if (type.contains("."))
		{
			// Remove array type artifacts.
			int arrayDefStart= type.indexOf('[');
			if (arrayDefStart != -1)
			{
				type= type.substring(0, arrayDefStart);
			}
			return type.replace('/', '.');
		}
		else
		{
			return null;
		}
	}
}
