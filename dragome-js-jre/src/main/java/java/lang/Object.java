/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	protected Object clone() throws CloneNotSupportedException
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
		className= className.replace("_", ".");
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
//		return getClass().toString() + "@" + hashCode();
		return (String) ScriptHelper.eval("Object.prototype.toString.call(this)", this);
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
