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

package com.dragome.forms.bindings.client.style;

import java.util.List;

import com.dragome.forms.bindings.client.binding.AbstractValueBinding;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class DependentStyleBinding extends AbstractValueBinding<Boolean>
{
	private List<UIObject> widgets;
	private String styleName;

	public DependentStyleBinding(ValueModel<Boolean> condition, List<UIObject> widgets, String styleName)
	{
		super(condition);
		this.widgets= widgets;
		this.styleName= styleName;
	}

	protected void updateTarget(Boolean value)
	{
		for (UIObject widget : widgets)
		{
			if (Boolean.TRUE.equals(value))
			{
				widget.addStyleDependentName(styleName);
			}
			else
			{
				widget.removeStyleDependentName(styleName);
			}
		}
	}

	public List<UIObject> getTarget()
	{
		return widgets;
	}
}