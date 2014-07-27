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

import com.dragome.forms.bindings.client.form.FieldModel;
import com.dragome.forms.bindings.client.form.FieldModelBase;
import com.dragome.forms.bindings.client.form.FormModel;
import com.dragome.forms.bindings.client.form.FormattedFieldModel;
import com.dragome.forms.bindings.client.form.FormattedListFieldModel;
import com.dragome.forms.bindings.client.form.ListFieldModel;
import com.dragome.forms.bindings.client.form.ListFieldModelBase;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 19, 2008
 * Time: 12:27:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationPlugin
{

	public static <T> FieldValidationBuilder<T> validateField(FieldModel<T> field)
	{
		return new FieldValidationBuilder<T>(getValidationManager(field.getFormModel()), field);
	}

	public static <T> FormattedFieldValidationBuilder<T> validateField(FormattedFieldModel<T> field)
	{
		return new FormattedFieldValidationBuilder<T>(getValidationManager(field.getFormModel()), field);
	}

	public static <T> ListFieldValidationBuilder<T> validateField(ListFieldModel<T> field)
	{
		return new ListFieldValidationBuilder<T>(getValidationManager(field.getFormModel()), field);
	}

	public static <T> FormattedListFieldValidationBuilder<T> validateField(FormattedListFieldModel<T> field)
	{
		return new FormattedListFieldValidationBuilder<T>(getValidationManager(field.getFormModel()), field);
	}

	public static HasValidation getFieldValidator(FieldModelBase<?> field)
	{
		return getValidationManager(field.getFormModel()).getValidator(field);
	}

	public static HasIndexedValidationResult getFieldValidator(ListFieldModelBase<?> field)
	{
		return getValidationManager(field.getFormModel()).getIndexedValidator(field);
	}

	public static ValidationManager getValidationManager(FormModel form)
	{
		ValidationManager manager= (ValidationManager) form.getProperty(ValidationManager.class);
		if (manager == null)
		{
			manager= new ValidationManager(form);
			form.putProperty(ValidationManager.class, manager);
			form.addBindingCallback(manager);
		}

		return manager;
	}
}