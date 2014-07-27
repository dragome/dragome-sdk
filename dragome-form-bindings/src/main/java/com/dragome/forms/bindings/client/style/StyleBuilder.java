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

import com.dragome.forms.bindings.client.binding.BindingContainer;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 14, 2009
 * Time: 2:09:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class StyleBuilder
{
	private BindingContainer builder;
	private List<UIObject> widgets;

	public StyleBuilder(BindingContainer builder, List<UIObject> widgets)
	{
		this.builder= builder;
		this.widgets= widgets;
	}

	/**
	 * Styles the widgets using {@link UIObject#addStyleName(String)}.
	 *
	 * @param style the style name
	 * @return the builder to specify the condition.
	 */
	public StyleBindingBuilder with(String style)
	{
		return new StyleBindingBuilder(builder, widgets, style);
	}

	/**
	 * Styles the widgets using {@link UIObject#addStyleDependentName(String)}.
	 *
	 * @param style the style name
	 * @return the builder to specify the condition.
	 */
	public DependentStyleBindingBuilder withDependent(String style)
	{
		return new DependentStyleBindingBuilder(builder, widgets, style);
	}
}
