/*
 * Copyright 2010 Andrew Pietsch
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

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jan 3, 2010
 * Time: 12:56:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HasValidation extends HasValidationResult
{
	/**
	 * Runs all the validators and updates the validation result.  If any widgets are bound
	 * to the result then they will also update.
	 * @return <code>false</code> if any validation message has {@link net.ar.unfeca.model.interfaces.Severity#ERROR}, <code>true</code>
	 * otherwise.
	 */
	boolean validate();

	/**
	 * Clears the validation result for this validator.
	 */
	void clear();
}
