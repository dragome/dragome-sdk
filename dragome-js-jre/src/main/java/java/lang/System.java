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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.script.ScriptEngine;

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
public final class System
{

	/**
	 * The "standard" error output stream. By default, the output is directed to the {@link ConsoleOutputStream}.
	 */
	public static PrintStream err;

	/**
	 * The "standard" output stream. By default, the output is directed to the {@link ConsoleOutputStream}.
	 */
	public static PrintStream out;

	public static HashMap<String, String> properties= new HashMap<String, String>();

	public static ScriptEngine scriptEngine;

	public static InputStream in;

	private System()
	{
	}

	/**
	 * Returns the current time in milliseconds.
	 */
	public static long currentTimeMillis()
	{
		return ScriptHelper.evalLong("new Date().getTime()", null);
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position,
	 * to the specified position of the destination array.
	 */
	public static void arraycopy(Object src, int srcPosition, Object dst, int dstPosition, int length)
	{
		Object[] srcArray= (Object[]) src;
		Object[] dstArray= (Object[]) dst;

		if (src == dst && srcPosition < dstPosition && srcPosition + length >= dstPosition)
		{
			for (int i= length - 1; i >= 0; i--)
				dstArray[dstPosition + i]= srcArray[srcPosition + i];
		}
		else
		{
			for (int i= 0; i < length; i++)
				dstArray[dstPosition + i]= srcArray[srcPosition + i];
		}
	}

	/**
	 * Gets the system property indicated by the specified key. On startup,
	 * system properties are set through the URL query parameters.
	 *
	 * @param key the name of the system property
	 */
	public static String getProperty(String key)
	{
		return properties.get(key);
	}

	/**
	 * Gets the system property indicated by the specified key. On startup,
	 * system properties are set through the URL query parameters.
	 *
	 * @param key the name of the system property
	 */
	public static String getProperty(String key, String def)
	{
		String propertyValue= properties.get(key);
		if (propertyValue == null)
			return def;
		return propertyValue;
	}

	/**
	 * Closes the window which is running this Java Virtual Machine.
	 *
	 * @param status ignored
	 */
	public static void exit(int status)
	{
		//Global.window.close();
	}

	/**
	 * Runs the garbage collector.
	 */
	public static void gc()
	{
	}

	public static int identityHashCode(Object object)
	{
		ScriptHelper.put("anObject", object, null);
		return ScriptHelper.evalInt("objectId(anObject)", null);
	}

	public static long nanoTime()
	{
		return ScriptHelper.evalLong("now()*1000*1000;", null);
	}

	public static String setProperty(String key, String value)
	{
		return properties.put(key, value);
	}
}
