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

package com.dragome.forms.bindings.client.condition;

import static com.dragome.forms.bindings.client.util.Utils.areEqual;

import com.dragome.forms.bindings.client.util.Utils;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.ListModelChangedEvent;
import com.dragome.model.interfaces.list.ListModelChangedHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 20, 2009
 * Time: 12:09:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValueInCondition<T> extends AbstractComputedCondition<T>
{
	private Iterable<T> values;

	public ValueInCondition(ValueModel<T> source, T first, T... others)
	{
		this(source, Utils.asList(first, others));
	}

	public ValueInCondition(ValueModel<T> source, Iterable<T> values)
	{
		super(source);

		if (values == null)
		{
			throw new NullPointerException("value is null");
		}

		this.values= values;
	}

	public ValueInCondition(ValueModel<T> source, ListModel<T> listModel)
	{
		this(source, (Iterable<T>) listModel);

		listModel.addListModelChangedHandler(new ListModelChangedHandler<T>()
		{
			public void onListDataChanged(ListModelChangedEvent<T> tListModelChangedEvent)
			{
				recompute();
			}
		});
	}

	protected Boolean computeValue(T sourceValue)
	{
		for (T value : values)
		{
			if (areEqual(value, sourceValue))
			{
				return true;
			}
		}

		return false;
	}
}