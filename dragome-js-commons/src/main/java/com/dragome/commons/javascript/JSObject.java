package com.dragome.commons.javascript;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public class JSObject<T>
{
	public static Object getProperty(Object obj, String propertyName)
	{
		ScriptHelper.put("obj", obj, null);
		ScriptHelper.put("propertyName", propertyName, null);
		return ScriptHelper.eval("obj[propertyName]", null);
	}

	public static boolean containsKey(Object obj, String propertyName)
	{
		ScriptHelper.put("obj", obj, null);
		ScriptHelper.put("propertyName", propertyName, null);
		// Warning: Cannot use (obj[propertyName] != undefined) because
		// (null == undefined) evaluates to true.
		return ScriptHelper.evalBoolean("typeof(obj[propertyName]) != 'undefined'", null);
	}

	public JSObject()
	{
		ScriptHelper.evalNoResult("this.obj = new Object()", null);
	}

	public JSObject(Object obj)
	{
		ScriptHelper.put("obj", obj, null);
		ScriptHelper.evalNoResult("this.obj = obj", null);
	}

	public JSObject(String javascriptRef)
	{
		ScriptHelper.put("javascriptRef", javascriptRef, null);
		ScriptHelper.evalNoResult("this.obj = eval(javascriptRef)", null);
	}

	public boolean containsKey(String propertyName)
	{
		return containsKey(ScriptHelper.eval("this.obj", null), propertyName);
	}

	public T get(String key)
	{
		ScriptHelper.put("key", key, null);
		if (containsKey(ScriptHelper.eval("this.obj", null), key))
		{
			return (T) ScriptHelper.eval("this.obj[key]", null);
		}
		return null;
	}

	public void put(String key, T value)
	{
		ScriptHelper.put("key", key, null);
		ScriptHelper.put("value", value, null);
		ScriptHelper.evalNoResult("this.obj[key] = value", null);
	}

	public void remove(String key)
	{
		ScriptHelper.put("key", key, null);
		ScriptHelper.evalNoResult("delete this.obj[key]", null);
	}

	public String[] keys()
	{
		ScriptHelper.evalNoResult("var keys = new Array(); for (var e in this.obj) keys.push(e)", null);
		return (String[]) ScriptHelper.eval("keys", null);
	}
}
