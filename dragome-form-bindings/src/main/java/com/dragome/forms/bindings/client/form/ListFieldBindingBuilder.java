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

import com.dragome.forms.bindings.client.list.ArrayListModel;
import com.dragome.model.interfaces.list.ListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 12:20:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListFieldBindingBuilder<T>
{
	private FormModel formModel;
	private Class<T> type;

	protected ListFieldBindingBuilder(FormModel formModel, Class<T> type)
	{
		this.formModel= formModel;
		this.type= type;
	}

	public ListFieldModel<T> create()
	{
		return formModel.createListModel(new ArrayListModel<T>(), type);
	}

	public ListFieldModel<T> boundTo(ListModel<T> source)
	{
		return formModel.createListModel(source, type);
	}

	/**
	 * Binds the field to the specified provider using the specified key.  The type
	 * of the key is determined by the provider.  I.e. a ListModelProvider&lt;String&gt;
	 * will require a string key.
	 * @param provider the ValueModelProvider to use.
	 * @param key the key of the value (that will be passed to the provider).
	 * @return a new field model bound to the provider using the specified key.
	 */
	public <K> ListFieldModel<T> boundTo(ListModelProvider<K> provider, K key)
	{
		return formModel.createListModel(provider.getListModel(key, type), type);
	}
}