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

package com.dragome.forms.bindings.client.form.validation;

import com.dragome.forms.bindings.client.binding.AbstractBinding;
import com.dragome.forms.bindings.client.binding.BindingContainer;
import com.dragome.forms.bindings.client.form.BindingCallback;
import com.dragome.forms.bindings.client.form.FieldModel;
import com.dragome.forms.bindings.client.form.FieldModelBase;
import com.dragome.forms.bindings.client.form.FormModel;
import com.dragome.forms.bindings.client.form.FormattedFieldModel;
import com.dragome.forms.bindings.client.form.FormattedListFieldModel;
import com.dragome.forms.bindings.client.form.ListFieldModel;
import com.dragome.forms.bindings.client.form.ListFieldModelBase;
import com.dragome.forms.bindings.client.form.validation.binding.IndexedValidationDisplayBinding;
import com.dragome.forms.bindings.client.form.validation.binding.ValidationDisplayBinding;
import com.dragome.model.interfaces.IndexedValidationDisplay;
import com.dragome.model.interfaces.ValidationDisplay;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 19, 2008
 * Time: 12:27:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationManager implements BindingCallback
{
	private FormValidator formValidator;

	public ValidationManager(FormModel form)
	{
		formValidator= new FormValidator(form);
	}

	public FieldValidator getValidator(FieldModelBase<?> field)
	{
		return getFormValidator().getValidator(field);
	}

	public ListFieldValidator getIndexedValidator(ListFieldModelBase<?> field)
	{
		return getFormValidator().getIndexedValidator(field);
	}

	public boolean validate()
	{
		return getFormValidator().validate();
	}

	public void clear()
	{
		getFormValidator().clear();
	}

	public FormValidator getFormValidator()
	{
		return formValidator;
	}

	public <T> void onWidgetBinding(final AbstractBinding binding, FieldModel<T> field, Object target)
	{
		doBinding(binding, target, getFormValidator().getFieldValidator(field, false));
	}

	public <T> void onWidgetBinding(AbstractBinding binding, FormattedFieldModel<T> field, Object target)
	{
		doBinding(binding, target, getFormValidator().getFieldValidator(field, false));
	}

	private <T> void doBinding(BindingContainer binding, Object target, FieldValidator<T> fieldValidator)
	{
		if (fieldValidator != null)
		{
			if (target instanceof ValidationDisplay)
			{
				ValidationDisplayBinding validationBinding= new ValidationDisplayBinding(fieldValidator, (ValidationDisplay) target);
				validationBinding.updateTarget();

				binding.registerDisposableAndUpdateTarget(validationBinding);
			}
		}
	}

	public <T> void onWidgetBinding(AbstractBinding binding, ListFieldModel<T> field, Object target)
	{
		ListFieldValidator<T> fieldValidator= getFormValidator().getFieldValidator(field, false);

		if (fieldValidator != null)
		{
			if (target instanceof IndexedValidationDisplay)
			{
				IndexedValidationDisplayBinding validationBinding= new IndexedValidationDisplayBinding(fieldValidator, (IndexedValidationDisplay) target);
				validationBinding.updateTarget();
				binding.registerDisposableAndUpdateTarget(validationBinding);
			}
		}
	}

	public <T> void onWidgetBinding(AbstractBinding binding, FormattedListFieldModel<T> field, Object target)
	{
		ListFieldValidator<T> fieldValidator= getFormValidator().getFieldValidator(field, false);

		if (fieldValidator != null)
		{
			if (target instanceof IndexedValidationDisplay)
			{
				IndexedValidationDisplayBinding validationBinding= new IndexedValidationDisplayBinding(fieldValidator, (IndexedValidationDisplay) target);
				validationBinding.updateTarget();
				binding.registerDisposableAndUpdateTarget(validationBinding);
			}
		}
	}
}