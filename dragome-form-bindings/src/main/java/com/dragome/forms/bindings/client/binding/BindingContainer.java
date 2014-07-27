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
 * BindingContainer defines the common methods for all classes that generate bindings.  The methods
 * are primarily related to registering handlers and disposables.
 */
public interface BindingContainer extends Disposable
{
	/**
	 * Registers the binding with the container and ensures {@link com.pietschy.gwt.pectin.client.binding.AbstractBinding#updateTarget()}
	 * is invoked to synchronise the model with the target.
	 * <p>
	 * The container will ensure {@link AbstractBinding#dispose()} wil be called when the container is disposed.
	 *  
	 * @param binding the binding to register
	 */
	void registerDisposableAndUpdateTarget(AbstractBinding binding);

	/**
	 * Registers a {@link com.google.gwt.event.shared.HandlerRegistration} with this container.  The container will ensure
	 * that {@link com.google.gwt.event.shared.HandlerRegistration#removeHandler()} will be invoked when the container
	 * is disposed.
	 * @param handlerRegistration the registration to register.
	 */
	void registerDisposable(HandlerRegistration handlerRegistration);

	/**
	 * Registers a {@link Disposable} with this container.  The container will ensure
	 * that {@link Disposable#dispose()} will be invoked when the container
	 * is disposed.
	 * @param disposable the disposable to register.
	 */
	void registerDisposable(Disposable disposable);
}
