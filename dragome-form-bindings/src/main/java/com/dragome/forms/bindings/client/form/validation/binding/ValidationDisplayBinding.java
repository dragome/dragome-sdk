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

import com.dragome.forms.bindings.client.binding.AbstractBinding;
import com.dragome.forms.bindings.client.form.validation.HasValidationResult;
import com.dragome.forms.bindings.client.form.validation.ValidationEvent;
import com.dragome.forms.bindings.client.form.validation.ValidationHandler;
import com.dragome.model.interfaces.ValidationDisplay;
import com.dragome.model.interfaces.ValidationResult;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 17, 2009
 * Time: 1:06:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationDisplayBinding extends AbstractBinding implements ValidationHandler
{
	private ValidationDisplay display;
	private HasValidationResult validator;

	public ValidationDisplayBinding(HasValidationResult validator, ValidationDisplay display)
	{
		this.display= display;
		this.validator= validator;
		registerDisposable(validator.addValidationHandler(this));
	}

	public void updateTarget()
	{
		updateDisplay(validator.getValidationResult());
	}

	public ValidationDisplay getTarget()
	{
		return display;
	}

	private void updateDisplay(ValidationResult result)
	{
		display.setValidationResult(result);
	}

	public void onValidate(ValidationEvent event)
	{
		updateDisplay(event.getValidationResult());
	}
}
