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

package com.dragome.forms.bindings.client.form.validation.message;

import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationMessage;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 13, 2009
 * Time: 11:14:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationMessageImpl implements ValidationMessage
{
	private Severity severity= Severity.ERROR;
	private String message;
	private String additionalInfo;

	public ValidationMessageImpl(Severity severity, String message, String additionalInfo)
	{
		this.severity= severity;
		this.message= message;
		this.additionalInfo= additionalInfo;
	}

	public ValidationMessageImpl(Severity severity, String message)
	{
		this(severity, message, null);
	}

	public Severity getSeverity()
	{
		return severity;
	}

	public String getMessage()
	{
		return message;
	}

	public String getAdditionalInfo()
	{
		return additionalInfo;
	}

	public int compareTo(ValidationMessage o)
	{
		return severity.compareTo(o.getSeverity());
	}
}
