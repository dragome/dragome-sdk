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

package com.dragome.forms.bindings.client.form.validation.component;

import com.dragome.forms.bindings.client.form.validation.IndexedValidationResult;

/**
 * IndexedValidationDisplay denotes components that can display {@link IndexedValidationResult}s.
 */
public interface IndexedValidationDisplay
{
	/**
	 * Sets the {@link IndexedValidationResult} to display.
	 * @param result the {@link IndexedValidationResult} to display.
	 */
	void setValidationResult(IndexedValidationResult result);
}
