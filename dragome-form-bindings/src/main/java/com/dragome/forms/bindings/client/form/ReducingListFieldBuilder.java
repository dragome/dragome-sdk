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

import static com.dragome.forms.bindings.client.function.Functions.computedFrom;

import com.dragome.forms.bindings.client.function.Reduce;
import com.dragome.model.interfaces.list.ListModel;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Sep 5, 2009
* Time: 10:40:33 AM
* To change this template use File | Settings | File Templates.
*/
public class ReducingListFieldBuilder<T, S>
{
	private FormModel formModel;
	private ListModel<S> model;
	private Class<T> valueType;

	public ReducingListFieldBuilder(FormModel formModel, Class<T> valueType, ListModel<S> model)
	{
		this.formModel= formModel;
		this.valueType= valueType;
		this.model= model;
	}

	public FieldModel<T> using(Reduce<T, ? super S> function)
	{
		return formModel.createFieldModel(computedFrom(model).using(function), valueType);
	}
}