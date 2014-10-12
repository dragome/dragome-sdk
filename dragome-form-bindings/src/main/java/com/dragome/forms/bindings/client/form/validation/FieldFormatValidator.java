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

import java.util.List;

import com.dragome.forms.bindings.client.form.FormattedFieldBase;
import com.dragome.forms.bindings.client.form.validation.message.ErrorMessage;
import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.format.FormatException;
import com.dragome.model.interfaces.IndexedValidationResultCollector;
import com.dragome.model.interfaces.Message;
import com.dragome.model.pectin.ValidationResultCollector;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Nov 21, 2009
* Time: 10:37:20 AM
* To change this template use File | Settings | File Templates.
*/
public class FieldFormatValidator<T> implements Validator<String>, ListValidator<String>
{

	private FormattedFieldBase<T> model;

	public FieldFormatValidator(FormattedFieldBase<T> model)
	{
		this.model= model;
	}

	public void validate(String value, ValidationResultCollector results)
	{
		doParse(value, model.getFormat(), results);
	}

	public void validate(List<? extends String> values, IndexedValidationResultCollector results)
	{
		Format<T> format= model.getFormat();

		for (int i= 0; i < values.size(); i++)
		{
			String value= values.get(i);
			doParse(value, format, results.getIndexedCollector(i));
		}
	}

	private void doParse(String value, Format<T> format, ValidationResultCollector results)
	{
		try
		{
			format.parse(value);
		}
		catch (FormatException e)
		{
			if (e instanceof Message)
			{
				Message m= (Message) e;
				results.add(new ErrorMessage(m.getMessage(), m.getAdditionalInfo()));
			}
			else
			{
				results.add(new ErrorMessage(e.getMessage()));
			}
		}
	}
}
