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

import java.util.Collection;

import com.dragome.model.interfaces.HasValue;
import com.dragome.model.interfaces.list.MutableListModel;

/**
 *
 */
public class MutableListBindingBuilder<T> extends ListBindingBuilder<T>
{
	public MutableListBindingBuilder(MutableListModel<T> model, BindingBuilderCallback callback)
	{
		super(model, callback);
	}

	public void to(HasValue<Collection<T>> widget)
	{
		getCallback().onBindingCreated(new MutableListModelToHasValueBinding<T>(getModel(), widget), widget);
	}

	public void to(MutableListModel<T> target)
	{
		getCallback().onBindingCreated(new MutableListModelToMutableListModelBinding<T>(getModel(), target), target);
	}

	public ContainingValueBuilder containingValue(T value)
	{
		return new ContainingValueBuilder(value);
	}

	@Override
	protected MutableListModel<T> getModel()
	{
		return (MutableListModel<T>) super.getModel();
	}

	public class ContainingValueBuilder
	{
		private T selectedValue;

		public ContainingValueBuilder(T selectedValue)
		{
			this.selectedValue= selectedValue;
		}

		public void to(HasValue<Boolean> selectable)
		{
			final ListContainsValueBinding<T> binding= new ListContainsValueBinding<T>(getModel(), selectable, selectedValue);
			getCallback().onBindingCreated(binding, binding.getTarget());
		}

	}
}
