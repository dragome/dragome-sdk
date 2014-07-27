/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.model.pectin;

import com.dragome.model.interfaces.IndexedValidationDisplay;
import com.dragome.model.interfaces.IndexedValidationResult;
import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationDisplay;
import com.dragome.model.interfaces.ValidationMessage;
import com.dragome.model.interfaces.ValidationResult;
import com.dragome.model.interfaces.VisualLabel;

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
