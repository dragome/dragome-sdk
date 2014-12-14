/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.model.pectin;

import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.model.interfaces.IndexedValidationDisplay;
import com.dragome.model.interfaces.IndexedValidationResult;
import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationDisplay;
import com.dragome.model.interfaces.ValidationMessage;
import com.dragome.model.interfaces.ValidationResult;

public class ValidationDisplayAdapter implements ValidationDisplay, IndexedValidationDisplay
{
	private VisualLabel<String> label;

	public ValidationDisplayAdapter(VisualLabel<String> label)
	{
		this.label= label;
	}

	public void setValidationResult(ValidationResult result)
	{
		updateDisplay(result);
	}

	private void updateDisplay(ValidationResult result)
	{
		if (result.isEmpty())
		{
			clearLabel();
		}
		else
		{
			StringBuilder text= new StringBuilder();
			for (ValidationMessage validationMessage : result.getMessages())
				text.append(validationMessage.getMessage() + "<br>");

			//	    ValidationMessage message= getHightestSeverityMessage(result);
			label.setValue(text.toString());
			//          validationStyles.applyStyle(label, message.getSeverity());
		}
	}

	protected ValidationMessage getHightestSeverityMessage(ValidationResult result)
	{
		Severity worstSeverity= result.getSeverities().iterator().next();
		return result.getMessages(worstSeverity).get(0);

	}

	private void clearLabel()
	{
		label.setValue("");
	}

	public void setValidationResult(IndexedValidationResult result)
	{
		setValidationResult((ValidationResult) result);
	}
}
