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

package com.dragome.forms.bindings.client.form;

import static com.dragome.forms.bindings.client.function.Functions.convert;

import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.value.Converter;
import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Sep 5, 2009
 * Time: 10:40:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertedFormattedFieldBuilder<T, S>
{
	private FormModel formModel;
	private ValueModel<S> from;
	private Format<T> format;
	private FormatExceptionPolicy<T> exceptionPolicy;
	private Class<T> valueType;

	protected ConvertedFormattedFieldBuilder(FormModel formModel, ValueModel<S> from, Format<T> format, FormatExceptionPolicy<T> exceptionPolicy, Class<T> valueType)
	{
		this.formModel= formModel;
		this.from= from;
		this.format= format;
		this.exceptionPolicy= exceptionPolicy;
		this.valueType= valueType;
	}

	public FormattedFieldModel<T> using(final Converter<T, S> converter)
	{
		ValueModel<T> vm= from instanceof MutableValueModel ? convert((MutableValueModel<S>) from).using(converter) : convert(from).using(converter);

		return formModel.createFormattedFieldModel(vm, format, exceptionPolicy, valueType);
	}

}