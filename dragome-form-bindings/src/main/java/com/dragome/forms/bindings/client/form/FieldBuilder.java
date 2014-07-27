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

import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.list.ListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 12:20:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class FieldBuilder<T>
{
	private FormModel formModel;
	private Class<T> valueType;

	protected FieldBuilder(FormModel formModel, Class<T> valueType)
	{
		this.formModel= formModel;
		this.valueType= valueType;
	}

	public FieldModel<T> create()
	{
		return formModel.createFieldModel(new ValueHolder<T>(), valueType);
	}

	public FieldModel<T> createWithValue(T initialValue)
	{
		return formModel.createFieldModel(new ValueHolder<T>(initialValue), valueType);
	}

	public FieldModel<T> boundTo(ValueModel<T> source)
	{
		return formModel.createFieldModel(source, valueType);
	}

	/**
	 * Binds the field to the specified provider using the specified key.  The type
	 * of the key is determined by the provider.  I.e. a ValueModelProvider&lt;String&gt;
	 * will require a string key.
	 * @param provider the ValueModelProvider to use.
	 * @param key the key of the value (that will be passed to the provider).
	 * @return a new field model bound to the provider using the specified key.
	 */
	public <K> FieldModel<T> boundTo(ValueModelProvider<K> provider, K key)
	{
		return boundTo(provider.getValueModel(key, valueType));
	}

	public <S> ConvertedFieldBuilder<T, S> convertedFrom(ValueModel<S> source)
	{
		return new ConvertedFieldBuilder<T, S>(formModel, valueType, source);
	}

	public <S> ComputedFieldBuilder<T, S> computedFrom(ValueModel<S> source)
	{
		return new ComputedFieldBuilder<T, S>(formModel, valueType, source);
	}

	public <S> ReducingFieldBuilder<T, S> computedFrom(ValueModel<S>... source)
	{
		return new ReducingFieldBuilder<T, S>(formModel, valueType, source);
	}

	public <S> ReducingListFieldBuilder<T, S> computedFrom(ListModel<S> source)
	{
		return new ReducingListFieldBuilder<T, S>(formModel, valueType, source);
	}
}
