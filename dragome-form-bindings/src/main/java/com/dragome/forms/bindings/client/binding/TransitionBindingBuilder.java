package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 13, 2010
 * Time: 12:19:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransitionBindingBuilder<T>
{
	private BindingContainer bindingContainer;
	private ValueModel<T> model;

	public TransitionBindingBuilder(BindingContainer bindingContainer, ValueModel<T> model)
	{
		this.bindingContainer= bindingContainer;
		this.model= model;
	}

	public CommandBinder to(T triggerValue)
	{
		return new CommandBinder(new TransitionToBinding<T>(triggerValue, model.getValue()));
	}

	public CommandBinder from(T triggerValue)
	{
		return new CommandBinder(new TransitionFromBinding<T>(triggerValue, model.getValue()));
	}

	public class CommandBinder
	{
		private TransitionBindingSupport<T> binding;

		public CommandBinder(TransitionBindingSupport<T> binding)
		{
			this.binding= binding;
		}

		public void invoke(Command command)
		{
			binding.setCommand(command);
			bindingContainer.registerDisposable(model.addValueChangeHandler(binding));
		}

	}
}
