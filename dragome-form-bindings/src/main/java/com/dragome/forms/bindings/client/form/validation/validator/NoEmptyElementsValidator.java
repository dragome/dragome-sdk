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

package com.dragome.forms.bindings.client.form.validation.validator;

import java.util.List;

import com.dragome.forms.bindings.client.form.validation.ListValidator;
import com.dragome.forms.bindings.client.form.validation.message.ErrorMessage;
import com.dragome.model.interfaces.IndexedValidationResultCollector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 20, 2008
 * Time: 5:35:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoEmptyElementsValidator implements ListValidator<String>
{
	public boolean stopAtFirstError= false;

	public NoEmptyElementsValidator()
	{
	}

	public NoEmptyElementsValidator(boolean stopAtFirstError)
	{
		this.stopAtFirstError= stopAtFirstError;
	}

	public void validate(List<? extends String> values, IndexedValidationResultCollector collector)
	{
		if (values != null)
		{
			for (int i= 0; i < values.size(); i++)
			{
				String value= values.get(i);
				if (value == null || value.trim().length() < 1)
				{
					collector.add(i, createErrorMessageForIndex(i));

					if (stopAtFirstError)
					{
						return;
					}
				}
			}
		}
	}

	protected ErrorMessage createErrorMessageForIndex(int i)
	{
		return new ErrorMessage(getErrorTextForIndex(i));
	}

	protected String getErrorTextForIndex(int index)
	{
		return "The value at position " + index + " is empty";
	}
}