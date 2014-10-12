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

package com.dragome.model.pectin;

import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationMessage;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 15, 2008
 * Time: 4:52:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ValidationResultCollector
{
	/**
	 * Adds a new message to the collector.
	 * @param message the validation message to add.
	 */
	void add(ValidationMessage message);

	/**
	 * Checks if the result contains one or messages of the specified severity.
	 * @param severity the severity of interest.
	 * @return <code>true</code> if the result contains messages with the specified severity, <code>false</code> otherwise.
	 */
	boolean contains(Severity severity);

}