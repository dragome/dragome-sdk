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

package com.dragome.forms.bindings.client.form.metadata;

import com.dragome.forms.bindings.client.condition.Condition;
import com.dragome.forms.bindings.client.condition.ValueIsCondition;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 3, 2009
 * Time: 4:19:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetadataConditionBuilder
{
	private Metadata metadata;

	public MetadataConditionBuilder(Metadata metadata)
	{
		this.metadata= metadata;
	}

	public Condition isEnabled()
	{
		return new ValueIsCondition<Boolean>(metadata.getEnabledModel(), true);
	}

	public ValueModel<Boolean> isDisabled()
	{
		return new ValueIsCondition<Boolean>(metadata.getEnabledModel(), false);
	}

	public ValueModel<Boolean> isVisible()
	{
		return new ValueIsCondition<Boolean>(metadata.getVisibleModel(), true);
	}

	public ValueModel<Boolean> isHidden()
	{
		return new ValueIsCondition<Boolean>(metadata.getVisibleModel(), false);
	}
}
