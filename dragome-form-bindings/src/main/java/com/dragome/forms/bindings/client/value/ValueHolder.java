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

/**
 * ValueHolder is an implementation of {@link MutableValueModel} that is not bound to
 * any other source model.  It's typically used when you need a value model but want
 * to manually configure the value.
 */
public class ValueHolder<T> extends AbstractMutableValueModel<T>
{
	private boolean fireEventsEvenWhenValuesEqual= true;
	private T value;

	public ValueHolder()
	{
	}

	public ValueHolder(T initialValue)
	{
		this.value= initialValue;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T newValue)
	{
		T old= this.value;

		this.value= newValue;

		if (isFireEventsEvenWhenValuesEqual() || !areEqual(old, newValue))
		{
			// we always fire the change event.
			fireValueChangeEvent(newValue);
		}
	}

	protected boolean areEqual(T oldValue, T newValue)
	{
		return oldValue == null ? newValue == null : oldValue.equals(newValue);
	}

	/**
	 * Checks if this model is firing events even when the new value is the same as the old.
	 * <p>
	 * The default value is <code>true</code>.  This is to reduce gotcha's that can
	 * happen when ValueHolders are used as value model sources for {@link com.pietschy.gwt.pectin.client.bean.BeanModelProvider}.
	 * In particular the provider may have uncommitted changes, and calling setBean(..) a second time
	 * with the same bean won't cause the form to reset (which can be quite confusing).
	 * <p>
	 * Another option would be have this value default to <code>false</code> and put an entry in the
	 * Gotcha's page on the wiki.
	 * 
	 * @return <code>true</code> if the value model will always fire events when {@link #setValue(Object)}
	 * is called.  Returns <code>false</code> the model will only fire events if the new value is different from
	 * the current value.  The default value is <code>true</code>.
	 */
	public boolean isFireEventsEvenWhenValuesEqual()
	{
		return fireEventsEvenWhenValuesEqual;
	}

	/**
	 * Configures this holder to always fire events even if the new value is equal to the old.
	 * <p>
	 * The default value is <code>true</code>.  This is to reduce gotcha's that can
	 * happen when ValueHolders are used as value model sources for {@link com.pietschy.gwt.pectin.client.bean.BeanModelProvider}.
	 * In particular the provider may have uncommitted changes, and calling setBean(..) a second time
	 * with the same bean won't cause the form to reset (which can be quite confusing).
	 * <p>
	 * Another option would be have this value default to <code>false</code> and put an entry in the
	 * Gotcha's page on the wiki.
	 *
	 *
	 * @param fireEventsEvenWhenValuesEqual <code>true</code> if the value model will always fire events when {@link #setValue(Object)}
	 * is called.  Returns <code>false</code> the model will only fire events if the new value is different from
	 * the current value.  The default value is <code>true</code>.
	 */
	public void setFireEventsEvenWhenValuesEqual(boolean fireEventsEvenWhenValuesEqual)
	{
		this.fireEventsEvenWhenValuesEqual= fireEventsEvenWhenValuesEqual;
	}
}
