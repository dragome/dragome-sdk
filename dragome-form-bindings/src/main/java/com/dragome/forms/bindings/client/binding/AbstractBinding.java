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

package com.dragome.forms.bindings.client.binding;

import com.dragome.model.interfaces.HandlerRegistration;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 11, 2009
 * Time: 4:45:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBinding implements BindingContainer
{
	GarbageCollector gc= new GarbageCollector();

	public void registerDisposable(HandlerRegistration registration)
	{
		gc.add(registration);
	}

	public void registerDisposableAndUpdateTarget(AbstractBinding binding)
	{
		binding.updateTarget();
		gc.add(binding);
	}

	public void registerDisposable(Disposable disposable)
	{
		gc.add(disposable);
	}

	public abstract void updateTarget();

	public abstract Object getTarget();

	public void dispose()
	{
		gc.dispose();
	}
}
