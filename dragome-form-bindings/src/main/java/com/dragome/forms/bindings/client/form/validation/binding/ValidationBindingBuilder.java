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

package com.dragome.forms.bindings.client.form.validation.binding;

import com.dragome.forms.bindings.client.form.validation.HasValidationResult;
import com.dragome.forms.bindings.client.style.VisualComponentUIObjectWrapper;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.model.interfaces.UIObject;
import com.dragome.model.interfaces.ValidationDisplay;
import com.dragome.model.interfaces.ValidationStyles;
import com.dragome.model.pectin.ValidationDisplayAdapter;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Sep 15, 2009
* Time: 12:09:38 PM
* To change this template use File | Settings | File Templates.
*/
public class ValidationBindingBuilder
{
	private HasValidationResult validator;
	private ValidationBinder binder;
	private ValidationStyles validationStyles;

	public ValidationBindingBuilder(ValidationBinder binder, HasValidationResult validator, ValidationStyles validationStyles)
	{
		this.binder= binder;
		this.validator= validator;
		this.validationStyles= validationStyles;
	}

	public void to(final ValidationDisplay validationDisplay)
	{
		binder.registerDisposableAndUpdateTarget(new ValidationDisplayBinding(validator, validationDisplay));
	}

	public void toStyleOf(UIObject widget)
	{
		binder.registerDisposableAndUpdateTarget(new ValidationStyleBinding(validator, widget, validationStyles));
	}

	public void to(VisualLabel label)
	{
		to(new ValidationDisplayAdapter(label));
	}

	public void toStyleOf(VisualComponent visualComponent)
	{
		toStyleOf(new VisualComponentUIObjectWrapper(visualComponent));
	}
}
