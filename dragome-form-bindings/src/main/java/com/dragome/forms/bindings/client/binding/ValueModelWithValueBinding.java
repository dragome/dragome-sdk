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

package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.model.interfaces.HasValue;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValueModelWithValueBinding<T> extends AbstractMutableValueBinding<T>
{
	private HasValue<Boolean> widget;

	private T value;
	private ValueChangeHandler<Boolean> widgetChangeMonitor= new ValueChangeHandler<Boolean>()
	{
		public void onValueChange(ValueChangeEvent<Boolean> event)
		{
			onWidgetValueChange(event);
		}
	};

	public ValueModelWithValueBinding(MutableValueModel<T> field, HasValue<Boolean> widget, T value)
	{
		super(field);
		this.widget= widget;
		this.value= value;
		registerDisposable(widget.addValueChangeHandler(widgetChangeMonitor));
	}

	private void onWidgetValueChange(ValueChangeEvent<Boolean> event)
	{
		// we only write back to the model if we've been selected.
		if (event.getValue())
		{
			updateModel(this.value);
		}
	}

	protected void updateTarget(T value)
	{
		widget.setValue(areEqual(this.value, value), false);
	}

	public HasValue<Boolean> getTarget()
	{
		return widget;
	}
}