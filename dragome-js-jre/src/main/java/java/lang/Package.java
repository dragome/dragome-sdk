/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.lang;

import java.net.URL;

public class Package
{
	public String getName()
	{
		return pkgName;
	}
	public String getSpecificationTitle()
	{
		return specTitle;
	}

	public String getSpecificationVersion()
	{
		return specVersion;
	}
	public String getSpecificationVendor()
	{
		return specVendor;
	}

	/**
	 * Return the title of this package.
	 * @return the title of the implementation, null is returned if it is not known.
	 */
	public String getImplementationTitle()
	{
		return implTitle;
	}

	/**
	 * Return the version of this implementation. It consists of any string
	 * assigned by the vendor of this implementation and does
	 * not have any particular syntax specified or expected by the Java
	 * runtime. It may be compared for equality with other
	 * package version strings used for this implementation
	 * by this vendor for this package.
	 * @return the version of the implementation, null is returned if it is not known.
	 */
	public String getImplementationVersion()
	{
		return implVersion;
	}

	/**
	 * Returns the name of the organization,
	 * vendor or company that provided this implementation.
	 * @return the vendor that implemented this package..
	 */
	public String getImplementationVendor()
	{
		return implVendor;
	}

	/**
	 * Returns true if this package is sealed.
	 *
	 * @return true if the package is sealed, false otherwise
	 */
	public boolean isSealed()
	{
		return sealBase != null;
	}

	/**
	 * Returns true if this package is sealed with respect to the specified
	 * code source url.
	 *
	 * @param url the code source url
	 * @return true if this package is sealed with respect to url
	 */
	public boolean isSealed(URL url)
	{
		return url.equals(sealBase);
	}

	/**
	 * Return the hash code computed from the package name.
	 * @return the hash code computed from the package name.
	 */
	public int hashCode()
	{
		return pkgName.hashCode();
	}

	/**
	 * Returns the string representation of this Package.
	 * Its value is the string "package " and the package name.
	 * If the package title is defined it is appended.
	 * If the package version is defined it is appended.
	 * @return the string representation of the package.
	 */
	public String toString()
	{
		String spec= specTitle;
		String ver= specVersion;
		if (spec != null && spec.length() > 0)
			spec= ", " + spec;
		else
			spec= "";
		if (ver != null && ver.length() > 0)
			ver= ", version " + ver;
		else
			ver= "";
		return "package " + pkgName + spec + ver;
	}

	/**
	 * Construct a package instance with the specified version
	 * information.
	 * @param pkgName the name of the package
	 * @param spectitle the title of the specification
	 * @param specversion the version of the specification
	 * @param specvendor the organization that maintains the specification
	 * @param impltitle the title of the implementation
	 * @param implversion the version of the implementation
	 * @param implvendor the organization that maintains the implementation
	 * @return a new package for containing the specified information.
	 */
	Package(String name, String spectitle, String specversion, String specvendor, String impltitle, String implversion, String implvendor, URL sealbase, ClassLoader loader)
	{
		pkgName= name;
		implTitle= impltitle;
		implVersion= implversion;
		implVendor= implvendor;
		specTitle= spectitle;
		specVersion= specversion;
		specVendor= specvendor;
		sealBase= sealbase;
	}

	public Package(String packageName)
	{
		pkgName= packageName;
	}

	private String pkgName;
	private String specTitle;
	private String specVersion;
	private String specVendor;
	private String implTitle;
	private String implVersion;
	private String implVendor;
	private URL sealBase;
}
