package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.util.Utils;
import com.dragome.forms.bindings.extra.user.client.Command;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 13, 2010
 * Time: 1:30:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TransitionBindingSupport<T> implements ValueChangeHandler<T>
{
	final public T triggerValue;
	protected T lastKnownValue;
	private Command command;

	public TransitionBindingSupport(T lastKnownValue, T triggerValue)
	{
		this.lastKnownValue= lastKnownValue;
		this.triggerValue= triggerValue;
	}

	public void onValueChange(ValueChangeEvent<T> event)
	{
		onNewValue(event.getValue());

	}

	void onNewValue(T newValue)
	{
		if (Utils.areEqual(lastKnownValue, newValue))
		{
			return;
		}

		// we only trigger if the values are different.
		if (isTrigger(triggerValue, lastKnownValue, newValue))
		{
			command.execute();
		}

		lastKnownValue= newValue;
	}

	public void setCommand(Command command)
	{
		this.command= command;
	}

	abstract boolean isTrigger(T triggerValue, T lastKnownValue, T newValue);

}
