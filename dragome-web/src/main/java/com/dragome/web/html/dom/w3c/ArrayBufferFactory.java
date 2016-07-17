package com.dragome.web.html.dom.w3c;

import org.w3c.dom.typedarray.ArrayBuffer;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.javascript.ScriptHelper;

public class ArrayBufferFactory
{
	@DelegateCode(ignore= true)
	public static ArrayBuffer createArrayBuffer(int length)
	{
		ScriptHelper.put("length", length, null);
		ArrayBuffer node= ScriptHelper.evalCasting("new ArrayBuffer(length)", ArrayBuffer.class, null);
		return node;
	}
}
