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
import com.dragome.forms.bindings.client.value.GuardedValueChangeHandler;
import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.model.interfaces.ValueChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:53:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFormattedValueBinding<T> extends AbstractFormattedBinding<FormattedFieldModel<T>>
{
	private GuardedValueChangeHandler<String> fieldMonitor= new GuardedValueChangeHandler<String>()
	{
		public void onGuardedValueChanged(ValueChangeEvent<String> event)
		{
			updateTarget(event.getValue());
		}
	};

	private MutableValueModel<String> textModel;

	public AbstractFormattedValueBinding(FormattedFieldModel<T> model)
	{
		super(model);
		textModel= model.getTextModel();
		registerDisposable(textModel.addValueChangeHandler(fieldMonitor));
	}

	protected abstract void updateTarget(String value);

	public void updateTarget()
	{
		updateTarget(textModel.getValue());
	}

	protected void onWidgetChanged(String text)
	{
		fieldMonitor.setIgnoreEvents(true);
		try
		{
			getModel().getTextModel().setValue(text);
		}
		finally
		{
			fieldMonitor.setIgnoreEvents(false);
		}
	}

}