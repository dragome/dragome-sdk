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

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationMessage;
import com.dragome.model.interfaces.ValidationResult;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Nov 20, 2009
 * Time: 12:39:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmptyValidationResult implements ValidationResult
{
	public static final ValidationResult INSTANCE= new EmptyValidationResult();
	private static final SortedSet<Severity> EMPTY_SEVERITIES= new TreeSet<Severity>();

	public boolean isEmpty()
	{
		return true;
	}

	public boolean contains(Severity severity)
	{
		return false;
	}

	public List<ValidationMessage> getMessages()
	{
		return Collections.emptyList();
	}

	public List<ValidationMessage> getMessages(Severity severity)
	{
		return Collections.emptyList();
	}

	public SortedSet<Severity> getSeverities()
	{
		return EMPTY_SEVERITIES;
	}
}
