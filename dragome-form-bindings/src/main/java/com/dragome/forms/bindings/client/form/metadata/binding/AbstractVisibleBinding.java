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

import com.dragome.forms.bindings.client.binding.AbstractValueBinding;
import com.dragome.forms.bindings.client.util.Utils;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 29, 2009
 * Time: 2:44:47 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractVisibleBinding<T> extends AbstractValueBinding<Boolean>
{
	private Collection<T> targets;

	public AbstractVisibleBinding(ValueModel<Boolean> model, T target)
	{
		this(model, Utils.asList(target));
	}

	public AbstractVisibleBinding(ValueModel<Boolean> model, T target, T... others)
	{
		this(model, Utils.asList(target, others));
	}

	public AbstractVisibleBinding(ValueModel<Boolean> model, Collection<T> target)
	{
		super(model);
		this.targets= target;
	}

	protected void updateTarget(Boolean value)
	{
		for (T target : getTarget())
		{
			updateVisibility(target, value != null ? value : false);
		}
	}

	public Collection<T> getTarget()
	{
		return targets;
	}

	protected abstract void updateVisibility(T target, boolean visible);

}
