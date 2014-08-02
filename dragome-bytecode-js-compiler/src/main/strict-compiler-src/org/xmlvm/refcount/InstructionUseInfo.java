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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Attribute;

/**
 * This class contains information about how an instruction uses registers.
 */
public class InstructionUseInfo
{
	/**
	 * The instruction that we collect information about
	 */
	Element Instruction;

	/**
	 * On construction, we check for the normal vx vy vz register usage patterns
	 * present in DEX, adding them to our info store if they exist.
	 */
	public InstructionUseInfo(Element element) throws DataConversionException
	{
		this.Instruction= element;

		checkUsage(element.getAttribute("vx"), element.getAttribute("vx-type"));
		checkUsage(element.getAttribute("vy"), element.getAttribute("vy-type"));
		checkUsage(element.getAttribute("vz"), element.getAttribute("vz-type"));
		checkUsage(element.getAttribute("register"), element.getAttribute("type"));
		checkUsage(element.getAttribute("register"), element.getAttribute("class-type"));
		possibleWrites.add("vx");
	}

	/**
	 * Meaning that we are doing something like aput, which means we will need
	 * to potentially insert a release before we overwrite the pointer in the
	 * array.
	 */
	public Element putRelease= null;

	/**
	 * Given a register attribute and a type attribute, this method adds both to
	 * this classes list of register usage. It also determines whether the usage
	 * is object or not object depending on the type. In order for all of the
	 * ref counting code to work, all register usage everywhere must be
	 * registered with this function.
	 */
	public void checkUsage(Attribute vy, Attribute vyType) throws DataConversionException
	{
		InstructionUseInfo use= this;
		if (vy != null)
		{
			if (vyType != null)
			{
				if (InstructionProcessor.nonObjTypes.matcher(vyType.getValue()).matches())
				{
					use.typeIsObj.put(vy, Boolean.FALSE);
				}
				else
				{
					use.typeIsObj.put(vy, Boolean.TRUE);
				}
			}
		}
	}

	/**
	 * If this instruction is a write instruction, the name of the register that
	 * is being written into.
	 */
	public List<String> possibleWrites= new ArrayList<String>();

	/**
	 * Returns the registers which this instruction writes objects into
	 */
	public RegisterSet writesObj() throws DataConversionException
	{
		if (isWrite)
		{
			for (Attribute key : typeIsObj.keySet())
			{
				for (String s : possibleWrites)
				{
					if (key.getName().equals(s))
					{
						if (typeIsObj.get(key))
						{
							return RegisterSet.from(key.getIntValue());
						}
					}
				}
			}
		}
		return RegisterSet.none();

	}

	/**
	 * The list of registers this instruction writes with non objects
	 */
	public RegisterSet writesNonObj() throws DataConversionException
	{
		if (isWrite)
		{
			for (Attribute key : typeIsObj.keySet())
			{
				for (String s : possibleWrites)
				{
					if (key.getName().equals(s))
					{
						if (!typeIsObj.get(key))
						{
							return RegisterSet.from(key.getIntValue());
						}
					}
				}
			}
		}
		return RegisterSet.none();
	}

	/**
	 * The list of registers this instruction uses as objects
	 */
	public RegisterSet usesAsObj() throws DataConversionException
	{
		RegisterSet toRet= RegisterSet.none();
		if (isWrite)
		{
			for (Entry<Attribute, Boolean> a : typeIsObj.entrySet())
			{
				if (!possibleWrites.contains(a.getKey().getName()) && a.getValue())
				{
					// is object
					toRet.add(a.getKey().getIntValue());
				}
			}
		}
		else
		{
			for (Entry<Attribute, Boolean> a : typeIsObj.entrySet())
			{
				if (a.getValue())
				{
					// is object
					toRet.add(a.getKey().getIntValue());
				}
			}
		}
		return toRet;

	}

	/**
	 * The list of registers this instruction uses as non objects
	 */
	public RegisterSet usesAsNonObj() throws DataConversionException
	{
		RegisterSet toRet= RegisterSet.none();
		if (isWrite)
		{
			for (Entry<Attribute, Boolean> a : typeIsObj.entrySet())
			{
				if (!possibleWrites.contains(a.getKey().getName()) && !a.getValue())
				{
					// is object
					toRet.add(a.getKey().getIntValue());
				}
			}
		}
		else
		{
			for (Entry<Attribute, Boolean> a : typeIsObj.entrySet())
			{
				if (!a.getValue())
				{
					// is object
					toRet.add(a.getKey().getIntValue());
				}
			}
		}
		return toRet;

	}

	/**
	 * What registers should be freed after this instruction
	 */
	public RegisterSet willFree= RegisterSet.none();

	/**
	 * What registers should be nulled after any free
	 */
	public RegisterSet willNull= RegisterSet.none();

	/**
	 * The list of registers that require a retain after this instruction
	 * executes.
	 */
	public RegisterSet requiresRetain= RegisterSet.none();

	/**
	 * Whether or not we need to free the temp register after this instruction
	 */
	public boolean freeTmpAfter;
	/**
	 * Whether or not this instruction writes any registers.
	 */
	public boolean isWrite;

	/**
	 * Given a register name, is it referring to an object or a non object?
	 */
	public HashMap<Attribute, Boolean> typeIsObj= new HashMap<Attribute, Boolean>();

	/**
	 * The all registers (non object or object) used by this instruction
	 */
	public RegisterSet usedReg() throws DataConversionException
	{
		return usesAsNonObj().or(usesAsObj());
	}

	/*
	 * All registers written (non object or object) by this instruction
	 */
	public RegisterSet allWrites() throws DataConversionException
	{
		return writesObj().or(writesNonObj());
	}

	/**
	 * Complete dump of all registers used by this instruction
	 */
	public String toString()
	{

		StringBuilder toRet= new StringBuilder();
		try
		{
			toRet.append(Instruction.getName());
			if (isWrite)
			{
				if (!writesNonObj().isEmpty())
				{
					toRet.append(" Wi:" + writesNonObj());
				}
				else
				{
					toRet.append(" Wo:" + writesObj());
				}
			}
			if (!usesAsNonObj().isEmpty())
			{
				toRet.append(" Ri:" + usesAsNonObj());
			}
			if (!usesAsObj().isEmpty())
			{
				toRet.append(" Ro:" + usesAsObj());
			}
		}
		catch (DataConversionException ex)
		{
			toRet.append(ex.getMessage());
		}
		return toRet.toString();
	}
}
