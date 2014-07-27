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

import org.w3c.dom.Element;

import com.dragome.forms.bindings.client.binding.BindingContainer;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.HasVisible;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 17, 2009
 * Time: 1:47:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisibilityBindingBuilder
{
	private ValueModel<Boolean> model;
	private BindingContainer container;

	public VisibilityBindingBuilder(BindingContainer container, ValueModel<Boolean> model)
	{
		this.container= container;
		this.model= model;
	}

	/**
	 * Binds the visible metadata property to the speicified widget.
	 *
	 * @param widget the widget to bind to.
	 */
	public void to(UIObject widget)
	{
		container.registerDisposableAndUpdateTarget(new VisibleBinding(model, widget));
	}

	/**
	 * Binds the visible metadata property to the speicified widget.
	 *
	 * @param widget the widget to bind to.
	 * @param others other widgets to bind to.
	 */
	public void to(UIObject widget, UIObject... others)
	{
		container.registerDisposableAndUpdateTarget(new VisibleBinding(model, widget, others));
	}

	/**
	 * Binds the visible metadata property to the speicified dom element.  This binding
	 * delegates to {@link UIObject#setVisible(com.google.gwt.dom.client.Element, boolean)}
	 *
	 * @param element the widget to bind to.
	 */
	public void to(Element element)
	{
		container.registerDisposableAndUpdateTarget(new ElementVisibleBinding(model, element));
	}

	/**
	 * Binds the visible metadata property to the speicified dom element.  This binding
	 * delegates to {@link UIObject#setVisible(com.google.gwt.dom.client.Element, boolean)}
	 *
	 * @param element the widget to bind to.
	 * @param others additional widgets to bind to.
	 */
	public void to(Element element, Element... others)
	{
		container.registerDisposableAndUpdateTarget(new ElementVisibleBinding(model, element, others));
	}

	/**
	 * Binds the visible metadata property to the speicified widget.
	 *
	 * @param widget the widget to bind to.
	 */
	public void to(HasVisible widget)
	{
		container.registerDisposableAndUpdateTarget(new HasVisibleBinding(model, widget));
	}

	/**
	 * Binds the visible metadata property to the speicified widget.
	 *
	 * @param widget the widget to bind to.
	 * @param others additional widgets to bind to.
	 */
	public void to(HasVisible widget, HasVisible... others)
	{
		container.registerDisposableAndUpdateTarget(new HasVisibleBinding(model, widget, others));
	}

}
