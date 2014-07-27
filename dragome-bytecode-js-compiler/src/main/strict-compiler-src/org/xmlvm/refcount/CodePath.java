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

package org.xmlvm.refcount;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents part of an execution path through a particular
 * function. It will be used in the future for performing more sophisticated
 * optimizations than we are doing now.
 */
public class CodePath
{

	public CodePath(int id, CodePath parent)
	{
		this.id= id;
		this.parent= parent;
	}

	/**
	 * Every code path gets a unique id.
	 */
	public int id;
	/**
	 * A pointer to its parent
	 */
	public CodePath parent;
	/**
	 * And the instructions that are in it.
	 */
	public List<OnePathInstructionRegisterContents> path= new ArrayList<OnePathInstructionRegisterContents>();
	/**
	 * as well as any sub paths (created by things like branches)
	 */
	public List<CodePath> subPaths= new ArrayList<CodePath>();

	/**
	 * For future use.
	 */
	public RegisterSet wantsObject= RegisterSet.none();
	public RegisterSet givesObject= RegisterSet.none();
	public RegisterSet shouldFree= RegisterSet.none();

	private void toStringRec(int depth, StringBuilder toRet)
	{
		char[] tabs= new char[depth];
		for (int x= 0; x < tabs.length; x++)
		{
			tabs[x]= '\t';
		}
		String tabsString= new String(tabs);
		toRet.append(tabsString + "ID " + id + " subpaths " + subPaths.size() + "\n");
		for (OnePathInstructionRegisterContents inst : path)
		{
			toRet.append(tabsString + inst.instruction.getName() + "\n");
		}
		for (CodePath p : this.subPaths)
		{
			p.toStringRec(depth + 1, toRet);
		}
	}

	public String toString()
	{
		StringBuilder toRet= new StringBuilder();
		toStringRec(0, toRet);
		return toRet.toString();
	}
}
