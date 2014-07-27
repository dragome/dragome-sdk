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

package com.dragome.forms.bindings.client.value;

import com.dragome.forms.bindings.extra.event.shared.HandlerManager;
import com.dragome.model.interfaces.GwtEvent;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.HasValueChangeHandlers;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeEventAdapter;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 *
 */
public class DelegatingValueModel<T> implements MutableValueModel<T>, HasValueChangeHandlers<T>
{
	/**
	 * Convenience method to create a new instance.
	 *
	 * @return a new instance;
	 */
	public static <T> DelegatingValueModel<T> create()
	{
		return new DelegatingValueModel<T>();
	}

	/**
	 * Convenience method to create a new instance.
	 *
	 * @param defaultValue the default value for cases where the deleget is null.
	 * @return a new instance;
	 */
	public static <T> DelegatingValueModel<T> create(T defaultValue)
	{
		return new DelegatingValueModel<T>(defaultValue);
	}

	/**
	 * Convenience method to create a new instance that is preconigured with
	 * the specified delegate.
	 *
	 * @param delegate the delegate.
	 * @return a new instance;
	 */
	public static <T> DelegatingValueModel<T> create(ValueModel<T> delegate)
	{
		return new DelegatingValueModel<T>(delegate);
	}

	private HandlerManager handlerManager= new HandlerManager(this);
	private DelegateMonitor delegateChangeMonitor= new DelegateMonitor();

	private T defaultValue;
	private ValueModel<T> delegate;
	private HandlerRegistration registration;

	public DelegatingValueModel()
	{
	}

	public DelegatingValueModel(T defaultValue)
	{
		this.defaultValue= defaultValue;
	}

	public DelegatingValueModel(ValueModel<T> delegate)
	{
		setDelegate(delegate);
	}

	public void setDelegate(ValueModel<T> delegate)
	{
		if (registration != null)
		{
			registration.removeHandler();
			// we have to nuke it as we don't create a new one if the delegate
			// is null, and calling removeHandler() twice will barf.
			registration= null;
		}

		this.delegate= delegate;

		if (this.delegate != null)
		{
			registration= delegate.addValueChangeHandler(delegateChangeMonitor);
		}

		fireValueChanged();
	}

	protected ValueModel<T> getDelegate()
	{
		return delegate;
	}

	public T getDefaultValue()
	{
		return defaultValue;
	}

	public T getValue()
	{
		return delegate == null ? defaultValue : delegate.getValue();
	}

	public void setValue(T value)
	{
		if (delegate == null)
		{
			throw new IllegalStateException("delegate is null");
		}

		if (delegate instanceof MutableValueModel)
		{
			((MutableValueModel) delegate).setValue(value);
		}
		else
		{
			throw new IllegalStateException("delegate doesn't implement MutableValueModel");
		}
	}

	private void fireValueChanged()
	{
		ValueChangeEventAdapter.fire(DelegatingValueModel.this, getValue());
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler)
	{
		return handlerManager.addHandler(ValueChangeEventAdapter.getType(), handler);
	}

	public void fireEvent(GwtEvent<?> event)
	{
		handlerManager.fireEvent(event);
	}

	private class DelegateMonitor implements ValueChangeHandler<T>
	{
		public void onValueChange(ValueChangeEvent<T> event)
		{
			fireValueChanged();
		}
	}
}
