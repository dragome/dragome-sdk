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
 * This annotation can be applied to classes, in which case the resulting XMLVM
 * will only contain a skeleton of this class. This means, that the
 * implementation of public methods is removed. Private and synthetic fields and
 * methods are ignored. If you need e specific class in the wrapper implementation,
 * you should add it to the references property of this annotation.
 */
@Retention(RetentionPolicy.CLASS)
public @interface XMLVMSkeletonOnly
{
	/**
	 * Classes that are needed by the generated Skeleton
	 * @return
	 */
	Class<?>[] references() default {};
}
