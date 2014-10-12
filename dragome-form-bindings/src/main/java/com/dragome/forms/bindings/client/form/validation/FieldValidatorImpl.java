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

import java.util.LinkedHashMap;
import java.util.Map;

import com.dragome.forms.bindings.client.form.FieldModel;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationMessage;
import com.dragome.model.interfaces.ValidationResult;
import com.dragome.model.pectin.ValidationResultCollector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 13, 2009
 * Time: 9:10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class FieldValidatorImpl<T> extends AbstractFieldValidator implements FieldValidator<T>
{
	private FieldModel<T> fieldModel;

	private LinkedHashMap<Validator<? super T>, ValueModel<Boolean>> validators= new LinkedHashMap<Validator<? super T>, ValueModel<Boolean>>();

	private ValidationResultImpl validationResult= new ValidationResultImpl();

	public FieldValidatorImpl(FieldModel<T> fieldModel)
	{
		if (fieldModel == null)
		{
			throw new NullPointerException("fieldModel is null");
		}

		this.fieldModel= fieldModel;
	}

	public FieldModel<T> getFieldModel()
	{
		return fieldModel;
	}

	public void addValidator(Validator<? super T> validator, ValueModel<Boolean> condition)
	{
		if (validator == null)
		{
			throw new NullPointerException("validator is null");
		}

		if (condition == null)
		{
			throw new NullPointerException("condition is null");
		}

		validators.put(validator, condition);
	}

	public boolean validate()
	{
		ValidationResultImpl result= new ValidationResultImpl();
		runValidators(result);
		setValidationResult(result);
		return !result.contains(Severity.ERROR);
	}

	public void runValidators(ValidationResultCollector collector)
	{
		runValidators(fieldModel.getValue(), collector);
	}

	protected void runValidators(T value, ValidationResultCollector collector)
	{
		if (collector == null)
		{
			throw new NullPointerException("collector is null");
		}

		for (Map.Entry<Validator<? super T>, ValueModel<Boolean>> entry : validators.entrySet())
		{
			ValueModel<Boolean> condition= entry.getValue();
			Validator<? super T> validator= entry.getKey();

			if (conditionSatisfied(condition))
			{
				validator.validate(value, collector);
			}
		}
	}

	public void addExternalMessage(ValidationMessage message)
	{
		validationResult.add(message);
		fireValidationChanged();
	}

	public void clear()
	{
		setValidationResult(new ValidationResultImpl());
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
		return addHandler(ValidationEvent.getType(), handler);
	}

}
