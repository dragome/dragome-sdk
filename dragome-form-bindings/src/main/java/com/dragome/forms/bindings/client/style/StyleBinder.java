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

import java.util.Arrays;

import com.dragome.forms.bindings.client.binding.AbstractBindingContainer;
import com.dragome.forms.bindings.client.util.Utils;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 14, 2009
 * Time: 12:14:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class StyleBinder extends AbstractBindingContainer
{
	public StyleBuilder style(UIObject widget)
	{
		return new StyleBuilder(this, Arrays.asList(widget));
	}

	public StyleBuilder style(UIObject widget, UIObject... others)
	{
		return new StyleBuilder(this, Utils.asList(widget, others));
	}

	public StyleBuilder style(VisualComponent widget)
	{
		return style(new VisualComponentUIObjectWrapper(widget));
	}
}
