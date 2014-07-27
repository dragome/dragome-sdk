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
import java.util.SortedSet;

import com.dragome.model.interfaces.ValidationMessage;

/**
 * ValidationResult is a container for {@link ValidationMessage}s. 
 * @see com.pietschy.gwt.pectin.client.form.validation.binding.ValidationBinder
 * @see com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay
 * 
 */
public interface ValidationResult extends HasValidationMessages
{
	/**
	 * Checks if the result is empty.
	 * @return <code>true</code> if there are no messages, <code>false</code> otherwise.
	 */
	boolean isEmpty();

	/**
	 * Checks if the result contains one or messages of the specified severity.
	 * @param severity the severity of interest.
	 * @return <code>true</code> if the result contains messages with the specified severity, <code>false</code> otherwise.
	 */
	boolean contains(Severity severity);

	/**
	 * Gets all the messages contained in this result. 
	 * @return all the messages contained in this result.  
	 */
	List<ValidationMessage> getMessages();

	/**
	 * Gets all the messages of the specified severity contained in this result.
	 * @param severity the severity of interest. 
	 * @return all the messages contain in this result.  
	 */
	List<ValidationMessage> getMessages(Severity severity);

	SortedSet<Severity> getSeverities();
}