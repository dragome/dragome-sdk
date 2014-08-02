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
import org.xmlvm.refcount.InstructionProcessor;
import org.xmlvm.refcount.InstructionUseInfo;
import org.xmlvm.refcount.OnePathInstructionRegisterContents;
import org.xmlvm.refcount.ReferenceCountingException;
import org.xmlvm.refcount.RegisterSet;

public class RegisterSizeAndNullingOptimization implements RefCountOptimization
{

	/**
	 * Takes care of defining registers for argument variables and moving the
	 * argument into the register. Determines which registers we need to null
	 * out in the start of the function in order to preserve reference counting
	 * semantics.
	 * 
	 * Also (eventually) will pick register sizes to be 8 or 4 byte depending on
	 * usage in the future.
	 */
	@SuppressWarnings("unchecked")
	public ReturnValue Process(List<CodePath> allCodePaths, Map<Element, InstructionActions> beenTo, Element codeElement) throws ReferenceCountingException, DataConversionException
	{
		ReturnValue toRet= new ReturnValue();
		// We want to identify all registers used.
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

		// Emit decl for each register
		for (int regId : allRegs)
		{
			Element initElem= new Element(InstructionProcessor.cmd_define_register, InstructionProcessor.vm);
			initElem.setAttribute("vartype", InstructionProcessor.cmd_define_register_attr_register);
			initElem.setAttribute("num", regId + "");
			toRet.functionInit.add(initElem);
		}

		// Find all the var elements, and add moves for them
		int argIdx= 1;
		for (Element curElem : (List<Element>) codeElement.getChildren())
		{
			if (curElem.getName().equals("var"))
			{
				Element moveArg= new Element(InstructionProcessor.cmd_move_argument, InstructionProcessor.vm);
				moveArg.setAttribute("vx", curElem.getAttributeValue("register"));
				moveArg.setAttribute("vx-type", curElem.getAttributeValue("type"));

				String sourceName= curElem.getAttributeValue("name");
				if (sourceName == "this")
				{
					moveArg.setAttribute("sourceArg", "self");
				}
				else
				{
					moveArg.setAttribute("sourceArg", argIdx + "");
					argIdx++;
				}
				toRet.functionInit.add(moveArg);
				allRegs.remove(curElem.getAttribute("register").getIntValue());

			}
		}

		RegisterSet hasObj= RegisterSet.none();
		for (CodePath curPath : allCodePaths)
		{
			// collect non obj writes
			for (OnePathInstructionRegisterContents curInst : curPath.path)
			{
				InstructionUseInfo useInfo= beenTo.get(curInst.instruction).useInfo;
				hasObj.orEq(useInfo.writesObj());
			}
		}

		allRegs.andEq(hasObj);

		for (int regId : allRegs)
		{
			Element setNull= new Element(InstructionProcessor.cmd_set_null, InstructionProcessor.vm);
			setNull.setAttribute("num", regId + "");
			toRet.functionInit.add(setNull);
		}

		return toRet;
	}

}
