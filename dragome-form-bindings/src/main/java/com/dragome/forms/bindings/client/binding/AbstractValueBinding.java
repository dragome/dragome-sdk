package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.value.GuardedValueChangeHandler;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.ValueChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: May 22, 2010
 * Time: 9:20:15 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractValueBinding<T> extends AbstractBinding
{
	private ValueModel<T> model;
	private ModelChangeHandler valueMonitor= new ModelChangeHandler();

	public AbstractValueBinding(ValueModel<T> model)
	{
		this.model= model;
		registerDisposable(model.addValueChangeHandler(valueMonitor));
	}

	protected ValueModel<T> getModel()
	{
		return model;
	}

	public void updateTarget()
	{
		updateTarget(model.getValue());
	}

	protected abstract void updateTarget(T value);

	protected void whileIgnoringModelChanges(Runnable r)
	{
		valueMonitor.whileIgnoringEvents(r);
	}

	protected Boolean areEqual(T one, T two)
	{
		return one != null ? one.equals(two) : two == null;
	}

	private class ModelChangeHandler extends GuardedValueChangeHandler<T>
	{
		@Override
		public void onGuardedValueChanged(ValueChangeEvent<T> event)
		{
			T value= event.getValue();
			updateTarget(value);
		}
	}
}
