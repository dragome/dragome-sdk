/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.forms.bindings.builders.helpers;

import com.dragome.forms.bindings.builders.ComponentBuilder;
import com.dragome.forms.bindings.builders.Getter;
import com.dragome.forms.bindings.builders.ItemRepeater;
import com.dragome.forms.bindings.builders.Order;
import com.dragome.forms.bindings.builders.RepeaterBuilder;
import com.dragome.forms.bindings.builders.Supplier;
import com.dragome.forms.bindings.builders.Tester;

public class RepeaterBuilderHelper<T>
{
	private RepeaterBuilder<T> repeaterBuilder;

	public RepeaterBuilderHelper(RepeaterBuilder<T> repeaterBuilder)
	{
		this.repeaterBuilder= repeaterBuilder;
	}

	public RepeaterBuilderHelper<T> repeat(final ItemRepeaterHelper<T> itemRepeater)
	{
		repeaterBuilder.repeat(new ItemRepeater<T>()
		{
			public void process(T item, ComponentBuilder componentBuilder)
			{
				ComponentBuilder lastComponentBuilder= BinderHelper.componentBuilder;
				BinderHelper.componentBuilder= componentBuilder;
				itemRepeater.process(item);
				BinderHelper.componentBuilder= lastComponentBuilder;
			}
		});
		return this;
	}

	public RepeaterBuilderHelper<T> filter(Supplier<Tester<T>> aFilter)
	{
		repeaterBuilder.filter(aFilter);
		return this;
	}

	public RepeaterBuilderHelper<T> orderBy(Getter<T, Comparable<?>> getter, Supplier<Order> order)
	{
		repeaterBuilder.orderBy(getter, order);
		return this;
	}
}
