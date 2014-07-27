/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.forms.bindings.builders;


import com.dragome.forms.bindings.client.style.StyleBinder;
import com.dragome.forms.bindings.client.style.StyleBindingBuilder;
import com.dragome.forms.bindings.client.style.StyleBuilder;
import com.dragome.model.interfaces.VisualComponent;

public class DragomeStyleBuilder<T>
{
	private StyleBinder styleBinder= new StyleBinder();
	private StyleBuilder styleBuilder;
	private StyleBindingBuilder styleBindingBuilder;
	private T model;

	public DragomeStyleBuilder(T model)
    {
		this.model= model;
    }

	public DragomeStyleBuilder<T> with(String className)
    {
		styleBindingBuilder= styleBuilder.with(className);
		
	    return this;
    }

	public void style(VisualComponent component)
    {
		styleBuilder= styleBinder.style(component);
    }

	public void when(Supplier<Boolean> object)
    {
		styleBindingBuilder.when(BindingSync.createCondition(object, model));
    }

}
