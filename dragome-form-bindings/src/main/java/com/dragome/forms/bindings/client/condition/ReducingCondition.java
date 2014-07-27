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

import java.util.Collection;

import com.dragome.forms.bindings.client.function.Reduce;
import com.dragome.forms.bindings.client.value.ReducingValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 16, 2009
 * Time: 11:24:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReducingCondition extends ReducingValueModel<Boolean, Boolean> implements Condition
{
	public ReducingCondition(Reduce<Boolean, Boolean> function, Collection<ValueModel<Boolean>> models)
	{
		super(function, models);
	}

	public Condition and(ValueModel<Boolean> condition, ValueModel<Boolean>... others)
	{
		return Conditions.and(this, condition, others);
	}

	public Condition or(ValueModel<Boolean> condition, ValueModel<Boolean>... others)
	{
		return Conditions.or(this, condition, others);
	}

	public Condition not()
	{
		return Conditions.isNot(this);
	}
}
