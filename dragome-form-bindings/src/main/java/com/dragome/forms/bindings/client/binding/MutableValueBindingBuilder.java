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

package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.model.interfaces.HasValue;

/**
 *
 */
public class MutableValueBindingBuilder<T> extends ValueBindingBuilder<T>
{

	public MutableValueBindingBuilder(MutableValueModel<T> model, BindingBuilderCallback callback)
	{
		super(model, callback);
	}

	public void to(HasValue<T> widget)
	{
		getCallback().onBindingCreated(new MutableValueModelToHasValueBinding<T>(getModel(), widget), widget);
	}

	public void to(MutableValueModel<T> target)
	{
		getCallback().onBindingCreated(new MutableValueModelToMutableValueModelBinding<T>(getModel(), target), target);
	}

	@Override
	protected MutableValueModel<T> getModel()
	{
		return (MutableValueModel<T>) super.getModel();
	}

	public WithValueBuilder withValue(T value)
	{
		return new WithValueBuilder(value);
	}

	public class WithValueBuilder
	{
		private T selectedValue;

		public WithValueBuilder(T selectedValue)
		{
			this.selectedValue= selectedValue;
		}

		public void to(HasValue<Boolean> selectable)
		{
			getCallback().onBindingCreated(new ValueModelWithValueBinding<T>(getModel(), selectable, selectedValue), selectable);
		}
	}
}
