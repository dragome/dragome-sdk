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

import java.util.BitSet;
import java.util.Iterator;

/**
 * A class for representing a set of registers. The set is represented via a
 * bitmap where each bit corresponds to one register.
 */
public class RegisterSet implements java.lang.Iterable<Integer>
{

	private BitSet map= new BitSet();

	/**
	 * |= operator
	 */
	public void orEq(RegisterSet other)
	{
		this.map.or(other.map);
	}

	/**
	 * & operator
	 */
	public RegisterSet and(RegisterSet other)
	{
		RegisterSet toRet= RegisterSet.none();
		toRet.orEq(this);
		toRet.andEq(other);
		return toRet;
	}

	/**
	 * &= operator
	 */
	public void andEq(RegisterSet other)
	{
		this.map.and(other.map);
	}

	/**
	 * | operator
	 */
	public RegisterSet or(RegisterSet other)
	{
		RegisterSet toRet= RegisterSet.none();
		toRet.orEq(this);
		toRet.orEq(other);
		return toRet;
	}

	public RegisterSet clone()
	{
		RegisterSet toRet= new RegisterSet();
		toRet.map= (BitSet) this.map.clone();
		return toRet;
	}

	/**
	 * &= ~val
	 */
	public void andEqNot(RegisterSet other)
	{

		RegisterSet tmp= other.clone();
		int lenForOp= Math.max(this.map.length(), other.map.length());
		tmp.map.flip(0, lenForOp);
		this.andEq(tmp);

	}

	/**
	 * &~
	 */
	public RegisterSet andNot(RegisterSet other)
	{
		RegisterSet toRet= this.clone();
		toRet.andEqNot(other);
		return toRet;
	}

	/**
	 * Adds register i to the map.
	 */
	public void add(int i)
	{
		this.map.set(i);
	}

	/**
	 * remove i from the reg set
	 */
	public void clear(int i)
	{
		this.map.clear(i);
	}

	/**
	 * Removes register i from the map
	 */
	public void remove(int i)
	{
		this.map.clear(i);
	}

	/**
	 * Do we have any registers set?
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * Create a regmap given a register index.
	 */
	public static RegisterSet from(int i)
	{
		RegisterSet toRet= none();
		toRet.map.set(i);
		return toRet;
	}

	/**
	 * Produce a string like R1 R2 R5 etc...
	 */
	public String toString()
	{
		StringBuilder toRet= new StringBuilder();
		for (int x : this)
		{
			toRet.append("R" + x + " ");
		}
		return toRet.toString();
	}

	/**
	 * return an empty register map
	 */
	public static RegisterSet none()
	{
		return new RegisterSet();
	}

	public boolean equals(Object regs)
	{
		if (!(regs instanceof RegisterSet))
			return false;
		return this.map.equals(((RegisterSet) regs).map);
	}

	class BitSetIterator implements Iterator<Integer>
	{
		public BitSetIterator(RegisterSet toIterate)
		{
			this.toIterate= toIterate;
		}

		RegisterSet toIterate;
		int cur= 0;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */

		public boolean hasNext()
		{
			int tmp= cur;
			while (tmp < toIterate.map.length())
			{
				if (toIterate.map.get(tmp))
				{
					return true;
				}
				tmp++;
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */

		public Integer next()
		{
			while (cur < toIterate.map.length())
			{
				cur++;
				if (toIterate.map.get(cur - 1))
				{
					return cur - 1;
				}
			}
			throw new RuntimeException("Impossible");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */

		public void remove()
		{
			throw new RuntimeException("Impossible");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */

	public Iterator<Integer> iterator()
	{
		return new BitSetIterator(this);
	}

	/**
	 * Contains this register?
	 */
	public boolean has(int oneReg)
	{
		return this.map.get(oneReg);
	}
}
