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

import java.util.Collection;

import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.list.ArrayListModel;
import com.dragome.model.interfaces.list.ListModel;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Sep 5, 2009
* Time: 11:42:50 AM
* To change this template use File | Settings | File Templates.
*/
public class FormattedListFieldBuilder<T>
{
	private Format<T> formatter;
	private FormModel formModel;
	private Class<T> valueType;
	private ListFormatExceptionPolicy<T> exceptionPolicy;

	protected FormattedListFieldBuilder(FormModel formModel, Class<T> type, Format<T> formatter, ListFormatExceptionPolicy<T> exceptionPolicy)
	{
		this.formModel= formModel;
		valueType= type;
		this.formatter= formatter;
		this.exceptionPolicy= exceptionPolicy;
	}

	public FormattedListFieldModel<T> create()
	{
		return formModel.createFormattedListFieldModel(new ArrayListModel<T>(), formatter, exceptionPolicy, valueType);
	}

	public FormattedListFieldModel<T> createWithValue(Collection<T> initialValue)
	{
		return formModel.createFormattedListFieldModel(new ArrayListModel<T>(initialValue), formatter, exceptionPolicy, valueType);
	}

	public FormattedListFieldModel<T> boundTo(ListModel<T> source)
	{
		return formModel.createFormattedListFieldModel(source, formatter, exceptionPolicy, valueType);
	}

	/**
	 * Binds the field to the specified provider using the specified key.  The type
	 * of the key is determined by the provider.  I.e. a ListModelProvider&lt;String&gt;
	 * will require a string key.
	 * @param provider the ValueModelProvider to use.
	 * @param key the key of the value (that will be passed to the provider).
	 * @return a new field model bound to the provider using the specified key.
	 */
	public <K> FormattedListFieldModel<T> boundTo(ListModelProvider<K> provider, K key)
	{
		return boundTo(provider.getListModel(key, valueType));
	}

}