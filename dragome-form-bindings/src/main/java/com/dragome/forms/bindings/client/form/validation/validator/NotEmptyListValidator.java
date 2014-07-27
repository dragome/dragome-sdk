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
* Date: Nov 24, 2009
* Time: 9:15:58 AM
* To change this template use File | Settings | File Templates.
*/
public class NotEmptyListValidator<T> implements ListValidator<T>
{
	private String errorText;

	public NotEmptyListValidator(String message)
	{
		this.errorText= message;
	}

	public void validate(List<? extends T> ages, IndexedValidationResultCollector collector)
	{
		if (ages.size() < 1)
		{
			collector.add(createErrorMessage());
		}
	}

	protected ErrorMessage createErrorMessage()
	{
		return new ErrorMessage(getErrorText());
	}

	protected String getErrorText()
	{
		return errorText;
	}
}
