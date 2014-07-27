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

import com.dragome.forms.bindings.client.util.Utils;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 *
 */
public abstract class AbstractComputedValueModel<T, S> extends AbstractValueModel<T>
{
	private ValueChangeHandler<S> changeMonitor= new ValueChangeHandler<S>()
	{
		public void onValueChange(ValueChangeEvent<S> event)
		{
			recompute();
		}
	};

	private ValueModel<S> source;
	private ValueCache valueCache;

	public AbstractComputedValueModel(ValueModel<S> source)
	{
		if (source == null)
		{
			throw new NullPointerException("source is null");
		}
		this.source= source;
		this.source.addValueChangeHandler(changeMonitor);
	}

	/**
	 * Recomputes the value and fires a value change event as required.
	 */
	protected void recompute()
	{
		// If we've never been initialised (i.e. getValue() has never been called) then
		// we need to force the firing (since the `oldValue` will equal the `newValue`
		// and thus no event will fire).  That way any listeners that have been added
		// prior to initialisation won't miss out on the first event.

		// must check this before we call getCache()
		boolean firstTime= !isCacheInitialised();

		// if we've never been initialised then this will be the 'newValue'
		T oldValue= getCache().getValue();

		// now recompute and get the value.
		getCache().recompute();
		T newValue= getCache().getValue();

		// and fire the event if the values are different or if this is the first time
		// we've been asked to recompute.
		if (firstTime || Utils.areDifferent(oldValue, newValue))
		{
			fireValueChangeEvent(newValue);
		}
	}

	public T getValue()
	{
		return getCache().getValue();
	}

	private ValueCache getCache()
	{
		if (valueCache == null)
		{
			valueCache= new ValueCache();
		}
		return valueCache;
	}

	private boolean isCacheInitialised()
	{
		return valueCache != null;
	}

	protected abstract T computeValue(S value);

	/**
	 * This class allows us to lazily compute the value on the first getValue() call or
	 * source model change event.
	 *
	 * Without the lazy computation we'd need to call recompute in our constructor... which would in
	 * turn case havoc for subclasses by invoking computeValue() during super()... thus before any 
	 * fields have been initialised... at which point we'd sit back and wait for the NullPointerExceptions.
	 */
	private class ValueCache
	{
		private T value;

		private ValueCache()
		{
			recompute();
		}

		public T getValue()
		{
			return value;
		}

		public void recompute()
		{
			value= computeValue(source.getValue());
		}
	}

}
