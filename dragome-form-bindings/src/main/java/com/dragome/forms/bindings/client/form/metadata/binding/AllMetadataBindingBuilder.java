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

import com.dragome.forms.bindings.client.binding.BindingContainer;
import com.dragome.forms.bindings.client.form.metadata.Metadata;
import com.dragome.forms.bindings.extra.user.client.ui.FocusWidget;
import com.dragome.model.interfaces.HasEnabled;
import com.dragome.model.interfaces.HasVisible;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Jul 17, 2009
* Time: 1:46:54 PM
* To change this template use File | Settings | File Templates.
*/
public class AllMetadataBindingBuilder
{
	private BindingContainer container;
	private Metadata metadata;

	public AllMetadataBindingBuilder(BindingContainer container, Metadata metadata)
	{
		this.container= container;
		this.metadata= metadata;
	}

	/**
	 * Binds the visible and enabled properties to the speicified widget.  For the
	 * enabled bindings to work the widget must extend either FocusWidget or implement
	 * the {@link HasEnabled} interface.
	 * 
	 * @param target the widget to bind to.
	 */
	public void to(Object target)
	{
		if (target instanceof UIObject)
		{
			VisibleBinding binding= new VisibleBinding(metadata.getVisibleModel(), (UIObject) target);
			container.registerDisposableAndUpdateTarget(binding);
		}
		else if (target instanceof HasVisible)
		{
			HasVisibleBinding binding= new HasVisibleBinding(metadata.getVisibleModel(), (HasVisible) target);
			container.registerDisposableAndUpdateTarget(binding);
		}

		if (target instanceof FocusWidget)
		{
			FocusWidgetEnabledBinding binding= new FocusWidgetEnabledBinding(metadata.getEnabledModel(), (FocusWidget) target);
			container.registerDisposableAndUpdateTarget(binding);
		}
		else if (target instanceof HasEnabled)
		{
			HasEnabledBinding binding= new HasEnabledBinding(metadata.getEnabledModel(), (HasEnabled) target);
			container.registerDisposableAndUpdateTarget(binding);
		}
	}
}
