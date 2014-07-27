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

package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.format.ListDisplayFormat;
import com.dragome.forms.bindings.extra.user.client.ui.HasHTML;
import com.dragome.model.interfaces.list.ListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListModelToHasHTMLBinding<T> extends AbstractListBinding<T> implements HasListDisplayFormat<T>
{
	private HasHTML target;
	private ListDisplayFormat<? super T> format;

	public ListModelToHasHTMLBinding(ListModel<T> listModel, HasHTML widget, ListDisplayFormat<? super T> format)
	{
		super(listModel);
		this.target= widget;
		this.format= format;
	}

	public HasHTML getTarget()
	{
		return target;
	}

	@Override
	protected void updateTarget(ListModel<T> model)
	{
		getTarget().setHTML(format.format(model.asUnmodifiableList()));
	}

	public void setFormat(ListDisplayFormat<? super T> format)
	{
		this.format= format;
		updateTarget();
	}

}