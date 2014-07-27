package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.channel.Destination;
import com.dragome.forms.bindings.client.command.ParameterisedCommand;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.client.value.ValueTarget;
import com.dragome.forms.bindings.extra.user.client.ui.HasHTML;
import com.dragome.forms.bindings.extra.user.client.ui.HasText;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: May 22, 2010
 * Time: 11:25:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class ValueBindingBuilder<T>
{
	private BindingBuilderCallback callback;
	private ValueModel<T> model;

	public ValueBindingBuilder(ValueModel<T> model, BindingBuilderCallback callback)
	{
		this.callback= callback;
		this.model= model;
	}

	protected ValueModel<T> getModel()
	{
		return model;
	}

	public void to(ValueTarget<? super T> widget)
	{
		getCallback().onBindingCreated(new ValueModelToValueTargetBinding<T>(model, widget), widget);
	}

	public void to(final ParameterisedCommand<? super T> command)
	{
		AbstractBinding binding= new ValueModelToValueTargetBinding<T>(model, new ValueTarget<T>()
		{
			public void setValue(T value)
			{
				command.execute(value);
			}
		});
		getCallback().onBindingCreated(binding, command);
	}

	public void to(final Destination<? super T> destination)
	{
		AbstractBinding binding= new ValueModelToValueTargetBinding<T>(model, new ValueTarget<T>()
		{
			public void setValue(T value)
			{
				destination.receive(value);
			}
		});
		getCallback().onBindingCreated(binding, destination);
	}

	/**
	 * @deprecated User {@link #toTextOf(com.google.gwt.user.client.ui.HasText)} instead.
	 * @param target the binding target.
	 * @return a builder to optionally configure the format.
	 */
	@Deprecated
	public DisplayFormatBuilder<T> toLabel(HasText target)
	{
		return toTextOf(target);
	}

	/**
	 * Binds the value model to the specified {@link HasText} target.
	 * @param target the binding target.
	 * @return a builder to optionally configure the format.
	 */
	public DisplayFormatBuilder<T> toTextOf(HasText target)
	{
		ValueModelToHasTextBinding<T> binding= new ValueModelToHasTextBinding<T>(model, target);
		getCallback().onBindingCreated(binding, target);
		return new DisplayFormatBuilder<T>(binding);
	}

	/**
	 * Binds the value model to the specified {@link HasHTML} target.
	 * @param target the binding target.
	 * @return a builder to optionally configure the format.
	 */
	public DisplayFormatBuilder<T> toHtmlOf(HasHTML target)
	{
		ValueModelToHasHtmlBinding<T> binding= new ValueModelToHasHtmlBinding<T>(model, target);
		getCallback().onBindingCreated(binding, target);
		return new DisplayFormatBuilder<T>(binding);
	}

	protected BindingBuilderCallback getCallback()
	{
		return callback;
	}
}
