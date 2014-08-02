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
 * This class helps determine how instructions are making use of objects, as
 * well as what sort of actions the processor should take.
 * 
 * This represents all aggregated object usage across all code paths.
 */
public class InstructionActions
{

	/**
	 * The registers that hold objects when the CPU executes this instruction.
	 */
	public RegisterSet getObjectRegs()
	{
		RegisterSet allEnteredHolding= RegisterSet.none();
		for (RegisterSet enteredHold : enteredHoldingObj)
		{
			allEnteredHolding.orEq(enteredHold);
		}
		return allEnteredHolding;
	}

	/**
	 * The registers that do not hold objects (hold primitives) when the CPUI
	 * executes this instruction.
	 */
	public RegisterSet getNonObjectRegs()
	{
		RegisterSet allEnteredNotHolding= RegisterSet.none();
		for (RegisterSet enteredNotHold : enteredNot)
		{
			allEnteredNotHolding.orEq(enteredNotHold);
		}
		return allEnteredNotHolding;
	}

	/**
	 * If along some code paths, we arrived with an object in a register and on
	 * another path a primitive in the register, this will tell us so.
	 */
	public RegisterSet getConflict()
	{
		return getObjectRegs().and(getNonObjectRegs());
	}

	/**
	 * Each time we hit this instruction through a code path, it adds info about
	 * the object/non object usage to these lists.
	 */
	public List<RegisterSet> enteredHoldingObj= new ArrayList<RegisterSet>();
	public List<RegisterSet> enteredNot= new ArrayList<RegisterSet>();

	/**
	 * The information produced by the InstructionProcessor.
	 */
	public InstructionUseInfo useInfo;

}
