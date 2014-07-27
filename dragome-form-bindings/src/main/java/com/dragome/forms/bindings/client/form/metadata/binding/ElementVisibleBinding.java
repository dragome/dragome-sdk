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

package com.dragome.forms.bindings.client.form.metadata.binding;

import java.util.Collection;

import org.w3c.dom.Element;

import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Jul 17, 2009
* Time: 12:59:25 PM
* To change this template use File | Settings | File Templates.
*/
public class ElementVisibleBinding extends AbstractVisibleBinding<Element>
{
	public ElementVisibleBinding(ValueModel<Boolean> model, Element target)
	{
		super(model, target);
	}

	public ElementVisibleBinding(ValueModel<Boolean> model, Element target, Element... others)
	{
		super(model, target, others);
	}

	public ElementVisibleBinding(ValueModel<Boolean> model, Collection<Element> elements)
	{
		super(model, elements);
	}

	protected void updateVisibility(Element target, boolean visible)
	{
		UIObject.setVisible(target, visible);
	}

}