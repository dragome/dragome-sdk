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

package org.xmlvm.proc.lib;

import java.util.List;

import org.xmlvm.main.Targets;
import org.xmlvm.util.universalfile.UniversalFile;

/**
 * A library that can be used in addition to the fixed set of library. These are
 * usually specified at compile-time with a flag.
 */
public class AdditionalLibrary extends Library
{
	private final UniversalFile file;

	public AdditionalLibrary(UniversalFile file)
	{
		this.file= file;
	}

	public boolean isMonolithic()
	{
		return false;
	}

	protected UniversalFile[] getLibraryUncached()
	{
		return new UniversalFile[] { file };
	}

	protected List<Targets> includedTargets()
	{
		return null;
	}

	protected List<Targets> excludedTargets()
	{
		return null;
	}
}
