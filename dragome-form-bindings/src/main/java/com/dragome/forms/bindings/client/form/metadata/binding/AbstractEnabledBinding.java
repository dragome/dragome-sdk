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

import com.dragome.forms.bindings.client.binding.AbstractBinding;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Jul 17, 2009
* Time: 1:00:14 PM
* To change this template use File | Settings | File Templates.
*/
public abstract class AbstractEnabledBinding<W> extends AbstractBinding implements ValueChangeHandler<Boolean>
{
	private ValueModel<Boolean> model;
	private W widget;

	protected AbstractEnabledBinding(ValueModel<Boolean> model, W widget)
	{
		this.model= model;
		this.widget= widget;
		registerDisposable(model.addValueChangeHandler(this));
	}

	public void updateTarget()
	{
		updateWidget(model.getValue());
	}

	public W getTarget()
	{
		return widget;
	}

	protected abstract void updateWidget(boolean enabled);

	public void onValueChange(ValueChangeEvent<Boolean> event)
	{
		updateWidget(event.getValue());
	}
}
