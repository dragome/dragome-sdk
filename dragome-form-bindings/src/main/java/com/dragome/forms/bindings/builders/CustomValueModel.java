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

import com.dragome.forms.bindings.client.value.AbstractMutableValueModel;

public abstract class CustomValueModel<T> extends AbstractMutableValueModel<T>
{
	public void fireValueChangeEvent(T newValue)
	{
		super.fireValueChangeEvent((T) new Object(), newValue);
	}

	public void fireValueChangeEvent()
    {
		super.fireValueChangeEvent(getValue());
    }
}
