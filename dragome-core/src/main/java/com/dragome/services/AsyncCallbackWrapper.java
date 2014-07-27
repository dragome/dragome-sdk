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
package com.dragome.services;

import com.dragome.services.interfaces.AsyncCallback;

public abstract class AsyncCallbackWrapper<T> implements AsyncCallback<T>
{
	AsyncCallback<T> asyncCallback;

	public AsyncCallbackWrapper(AsyncCallback<T> asyncCallback)
	{
		this.asyncCallback= asyncCallback;
	}

	public AsyncCallback<T> getAsyncCallback()
	{
		return asyncCallback;
	}

	public void setAsyncCallback(AsyncCallback<T> asyncCallback)
	{
		this.asyncCallback= asyncCallback;
	}
}
