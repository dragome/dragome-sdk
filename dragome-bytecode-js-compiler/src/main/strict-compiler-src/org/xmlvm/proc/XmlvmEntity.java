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

import java.util.ArrayList;
import java.util.List;

import org.xmlvm.proc.XmlvmClass.XmlvmField;
import org.xmlvm.proc.XmlvmClass.XmlvmMethod;

/**
 * The abstract base class for all XMLVM entities
 * <p>
 * <b>Known subclasses:</b> {@link XmlvmClass}, {@link XmlvmField},
 * {@link XmlvmMethod}.
 * 
 */
public abstract class XmlvmEntity
{
	protected String name;
	protected List<AccessFlag> accessFlags= new ArrayList<AccessFlag>();

	/**
	 * Get the name of this entity.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the name of this entity.
	 */
	public void setName(String name)
	{
		this.name= name;
	}

	/**
	 * Sets the flags for this entity.
	 * 
	 * @param modifiers
	 *            An array containing the modifier names such as "public",
	 *            "static" etc.
	 */
	public void setFlags(String[] modifiers)
	{
		for (String modifier : modifiers)
		{
			accessFlags.add(AccessFlag.valueOf(modifier.toUpperCase()));
		}
	}

	/**
	 * Returns the flags of this class.
	 */
	public AccessFlag[] getFlags()
	{
		return accessFlags.toArray(new AccessFlag[0]);
	}

	/**
	 * Returns {@code true}, if this entity has the given flag, {@code false}
	 * otherwise.
	 */
	public boolean hasFlag(AccessFlag flag)
	{
		return accessFlags.contains(flag);
	}

	/**
	 * Defines access flags for {@link XmlvmEntity} instances.
	 */
	public static enum AccessFlag
	{
		PUBLIC("isPublic"), PRIVATE("isPrivate"), PROTECTED("isProtected"), STATIC("isStatic"), FINAL("isFinal"), SYNCHRONIZED("isSynchronized"), VOLATILE("isVolatile"), TRANSIENT("isTransient"), NATIVE("isNative"), INTERFACE("isInterface"), ABSTRACT("isAbstract"), STRICTFP("isStrictfp");

		private final String flagName;

		private AccessFlag(String flagName)
		{
			this.flagName= flagName;
		}

		public String flagName()
		{
			return flagName;
		}
	}
}
