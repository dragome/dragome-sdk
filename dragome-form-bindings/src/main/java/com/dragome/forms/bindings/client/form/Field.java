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

package com.dragome.forms.bindings.client.form;

/**
 * Field is the base interfact for all types of field model.  It contains
 * a single method for accessing the enclosing {@link FormModel}.
 */
public interface Field<T>
{
	/**
	 * Gets the form to which this field belongs.
	 * @return the form to which this field belongs. 
	 */
	FormModel getFormModel();

	Class<T> getValueClass();

	boolean isMutableSource();
}
