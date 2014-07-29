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
package java.lang;

import com.dragome.commons.javascript.ScriptHelper;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public class Object
{
	private static int hashCodeCount= 0;
	private int hashCode;

	public Object()
	{
	}

	public Object clone() throws CloneNotSupportedException
	{
		if (ScriptHelper.evalBoolean("this instanceof Array", this))
			return ScriptHelper.eval("dragomeJs.cloneArray(this)", this);
		else
			return null;
	}

	public boolean equals(Object obj)
	{
		return this == obj;
	}

	public Class getClass()
	{
		String className= (String) ScriptHelper.eval("this.classname", this);
		try
		{
			return Class.forName(className);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public int hashCode()
	{
		if (hashCode == 0)
			hashCode= ++Object.hashCodeCount;

		return hashCode;
	}

	public void notify()
	{
	}

	public void notifyAll()
	{
	}

	public String toString()
	{
		return getClass().toString() + "@" + hashCode();
	}

	public void wait()
	{
	}
	public void wait(long timeout)
	{
	}
	public void wait(long timeout, int nanos)
	{
	}

}
