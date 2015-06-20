/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.web.debugging;

import java.util.Hashtable;
import java.util.Map;

import com.dragome.web.html.dom.DragomeJsException;

public class CrossExecutionSemaphore
{
	private static Map<String, ServiceInvocationResult> results= new Hashtable<String, ServiceInvocationResult>();

	public void pushResult(ServiceInvocationResult result)
	{
		results.put(result.getId(), result);
	}

	public Object waitUntilResponse(String id)
	{
		while (!(results.containsKey(id)))
			;

		ServiceInvocationResult result= results.remove(id);

		if (result.getObjectResult() instanceof DragomeJsException)
			throw (DragomeJsException) result.getObjectResult();

		return result.obtainRealResult();
	}
	public void reset()
	{
		results.clear();
	}
}
