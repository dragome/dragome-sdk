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

package org.xmlvm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Apply this annotation to a method to mark it as a delegate method.
 * 
 * In an interface, for which an implementation may become a delegate, denote on
 * the methods the information needed to invoke the respective native delegate
 * methods.
 */
@Retention(RetentionPolicy.CLASS)
public @interface XMLVMDelegateMethod
{
	/**
	 * @return the native name of the selector, NOT including parameters
	 */
	String selector();

	/**
	 * @return information about the native delegate method parameters
	 */
	Param[] params() default {};

	/**
	 * Information about a native delegate method parameter
	 */
	public @interface Param
	{
		/**
		 * @return the native instance type
		 */
		String type();

		/**
		 * @return the native name of the parameter, if any
		 */
		String name() default "";

		/**
		 * @return true if the parameter is the delegate implementation, else
		 *         false
		 */
		boolean isSource() default false;

		/**
		 * @return true if the parameter is not a pointer. This is not necessary
		 *         for primitive types.
		 */
		boolean isStruct() default false;

		boolean convert() default false;
	}
}
