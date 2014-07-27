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

import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.list.ListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 8:03:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValueConditionBuilder<T>
{
	private ValueModel<T> model;

	public ValueConditionBuilder(ValueModel<T> model)
	{
		this.model= model;
	}

	public Condition isNull()
	{
		return new ValueIsNullCondition<T>(getModel());
	}

	public Condition isNotNull()
	{
		return new ValueIsNullCondition<T>(getModel()).not();
	}

	public Condition is(T value)
	{
		return new ValueIsCondition<T>(getModel(), value);
	}

	public Condition isNot(T value)
	{
		return is(value).not();
	}

	public Condition isSameAs(ValueModel<T> model)
	{
		return new ValueEqualsCondition<T>(getModel(), model);
	}

	public Condition isNotSameAs(ValueModel<T> model)
	{
		return isSameAs(model).not();
	}

	public Condition isIn(T first, T... others)
	{
		return new ValueInCondition<T>(model, first, others);
	}

	public Condition isIn(ListModel<T> list)
	{
		return new ValueInCondition<T>(model, list);
	}

	public Condition isIn(Iterable<T> list)
	{
		return new ValueInCondition<T>(model, list);
	}

	public Condition isNotIn(T first, T... others)
	{
		return isIn(first, others).not();
	}

	public Condition isNotIn(ListModel<T> list)
	{
		return isIn(list).not();
	}

	public Condition isNotIn(Iterable<T> list)
	{
		return isIn(list).not();
	}

	protected ValueModel<T> getModel()
	{
		return model;
	}
}
