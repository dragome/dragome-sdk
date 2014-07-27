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

import com.dragome.forms.bindings.client.binding.AbstractBindingContainer;
import com.dragome.forms.bindings.client.form.FieldModelBase;
import com.dragome.forms.bindings.client.form.FormModel;
import com.dragome.forms.bindings.client.form.ListFieldModelBase;
import com.dragome.forms.bindings.client.form.validation.HasIndexedValidationResult;
import com.dragome.forms.bindings.client.form.validation.HasValidationResult;
import com.dragome.forms.bindings.client.form.validation.ValidationPlugin;
import com.dragome.model.interfaces.ValidationStyles;

/**
 * ValidationBinder binds the validation status of a given field to arbitrary widgets.
 *
 * @see net.ar.unfeca.model.interfaces.ValidationDisplay
 */
public class ValidationBinder extends AbstractBindingContainer
{
	private ValidationStyles validationStyles;

	/**
	 * Creates a new binder instance.
	 */
	public ValidationBinder()
	{
		this(ValidationStyles.defaultInstance());
	}

	/**
	 * Creates a new instance that uses the specified {@link net.ar.unfeca.model.interfaces.ValidationStyles} to apply styles
	 * to widgets.
	 *
	 * @param validationStyles the StyleApplicator to use.
	 */
	public ValidationBinder(ValidationStyles validationStyles)
	{
		if (validationStyles == null)
		{
			throw new NullPointerException("validationStyles is null");
		}
		this.validationStyles= validationStyles;
	}

	public ValidationBindingBuilder bindValidationOf(HasValidationResult hasValidation)
	{
		return new ValidationBindingBuilder(this, hasValidation, validationStyles);
	}

	public ValidationBindingBuilder bindValidationOf(FormModel form)
	{
		return bindValidationOf(ValidationPlugin.getValidationManager(form).getFormValidator());
	}

	public ValidationBindingBuilder bindValidationOf(FieldModelBase<?> field)
	{
		return bindValidationOf(ValidationPlugin.getFieldValidator(field));
	}

	public IndexedValidationBindingBuilder bindValidationOf(ListFieldModelBase<?> field)
	{
		return bindValidationOf(ValidationPlugin.getFieldValidator(field));
	}

	public IndexedValidationBindingBuilder bindValidationOf(HasIndexedValidationResult hasValidationResult)
	{
		return new IndexedValidationBindingBuilder(this, hasValidationResult, validationStyles);
	}

}
