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

import org.jdom.Element;

/**
 * Helper class for the code path. Every time any execution path hits an
 * instruction it stores away the object registers and non object registers that
 * are available to the instruction. In this way we can later decide whether
 * there is potential conflict if we decided to free a register
 */
public class OnePathInstructionRegisterContents
{
	public OnePathInstructionRegisterContents(Element elem, RegisterSet hasObj, RegisterSet noObj)
	{
		this.instruction= elem;
		this.hasObj= hasObj;
		this.noObj= noObj;
	}

	/**
	 * The instruction in question
	 */
	public Element instruction;
	/**
	 * The set of registers that hold objects on this particular execution path
	 */
	public RegisterSet hasObj;
	/**
	 * The set of registers that do not hold objects on this particular
	 * execution path
	 */
	public RegisterSet noObj;
}
