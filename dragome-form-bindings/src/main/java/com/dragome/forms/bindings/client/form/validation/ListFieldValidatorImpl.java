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
import java.util.List;
import java.util.Map;

import com.dragome.forms.bindings.client.form.ListFieldModel;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.IndexedValidationResult;
import com.dragome.model.interfaces.IndexedValidationResultCollector;
import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationMessage;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 13, 2009
 * Time: 9:47:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListFieldValidatorImpl<T> extends AbstractFieldValidator implements ListFieldValidator<T>
{

	private ListFieldModel<T> fieldModel;
	private IndexedValidationResultImpl validationResult= new IndexedValidationResultImpl();

	private LinkedHashMap<ListValidator<? super T>, ValueModel<Boolean>> validators= new LinkedHashMap<ListValidator<? super T>, ValueModel<Boolean>>();

	public ListFieldValidatorImpl(ListFieldModel<T> fieldModel)
	{
		if (fieldModel == null)
		{
			throw new NullPointerException("fieldModel is null");
		}

		this.fieldModel= fieldModel;
	}

	public void addValidator(ListValidator<? super T> validator, ValueModel<Boolean> condition)
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
		IndexedValidationResultImpl result= new IndexedValidationResultImpl();
		runValidators(result);
		setValidationResult(result);
		return !result.contains(Severity.ERROR);
	}

	public void runValidators(IndexedValidationResultCollector collector)
	{
		runValidators(fieldModel.asUnmodifiableList(), collector);
	}

	protected void runValidators(List<T> values, IndexedValidationResultCollector collector)
	{
		if (values == null)
		{
			throw new NullPointerException("values is null");
		}

		if (collector == null)
		{
			throw new NullPointerException("collector is null");
		}

		for (Map.Entry<ListValidator<? super T>, ValueModel<Boolean>> entry : validators.entrySet())
		{
			ListValidator<? super T> validator= entry.getKey();
			ValueModel<Boolean> condition= entry.getValue();
			if (conditionSatisfied(condition))
			{
				validator.validate(values, collector);
			}
		}
	}

	public IndexedValidationResult getValidationResult()
	{
		return validationResult;
	}

	private void setValidationResult(IndexedValidationResultImpl result)
	{
		validationResult= result;
		fireValidationChanged();
	}

	public void addExternalMessage(ValidationMessage message)
	{
		validationResult.add(message);
		fireValidationChanged();
	}

	public void addExternalMessage(int index, ValidationMessage message)
	{
		validationResult.add(index, message);
		fireValidationChanged();
	}

	public void clear()
	{
		setValidationResult(new IndexedValidationResultImpl());
	}

	private void fireValidationChanged()
	{
		IndexedValidationEvent.fire(this, validationResult);
		ValidationEvent.fire(this, validationResult);
	}

	public HandlerRegistration addValidationHandler(IndexedValidationHandler handler)
	{
		return addHandler(IndexedValidationEvent.getType(), handler);
	}

	public HandlerRegistration addValidationHandler(ValidationHandler handler)
	{
		return addHandler(ValidationEvent.getType(), handler);
	}
}
