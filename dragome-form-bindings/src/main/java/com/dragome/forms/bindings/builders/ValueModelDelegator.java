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

public class ValueModelDelegator<M, V> extends CustomValueModel<V>
{
	private MutableValueModel<V> valueSource;
	private M model;

	public ValueModelDelegator(MutableValueModel<V> valueSource, M model)
	{
		this.valueSource= valueSource;
		this.model= model;
	}

	public ValueModelDelegator(M model)
	{
		this.model= model;
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

	public M getModel()
	{
		return model;
	}

	public void setModel(M model)
	{
		this.model= model;
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
