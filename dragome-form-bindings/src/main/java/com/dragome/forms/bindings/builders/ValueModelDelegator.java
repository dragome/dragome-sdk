/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
