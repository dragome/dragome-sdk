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

package com.dragome.forms.bindings.client.condition;

import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 20, 2009
 * Time: 12:09:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextMatchesCondition extends AbstractComputedCondition<String>
{
	private String regex;

	public TextMatchesCondition(ValueModel<String> source, String regex)
	{
		super(source);

		if (regex == null)
		{
			throw new NullPointerException("regex is null");
		}

		this.regex= regex;
	}

	protected Boolean computeValue(String sourceValue)
	{
		return sourceValue != null && sourceValue.matches(regex);
	}

}