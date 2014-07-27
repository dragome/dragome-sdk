package com.dragome.forms.bindings.client.binding;

import com.dragome.model.interfaces.HandlerRegistration;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: May 22, 2010
 * Time: 10:45:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractBindingContainer implements BindingContainer
{
	private GarbageCollector gc= new GarbageCollector();

	/**
	 * Registers a binding with this binder.  The binding will be disposed when this binder
	 * is disposed.
	 *
	 * @param binding the binding to register.
	 */
	public void registerDisposableAndUpdateTarget(AbstractBinding binding)
	{
		binding.updateTarget();
		gc.add(binding);
	}

	/**
	 * Registers a HandlerRegistration with this container.  The handler will be unregistered
	 * when this binder is disposed.
	 *
	 * @param handlerRegistration the handler registration to register.
	 */
	public void registerDisposable(HandlerRegistration handlerRegistration)
	{
		gc.add(handlerRegistration);
	}

	public void registerDisposable(Disposable disposable)
	{
		gc.add(disposable);
	}

	/**
	 * Disposes all bindings created by the binder.  After this methods has finished
	 * listeners created by the bindings will be removed from all widgets and models.
	 */
	public void dispose()
	{
		gc.dispose();
	}
}
