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

package com.dragome.model.interfaces;

import com.dragome.services.interfaces.ValidationResultCollector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 15, 2008
 * Time: 4:52:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IndexedValidationResultCollector 
{
	/**
	 * Adds a non-indexed message.  This may be a message such as "the list requires
	 * at lest one value".
	 *
	 * @param message the message to add.
	 */
	void add(ValidationMessage message);

	/**
	 * Adds a indexed message.
	 *
	 * @param index   the index of the value in error
	 * @param message the message to add.
	 */
	void add(int index, ValidationMessage message);

	/**
	 * Gets a {@link ValidationResultCollector} whose messages are to this collector at
	 * the specified index
	 * @param index the index to add the messages to.
	 * @return a {@link ValidationResultCollector} whose messages are to this collector at
	 */
	ValidationResultCollector getIndexedCollector(int index);
}