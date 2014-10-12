/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.model.interfaces;

/**
 *
 */
public class Severity implements Comparable<Severity>
{
	public static final Severity ERROR= new Severity(100);
	public static final Severity WARNING= new Severity(50);
	public static final Severity INFO= new Severity(0);

	private int level;

	protected Severity(int level)
	{
		this.level= level;
	}

	public int compareTo(Severity o)
	{
		// The default sort order is reversed so the first entry in a SortedSet
		// is the highest severity.
		return this.level == o.level ? 0 : this.level > o.level ? -1 : 1;
	}

	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final Severity severity= (Severity) o;

		if (level != severity.level)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		return level;
	}
}
