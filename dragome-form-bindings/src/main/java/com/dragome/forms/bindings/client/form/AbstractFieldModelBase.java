/*
 * Copyright 2010 Andrew Pietsch
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

package com.dragome.forms.bindings.client.form;

import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeEventAdapter;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 30, 2009
 * Time: 7:32:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractFieldModelBase<T> extends AbstractField<T> implements FieldModelBase<T>
{
	private SourceModelListener sourceListener= new SourceModelListener();
	private ValueModel<T> source;
	private T cachedValue;

	public AbstractFieldModelBase(FormModel formModel, ValueModel<T> source, Class<T> valueType)
	{
		super(formModel, valueType);
		this.source= source;

		source.addValueChangeHandler(sourceListener);
		// read our state from our source.
		onSourceModelChange(source.getValue());
	}

	protected void writeToSource(T value)
	{
		try
		{
			sourceListener.setIgnoreEvents(true);

			getMutableSource().setValue(value);
		}
		finally
		{
			sourceListener.setIgnoreEvents(false);
		}
	}

	protected void onSourceModelChange(T value)
	{
		cachedValue= value;
		fireValueChangeEvent(value);
	}

	public T getValue()
	{
		return cachedValue;
	}

	public void setValue(T value)
	{
		T oldValue= cachedValue;

		cachedValue= value;
		writeToSource(value);

		fireValueChangeEvent(oldValue, value);
	}

	public boolean isMutableSource()
	{
		return getSource() instanceof MutableValueModel;
	}

	public ValueModel<T> getSource()
	{
		return source;
	}

	public MutableValueModel<T> getMutableSource()
	{
		verifyMutableSource();
		return (MutableValueModel<T>) source;
	}

	protected void fireValueChangeEvent(T newValue)
	{
		ValueChangeEventAdapter.fire(this, newValue);
	}

	protected void fireValueChangeEvent(T oldValue, T newValue)
	{
		ValueChangeEventAdapter.fireIfNotEqual(this, oldValue, newValue);
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler)
	{
		return addHandler(handler, ValueChangeEventAdapter.getType());
	}

	/**
	 * Probably nice to move this to a buffer strategy.
	 */
	private class SourceModelListener implements ValueChangeHandler<T>
	{
		private boolean ignoreEvents= false;

		public void onValueChange(ValueChangeEvent<T> event)
		{
			if (!ignoreEvents)
			{
				onSourceModelChange(event.getValue());
			}
		}

		public void setIgnoreEvents(boolean ignoreEvents)
		{
			this.ignoreEvents= ignoreEvents;
		}
	}
}