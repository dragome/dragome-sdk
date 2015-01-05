/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.forms.bindings.builders;

import com.dragome.forms.bindings.client.value.MutableValueModel;

public class ValueModelDelegator<V> extends CustomValueModel<V>
{
	private MutableValueModel<V> valueSource;

	public ValueModelDelegator()
	{
	}
	
	public ValueModelDelegator(MutableValueModel<V> valueSource)
	{
		this.valueSource= valueSource;
	}

	public void fireValueChangeEvent(V newValue)
	{
		super.fireValueChangeEvent(null, newValue);
	}

	public void fireValueChangeEvent()
	{
		super.fireValueChangeEvent(getValue());
	}

	public V getValue()
	{
		return valueSource.getValue();
	}

	public void setValue(V value)
	{
		valueSource.setValue(value);
	}


	public MutableValueModel<V> getValueSource()
	{
		return valueSource;
	}

	public void setValueSource(MutableValueModel<V> valueSource)
	{
		this.valueSource= valueSource;
	}
}
