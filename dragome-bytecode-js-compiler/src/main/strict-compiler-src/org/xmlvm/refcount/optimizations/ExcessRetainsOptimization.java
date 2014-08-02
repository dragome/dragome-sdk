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

import java.util.ArrayList;
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
 * This optimization will remove symmetric release retains in linear code
 * execution blocks. Said another way, in a piece of straight line code (no
 * branches), all redundant release/retaining will go away.
 */
public class ExcessRetainsOptimization implements RefCountOptimization
{

	class PairUseInfoIndex
	{
		public PairUseInfoIndex(InstructionUseInfo useInfo, int index)
		{
			this.useInfo= useInfo;
			this.index= index;
		}

		public InstructionUseInfo useInfo;
		public int index;
	}

	public ReturnValue Process(List<CodePath> allCodePaths, Map<Element, InstructionActions> beenTo, Element codeElement) throws ReferenceCountingException, DataConversionException
	{
		ReturnValue toRet= new ReturnValue();

		for (CodePath c : allCodePaths)
		{
			RegisterSet allRegs= new RegisterSet();
			for (OnePathInstructionRegisterContents curInst : c.path)
			{
				InstructionUseInfo useInfo= beenTo.get(curInst.instruction).useInfo;
				allRegs.orEq(useInfo.allWrites());
				allRegs.orEq(useInfo.usedReg());
			}

			// Look for a retain, no writes, then a release, and then get
			// rid of the symmetric retain / release

			for (int reg : allRegs)
			{
				List<PairUseInfoIndex> retains= new ArrayList<PairUseInfoIndex>();
				List<PairUseInfoIndex> releases= new ArrayList<PairUseInfoIndex>();

				int curIndex= 5;
				for (OnePathInstructionRegisterContents curInst : c.path)
				{
					InstructionActions act;
					act= beenTo.get(curInst.instruction);

					// changing the order of the next three ifs will screw
					// everything
					// up in unclear ways until you look at the output
					if (act.useInfo.willFree.has(reg))
					{
						releases.add(new PairUseInfoIndex(act.useInfo, curIndex));
						curIndex++;
					}

					RegisterSet allWrites= act.useInfo.allWrites();
					if (allWrites.has(reg))
					{
						eliminateExcessRetains(reg, retains, releases);
					}

					if (act.useInfo.requiresRetain.has(reg))
					{
						retains.add(new PairUseInfoIndex(act.useInfo, curIndex));
						curIndex++;
					}

				}
				eliminateExcessRetains(reg, retains, releases);
			}
		}
		return toRet;
	}

	private void eliminateExcessRetains(int reg, List<PairUseInfoIndex> retains, List<PairUseInfoIndex> releases)
	{
		// must free now, have new value being overwritten.
		while (retains.size() != 0 && releases.size() != 0)
		{
			// retains must come before releases, and not vice versa.
			if (releases.get(0).index < retains.get(0).index)
			{
				// can't use it because removing release then retain is
				// a great way to introduce a bug.
				retains.remove(0);
			}
			else
			{
				int retainIdx= retains.size() - 1;

				retains.get(retainIdx).useInfo.requiresRetain.clear(reg);
				releases.get(0).useInfo.willFree.clear(reg);
				retains.remove(retainIdx);
				releases.remove(0);
			}
		}
		retains.clear();
		releases.clear();
	}

}
