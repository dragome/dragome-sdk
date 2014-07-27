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

import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Sep 5, 2009
* Time: 11:42:50 AM
* To change this template use File | Settings | File Templates.
*/
public class FormattedFieldBuilder<T>
{
	private Format<T> formatter;
	private FormModel formModel;
	private Class<T> valueType;
	private FormatExceptionPolicy<T> exceptionPolicy;

	protected FormattedFieldBuilder(FormModel formModel, Class<T> type, Format<T> formatter, FormatExceptionPolicy<T> exceptionPolicy)
	{
		this.formModel= formModel;
		valueType= type;
		this.formatter= formatter;
		this.exceptionPolicy= exceptionPolicy;
	}

	public FormattedFieldModel<T> create()
	{
		return formModel.createFormattedFieldModel(new ValueHolder<T>(), formatter, exceptionPolicy, valueType);
	}

	public FormattedFieldModel<T> createWithValue(T initialValue)
	{
		return formModel.createFormattedFieldModel(new ValueHolder<T>(initialValue), formatter, exceptionPolicy, valueType);
	}

	public FormattedFieldModel<T> boundTo(ValueModel<T> source)
	{
		return formModel.createFormattedFieldModel(source, formatter, exceptionPolicy, valueType);
	}

	/**
	 * Binds the field to the specified provider using the specified key.  The type
	 * of the key is determined by the provider.  I.e. a ValueModelProvider&lt;String&gt;
	 * will require a string key.
	 * @param provider the ValueModelProvider to use.
	 * @param key the key of the value (that will be passed to the provider).
	 * @return a new field model bound to the provider using the specified key.
	 */
	public <K> FormattedFieldModel<T> boundTo(ValueModelProvider<K> provider, K key)
	{
		return boundTo(provider.getValueModel(key, valueType));
	}

	public <S> ConvertedFormattedFieldBuilder<T, S> convertedFrom(ValueModel<S> source)
	{
		return new ConvertedFormattedFieldBuilder<T, S>(formModel, source, formatter, exceptionPolicy, valueType);
	}
}
