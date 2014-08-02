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

package org.xmlvm.proc.out.build;

import org.xmlvm.Log;

public enum XcodeSkeleton
{

	/**
	 * Create a legacy iPhone 3.1 project
	 */
	IPHONE3("iphoneos3.1", "1"),
	/**
	 * Create an iPhone project
	 */
	IPHONE("iphoneos", "1"),
	/**
	 * Create an iPad project
	 */
	IPAD("iphoneos", "2"),
	/**
	 * Create an iPhone project
	 */
	IOS("iphoneos", "1,2");
	public final String root;
	public final String target;
	public final String architecture;
	public final String deploymenttarget;

	private XcodeSkeleton(String root, String target)
	{
		this.root= root;
		this.target= target;
		architecture= target.equals("2") ? "ARCHS_UNIVERSAL_IPHONE_OS" : "ARCHS_STANDARD_32_BIT";
		deploymenttarget= root.endsWith("3.1") ? "3.1" : "4.0";
	}

	public static XcodeSkeleton getTarget(String skeleton)
	{
		skeleton= skeleton.toUpperCase();
		try
		{
			return XcodeSkeleton.valueOf(skeleton);
		}
		catch (IllegalArgumentException ex)
		{
		}
		Log.warn("Unable to find xcode target " + skeleton + ". Using iphone instead.");
		return IPHONE;
	}
}
