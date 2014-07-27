package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.util.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 13, 2010
 * Time: 12:52:08 PM
 * To change this template use File | Settings | File Templates.
 */
class TransitionFromBinding<T> extends TransitionBindingSupport<T>
{
	public TransitionFromBinding(T triggerValue, T lastKnownValue)
	{
		super(lastKnownValue, triggerValue);
	}

	@Override
	boolean isTrigger(T triggerValue, T lastKnownValue, T newValue)
	{
		return Utils.areEqual(lastKnownValue, triggerValue);
	}
}
