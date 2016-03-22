package com.dragome.web.html.dom.w3c;

import org.w3c.dom.typedarray.ArrayBuffer;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsDelegateFactory;

public class ArrayBufferFactory
{
	@DelegateCode(ignore= true)
	public static ArrayBuffer createArrayBuffer(int length)
	{
		ScriptHelper.put("lenght", length, null);
		Object instance= ScriptHelper.eval("new ArrayBuffer(length);", null);
		ArrayBuffer node= JsDelegateFactory.createFrom(instance, ArrayBuffer.class);
		return node;
	}
}
