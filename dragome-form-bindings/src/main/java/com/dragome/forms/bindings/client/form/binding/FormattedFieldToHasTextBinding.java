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

import com.dragome.forms.bindings.client.binding.AbstractValueBinding;
import com.dragome.forms.bindings.client.binding.HasDisplayFormat;
import com.dragome.forms.bindings.client.form.FormattedFieldModel;
import com.dragome.forms.bindings.client.format.DisplayFormat;
import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.extra.user.client.ui.HasText;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormattedFieldToHasTextBinding<T> extends AbstractValueBinding<T> implements HasDisplayFormat<T>
{
	private HasText widget;
	private DisplayFormat<? super T> userFormat;

	public FormattedFieldToHasTextBinding(FormattedFieldModel<T> field, HasText widget)
	{
		super(field);
		registerDisposable(field.getFormatModel().addValueChangeHandler(new FormatChangeHandler<T>()));
		this.widget= widget;
	}

	public HasText getTarget()
	{
		return widget;
	}

	protected void updateTarget(T value)
	{
		getTarget().setText(format(value));
	}

	protected String format(T value)
	{
		return userFormat != null ? userFormat.format(value) : getModel().getFormat().format(value);
	}

	@Override
	protected FormattedFieldModel<T> getModel()
	{
		return (FormattedFieldModel<T>) super.getModel();
	}

	public void setFormat(DisplayFormat<? super T> format)
	{
		this.userFormat= format;
		updateTarget();
	}

	private class FormatChangeHandler<T> implements ValueChangeHandler<Format<T>>
	{
		public void onValueChange(ValueChangeEvent<Format<T>> event)
		{
			updateTarget();
		}
	}
}