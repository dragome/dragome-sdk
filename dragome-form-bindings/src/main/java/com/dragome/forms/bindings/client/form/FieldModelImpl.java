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

import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 30, 2009
 * Time: 7:32:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FieldModelImpl<T> extends AbstractFieldModelBase<T> implements FieldModel<T>
{
	public FieldModelImpl(FormModel formModel, ValueModel<T> source, Class<T> valueType)
	{
		super(formModel, source, valueType);
	}
}
