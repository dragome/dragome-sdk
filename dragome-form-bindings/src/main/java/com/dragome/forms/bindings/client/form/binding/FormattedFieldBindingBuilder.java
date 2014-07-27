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

import com.dragome.forms.bindings.client.binding.BindingBuilderCallback;
import com.dragome.forms.bindings.client.binding.DisplayFormatBuilder;
import com.dragome.forms.bindings.client.form.FormattedFieldModel;
import com.dragome.forms.bindings.client.format.DisplayFormat;
import com.dragome.forms.bindings.extra.user.client.ui.HasText;
import com.dragome.model.interfaces.HasValue;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:48:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormattedFieldBindingBuilder<T>
{
	private BindingBuilderCallback callback;
	private FormattedFieldModel<T> field;

	public FormattedFieldBindingBuilder(FormattedFieldModel<T> field, BindingBuilderCallback callback)
	{
		this.callback= callback;
		this.field= field;
	}

	public SanitiseTextBuilder to(HasValue<String> widget)
	{
		FormattedFieldToHasValueBinding<T> binding= new FormattedFieldToHasValueBinding<T>(field, widget);
		callback.onBindingCreated(binding, widget);
		return new SanitiseTextBuilder(binding, widget);
	}

	public DisplayFormatBuilder<T> toLabel(HasText label)
	{
		FormattedFieldToHasTextBinding<T> binding= new FormattedFieldToHasTextBinding<T>(field, label);
		callback.onBindingCreated(binding, label);
		return new DisplayFormatBuilder<T>(binding);
	}

	/**
	 * @deprecated use toLabel(label).withFormat(format) instead.
	 */
	public void toLabel(HasText label, DisplayFormat<? super T> format)
	{
		toLabel(label).withFormat(format);
	}
}