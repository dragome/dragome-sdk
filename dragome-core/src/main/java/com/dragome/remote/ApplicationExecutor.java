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
package com.dragome.remote;

import com.dragome.debugging.ServiceInvocationResult;
import com.dragome.debugging.execution.DragomeJsException;

public interface ApplicationExecutor
{
	void pushResult(ServiceInvocationResult result);
	void executeByClassName(String typeName);
	void pushException(DragomeJsException exception);
}
