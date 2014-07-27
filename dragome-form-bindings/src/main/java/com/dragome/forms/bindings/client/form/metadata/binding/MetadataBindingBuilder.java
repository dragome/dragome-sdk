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

package com.dragome.forms.bindings.client.form.metadata.binding;

import java.util.Collection;

import com.dragome.forms.bindings.client.form.Field;
import com.dragome.forms.bindings.client.form.metadata.Metadata;
import com.dragome.forms.bindings.client.form.metadata.MetadataPlugin;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 8:03:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetadataBindingBuilder<T>
{
	private Collection<Field<?>> fields;
	private Action action;

	public MetadataBindingBuilder(Collection<Field<?>> fields, Action action)
	{
		this.fields= fields;
		this.action= action;
	}

	public void when(ValueModel<Boolean> condition)
	{
		condition.addValueChangeHandler(new ValueChangeHandler<Boolean>()
		{
			public void onValueChange(ValueChangeEvent<Boolean> event)
			{
				Boolean conditionValue= event.getValue();
				configureMetadata(conditionValue != null ? conditionValue : false);
			}
		});

		Boolean conditionValue= condition.getValue();
		configureMetadata(conditionValue != null ? conditionValue : false);
	}

	private void configureMetadata(Boolean value)
	{
		for (Field<?> field : fields)
		{
			action.apply(MetadataPlugin.getMetadata(field), value);
		}
	}

	public interface Action
	{
		public void apply(Metadata metadata, boolean value);
	}

}