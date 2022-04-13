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

import java.util.function.Supplier;

import com.dragome.forms.bindings.builders.ComponentBuilder;
import com.dragome.forms.bindings.builders.Getter;
import com.dragome.forms.bindings.builders.ItemRepeater;
import com.dragome.forms.bindings.builders.Order;
import com.dragome.forms.bindings.builders.RepeaterBuilder;
import com.dragome.forms.bindings.builders.Tester;
import com.dragome.guia.components.interfaces.VisualComponent;

public class RepeaterBuilderHelper<T, C extends VisualComponent>
{
	private RepeaterBuilder<T, C> repeaterBuilder;

	public RepeaterBuilderHelper(RepeaterBuilder<T, C> repeaterBuilder)
	{
		this.repeaterBuilder= repeaterBuilder;
	}

	public RepeaterBuilderHelper<T, C> repeat(final ItemRepeaterHelper<T> itemRepeater)
	{
		repeaterBuilder.repeat(new ItemRepeater<T, C>()
		{
			public void process(T item, ComponentBuilder<C> componentBuilder)
			{
				ComponentBuilder<C> lastComponentBuilder= BinderHelper.componentBuilder;
				BinderHelper.componentBuilder= componentBuilder;
				itemRepeater.process(item);
				BinderHelper.componentBuilder= lastComponentBuilder;
			}
		});
		return this;
	}

	public RepeaterBuilderHelper<T, C> filter(Supplier<Tester<T>> aFilter)
	{
		repeaterBuilder.filter(aFilter);
		return this;
	}

	public RepeaterBuilderHelper<T, C> orderBy(Getter<T, Comparable<?>> getter, Supplier<Order> order)
	{
		repeaterBuilder.orderBy(getter, order);
		return this;
	}
}
