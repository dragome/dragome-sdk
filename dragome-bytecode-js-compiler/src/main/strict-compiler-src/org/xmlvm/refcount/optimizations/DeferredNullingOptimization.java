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

package org.xmlvm.refcount.optimizations;

import java.util.List;
import java.util.Map;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.xmlvm.refcount.CodePath;
import org.xmlvm.refcount.InstructionActions;
import org.xmlvm.refcount.InstructionUseInfo;
import org.xmlvm.refcount.OnePathInstructionRegisterContents;
import org.xmlvm.refcount.ReferenceCountingException;
import org.xmlvm.refcount.RegisterSet;

/**
 * Used to defer nulling of registers until the last second. This allows us to
 * not null as often because writes of other values clear the need to null out a
 * register.
 */
public class DeferredNullingOptimization implements RefCountOptimization
{

	public ReturnValue Process(List<CodePath> allCodePaths, Map<Element, InstructionActions> beenTo, Element codeElement) throws ReferenceCountingException, DataConversionException
	{

		ReturnValue toRet= new ReturnValue();

		RegisterSet allRegs= new RegisterSet();
		for (CodePath curPath : allCodePaths)
		{
			for (OnePathInstructionRegisterContents curInst : curPath.path)
			{
				InstructionUseInfo useInfo= beenTo.get(curInst.instruction).useInfo;
				allRegs.orEq(useInfo.allWrites());
				allRegs.orEq(useInfo.usedReg());
			}
		}

		for (CodePath c : allCodePaths)
		{
			// We start with as early as possible nulls. Now we switch to
			// as late as possible to prevent excess nulling
			RegisterSet needsNull= RegisterSet.none();
			InstructionActions act= null;
			OnePathInstructionRegisterContents last= null;
			for (OnePathInstructionRegisterContents curInst : c.path)
			{
				last= curInst;
				act= beenTo.get(curInst.instruction);
				needsNull.orEq(act.useInfo.willNull);
				act.useInfo.willNull= RegisterSet.none();
				needsNull.andEqNot(act.useInfo.allWrites());
			}
			if (act != null)
			{
				if (last.instruction.getName().startsWith("return"))
				{
					act.useInfo.willNull= RegisterSet.none();
				}
				else
				{
					act.useInfo.willNull.orEq(needsNull);
				}
			}
		}
		return toRet;
	}

}
