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

package com.dragome.forms.bindings.client.form.binding;

import com.dragome.forms.bindings.client.form.FormattedFieldModel;
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
public class FormattedFieldToHasValueBinding<T> extends AbstractFormattedValueBinding<T>
{
	private HasValue<String> widget;

	public FormattedFieldToHasValueBinding(FormattedFieldModel<T> field, HasValue<String> widget)
	{
		super(field);
		this.widget= widget;
		registerDisposable(widget.addValueChangeHandler(new WidgetMonitor()));
	}

	public HasValue<String> getTarget()
	{
		return widget;
	}

	protected void updateTarget(String value)
	{
		widget.setValue(value);
	}

	private class WidgetMonitor implements ValueChangeHandler<String>
	{
		public void onValueChange(ValueChangeEvent<String> event)
		{
			onWidgetChanged(event.getValue());
		}
	}

}