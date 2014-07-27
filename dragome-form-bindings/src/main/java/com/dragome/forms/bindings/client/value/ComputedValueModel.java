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

import com.dragome.forms.bindings.client.function.Function;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Oct 28, 2009
 * Time: 11:14:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ComputedValueModel<T, S> extends AbstractValueModel<T>
{
	private ValueChangeHandler<S> changeMonitor= new ValueChangeHandler<S>()
	{
		public void onValueChange(ValueChangeEvent<S> event)
		{
			recompute();
		}
	};

	private ValueModel<S> source;
	private Function<T, ? super S> function;
	private T cachedValue;

	public ComputedValueModel(ValueModel<S> source, Function<T, ? super S> function)
	{
		if (source == null)
		{
			throw new NullPointerException("source is null");
		}

		if (function == null)
		{
			throw new NullPointerException("function is null");
		}

		this.source= source;
		this.function= function;
		this.source.addValueChangeHandler(changeMonitor);

		recompute();
	}

	protected void recompute()
	{
		T oldValue= cachedValue;
		cachedValue= function.compute(source.getValue());
		fireValueChangeEvent(oldValue, cachedValue);
	}

	public T getValue()
	{
		return cachedValue;
	}

	public Function<T, ? super S> getFunction()
	{
		return function;
	}

	public void setFunction(Function<T, ? super S> function)
	{
		if (function == null)
		{
			throw new NullPointerException("function is null");
		}
		this.function= function;
		recompute();
	}

}
