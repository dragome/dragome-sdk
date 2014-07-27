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

package org.xmlvm.main;

import static org.xmlvm.main.Targets.Affinity.SKELETON;
import static org.xmlvm.main.Targets.Affinity.TARGET;

import org.xmlvm.Log;

/**
 * All possible targets for the cross-compilation.
 */
public enum Targets
{

	NONE, XMLVM, DEXMLVM, JVM, CLR, DFA, CLASS, EXE, DEX, JS, JAVA, JSANDROID, C, POSIX, GENCWRAPPERS, PYTHON, OBJC, QOOXDOO, IPHONE, IPHONEC, IPHONECANDROID, VTABLE, WEBOS, CSHARP, IPHONETEMPLATE(SKELETON), ANDROIDTEMPLATE(SKELETON), ANDROIDMIGRATETEMPLATE(SKELETON), IPHONEUPDATETEMPLATE(SKELETON), ANDROIDUPDATETEMPLATE(SKELETON), WP7, WP7ANDROID, GENCSHARPWRAPPERS, IPHONEHYBRIDTEMPLATE(SKELETON), SDLANDROID;

	/**
	 * Returns a target with the given name.
	 * 
	 * @param target
	 *            the name of the target
	 * @return The target or <code>null</code>, if a target by that name cannot
	 *         be found.
	 */
	public static Targets getTarget(String target)
	{
		try
		{
			return Targets.valueOf(target.toUpperCase().replace("_", "").replace("-", "").replace(":", ""));
		}
		catch (IllegalArgumentException ex)
		{
			Log.error(Targets.class.getSimpleName(), "Could not find target by name: " + target);
		}
		return null;
	}

	/** The affinity of the target. */
	public final Affinity affinity;

	/**
	 * Creates a new target with the default affinity TARGET.
	 */
	private Targets()
	{
		affinity= TARGET;
	}

	/**
	 * Creates a new target with the given affinity.
	 */
	private Targets(Affinity af)
	{
		affinity= af;
	}

	/**
	 * Note if a target has to do with an actual output target or is a skeleton
	 */
	public enum Affinity
	{

		TARGET, SKELETON;
	}
}
