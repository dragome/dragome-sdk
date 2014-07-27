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

import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 11:42:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertingValueModel<T, S> extends AbstractValueModel<T>
{
	private Converter<T, S> converter;
	private ValueModel<S> source;

	private ValueChangeHandler<S> changeMonitor= new ValueChangeHandler<S>()
	{
		public void onValueChange(ValueChangeEvent<S> event)
		{
			fireValueChangeEvent(getValue());
		}
	};

	public ConvertingValueModel(ValueModel<S> source, Converter<T, S> converter)
	{
		if (source == null)
		{
			throw new NullPointerException("source is null");
		}

		if (converter == null)
		{
			throw new NullPointerException("converter is null");
		}

		this.source= source;
		this.converter= converter;

		this.source.addValueChangeHandler(changeMonitor);
	}

	protected ValueModel<S> getSource()
	{
		return source;
	}

	protected Converter<T, S> getConverter()
	{
		return converter;
	}

	public T getValue()
	{
		return converter.fromSource(source.getValue());
	}
}