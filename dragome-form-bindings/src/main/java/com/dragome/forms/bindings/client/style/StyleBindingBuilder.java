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

package com.dragome.forms.bindings.client.style;

import java.util.List;
import java.util.function.Supplier;

import com.dragome.forms.bindings.builders.BindingSync;
import com.dragome.forms.bindings.builders.ValueModelDelegator;
import com.dragome.forms.bindings.client.binding.BindingContainer;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.client.value.ValueSource;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Oct 27, 2009
 * Time: 10:37:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class StyleBindingBuilder
{
	private BindingContainer bindingContainer;
	private String styleName;
	private List<UIObject> widgets;

	public StyleBindingBuilder(BindingContainer bindingContainer, List<UIObject> widgets, String styleName)
	{
		this.bindingContainer= bindingContainer;
		this.styleName= styleName;
		this.widgets= widgets;
	}

	public void when(ValueModel<Boolean> condition)
	{
		bindingContainer.registerDisposableAndUpdateTarget(new StyleBinding(condition, widgets, styleName));
	}

	public void when(ValueSource<Boolean> condition)
	{
		ValueModelDelegator<Boolean> condition2= BindingSync.createCondition(new Supplier<Boolean>()
		{
			public Boolean get()
			{
				return condition.getValue();
			}
		});

		when(condition2);

		//		ValueModelDelegator<Boolean> condition= new ValueModelDelegator<Boolean>(condition);
		//		styleBinder.style(allFilterLink).with("selected").when(condition);

	}
}