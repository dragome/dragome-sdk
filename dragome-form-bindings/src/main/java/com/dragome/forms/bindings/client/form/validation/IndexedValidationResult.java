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

import java.util.Set;

/**
 * IndexedValidationResult records validation messages that have been registered
 * against a {@link com.pietschy.gwt.pectin.client.form.ListFieldModel}.  Messages can be indexed in that they are associated
 * with particular value in the model, or they can be non-indexed messages that apply to 
 * the whole collection.
 */
public interface IndexedValidationResult extends ValidationResult
{
	/**
	 * The total number of messages in this result.
	 * @return the total number of messages in this result. 
	 */
	public int size();

	/**
	 * Gets the a {@link ValidationResult} containing only the unindexed mesages.
	 * @return a {@link ValidationResult} containing only the unindexed mesages.  Returns
	 * an empty {@link ValidationResult} if there are no unindexed messages. 
	 */
	ValidationResult getUnindexedResult();

	/**
	 * Gets the a {@link ValidationResult} containing only the mesages at the specified
	 * index.
	 * @param index the index of interest.
	 * @return a {@link ValidationResult} containing only the message of the specified index.  Returns
	 * an empty {@link ValidationResult} if there are none at that index. 
	 */
	ValidationResult getIndexedResult(int index);

	Set<Integer> getResultIndicies();
}