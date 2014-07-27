package com.dragome.forms.bindings.client.binding;

/**
 * This interface allows each builder to notify the binder when a new binding is created.
 */
public interface BindingBuilderCallback
{
	void onBindingCreated(AbstractBinding binding, Object target);
}
