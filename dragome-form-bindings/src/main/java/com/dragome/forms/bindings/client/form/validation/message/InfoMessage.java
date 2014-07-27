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

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 13, 2009
 * Time: 11:11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class InfoMessage extends ValidationMessageImpl
{

	public InfoMessage(String message)
	{
		super(Severity.INFO, message);
	}

	public InfoMessage(String message, String additionalInfo)
	{
		super(Severity.INFO, message, additionalInfo);
	}

}