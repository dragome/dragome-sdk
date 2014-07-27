/*
 * Copyright 2010 Andrew Pietsch
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

import java.util.HashMap;

import com.dragome.forms.bindings.client.form.AbstractHasHandlers;
import com.dragome.forms.bindings.client.form.Field;
import com.dragome.forms.bindings.client.form.FieldModel;
import com.dragome.forms.bindings.client.form.FieldModelBase;
import com.dragome.forms.bindings.client.form.FormModel;
import com.dragome.forms.bindings.client.form.FormattedFieldModel;
import com.dragome.forms.bindings.client.form.FormattedListFieldModel;
import com.dragome.forms.bindings.client.form.ListFieldModel;
import com.dragome.forms.bindings.client.form.ListFieldModelBase;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationResult;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 19, 2008
 * Time: 12:27:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormValidator extends AbstractHasHandlers implements HasValidation
{
	private FormModel form;

	private HashMap<Field<?>, HasValidation> fieldValidators= new HashMap<Field<?>, HasValidation>();

	private ValidationMonitor validationMonitor= new ValidationMonitor();
	private ValidationResult validationResult= EmptyValidationResult.INSTANCE;

	public FormValidator(FormModel form)
	{
		this.form= form;
	}

	@SuppressWarnings("unchecked")
	public <T> FieldValidatorImpl<T> getFieldValidator(FieldModel<T> field, boolean create)
	{
		FieldValidatorImpl<T> validator= (FieldValidatorImpl<T>) fieldValidators.get(field);

		if (validator == null && create)
		{
			validator= new FieldValidatorImpl<T>(field);
			validator.addValidationHandler(validationMonitor);
			fieldValidators.put(field, validator);
		}

		return validator;
	}

	@SuppressWarnings("unchecked")
	public <T> FormattedFieldValidatorImpl<T> getFieldValidator(FormattedFieldModel<T> field, boolean create)
	{
		FormattedFieldValidatorImpl<T> validator= (FormattedFieldValidatorImpl<T>) fieldValidators.get(field);

		if (validator == null && create)
		{
			validator= new FormattedFieldValidatorImpl<T>(field);
			validator.addValidationHandler(validationMonitor);
			fieldValidators.put(field, validator);
		}

		return validator;
	}

	@SuppressWarnings("unchecked")
	public <T> ListFieldValidatorImpl<T> getFieldValidator(ListFieldModel<T> field, boolean create)
	{
		ListFieldValidatorImpl<T> validator= (ListFieldValidatorImpl<T>) fieldValidators.get(field);

		if (validator == null && create)
		{
			validator= new ListFieldValidatorImpl<T>(field);
			validator.addValidationHandler((IndexedValidationHandler) validationMonitor);
			fieldValidators.put(field, validator);
		}

		return validator;
	}

	@SuppressWarnings("unchecked")
	public <T> FormattedListFieldValidatorImpl<T> getFieldValidator(FormattedListFieldModel<T> field, boolean create)
	{
		FormattedListFieldValidatorImpl<T> validator= (FormattedListFieldValidatorImpl<T>) fieldValidators.get(field);

		if (validator == null && create)
		{
			validator= new FormattedListFieldValidatorImpl<T>(field);
			validator.addValidationHandler((IndexedValidationHandler) validationMonitor);
			fieldValidators.put(field, validator);
		}

		return validator;
	}

	@SuppressWarnings("unchecked")
	public FieldValidator getValidator(FieldModelBase<?> field)
	{
		if (field instanceof FieldModel)
		{
			return getFieldValidator((FieldModel) field, true);
		}
		else if (field instanceof FormattedFieldModel)
		{
			return getFieldValidator((FormattedFieldModel) field, true);
		}
		else
		{
			throw new IllegalStateException("Unsupported field type: " + field);
		}
	}

	@SuppressWarnings("unchecked")
	public ListFieldValidator getIndexedValidator(ListFieldModelBase<?> field)
	{
		if (field instanceof ListFieldModel)
		{
			return getFieldValidator((ListFieldModel) field, true);
		}
		else if (field instanceof FormattedListFieldModel)
		{
			return getFieldValidator((FormattedListFieldModel) field, true);
		}
		else
		{
			throw new IllegalStateException("Unsupported field type: " + field);
		}
	}

	public boolean validate()
	{
		try
		{
			validationMonitor.setIgnoreEvents(true);

			boolean valid= true;

			for (HasValidation validator : fieldValidators.values())
			{
				validator.validate();
				if (validator.getValidationResult().contains(Severity.ERROR))
				{
					valid= false;
				}
			}

			updateValidationResult();

			return valid;
		}
		finally
		{
			validationMonitor.setIgnoreEvents(false);
		}

	}

	public void clear()
	{
		try
		{
			validationMonitor.setIgnoreEvents(true);

			for (HasValidation validator : fieldValidators.values())
			{
				validator.clear();
			}

			updateValidationResult();
		}
		finally
		{
			validationMonitor.setIgnoreEvents(false);
		}

	}

	public ValidationResult getValidationResult()
	{
		return validationResult;
	}

	private void setValidationResult(ValidationResultImpl result)
	{
		if (result == null)
		{
			throw new NullPointerException("validationResult is null");
		}

		this.validationResult= result;
		fireValidationChanged();
	}

	private void fireValidationChanged()
	{
		ValidationEvent.fire(this, validationResult);
	}

	public HandlerRegistration addValidationHandler(ValidationHandler handler)
	{
		return addHandler(handler, ValidationEvent.getType());
	}

	private void updateValidationResult()
	{
		ValidationResultImpl validationResult= new ValidationResultImpl();
		for (Field<?> field : form.allFields())
		{
			if (fieldValidators.containsKey(field))
			{
				validationResult.addAll(fieldValidators.get(field).getValidationResult().getMessages());
			}
		}

		setValidationResult(validationResult);
	}

	private class ValidationMonitor implements ValidationHandler, IndexedValidationHandler
	{
		private boolean ignoreEvents= false;

		public void onValidate(ValidationEvent event)
		{
			doUpdate();
		}

		public void onValidate(IndexedValidationEvent event)
		{
			doUpdate();
		}

		private void doUpdate()
		{
			if (!isIgnoreEvents())
			{
				updateValidationResult();
			}
		}

		public boolean isIgnoreEvents()
		{
			return ignoreEvents;
		}

		public void setIgnoreEvents(boolean ignoreEvents)
		{
			this.ignoreEvents= ignoreEvents;
		}
	}
}