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

import com.dragome.forms.bindings.client.value.ValueTarget;
import com.dragome.model.interfaces.list.ListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListModelToValueTargetBinding<T> extends AbstractListBinding<T>
{
	private ValueTarget<Collection<T>> target;

	public ListModelToValueTargetBinding(ListModel<T> listModel, ValueTarget<Collection<T>> target)
	{
		super(listModel);
		this.target= target;
	}

	public ValueTarget<Collection<T>> getTarget()
	{
		return target;
	}

	@Override
	protected void updateTarget(ListModel<T> model)
	{
		getTarget().setValue(model.asUnmodifiableList());
	}
}