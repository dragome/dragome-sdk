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

import com.dragome.forms.bindings.client.format.DisplayFormat;
import com.dragome.forms.bindings.client.format.ToStringFormat;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.user.client.ui.HasHTML;
import com.dragome.forms.bindings.extra.user.client.ui.HasText;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValueModelToHasHtmlBinding<T> extends AbstractValueBinding<T> implements HasDisplayFormat<T>
{
	private HasHTML target;
	private DisplayFormat<? super T> format;

	public ValueModelToHasHtmlBinding(ValueModel<T> model, HasHTML target)
	{
		this(model, target, ToStringFormat.defaultInstance());
	}

	public ValueModelToHasHtmlBinding(ValueModel<T> model, HasHTML target, DisplayFormat<? super T> format)
	{
		super(model);
		this.target= target;
		this.format= format;
	}

	protected void updateTarget(T value)
	{
		target.setHTML(format.format(value));
	}

	public HasText getTarget()
	{
		return target;
	}

	public void setFormat(DisplayFormat<? super T> format)
	{
		this.format= format;
		updateTarget();
	}
}