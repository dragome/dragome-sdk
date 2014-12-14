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
package com.dragome.services;

import java.util.Map;

import com.dragome.services.interfaces.AsyncCallback;

public interface RequestExecutor
{
	public String executeSynchronousRequest(String url, Map<String, String> parameters);
	public String executeFixedSynchronousRequest(String url, Map<String, String> parameters);
	public String executeAsynchronousRequest(String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback);
	public void executeCrossDomainAsynchronousRequest(String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback);
}
