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

package com.dragome.forms.bindings.client.form;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 9, 2009
 * Time: 6:01:58 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractField<T> extends AbstractHasHandlers implements Field<T>
{
	private FormModel formModel;
	private Class<T> valueClass;

	public AbstractField(FormModel formModel, Class<T> valueClass)
	{
		if (formModel == null)
		{
			throw new NullPointerException("formModel is null");
		}

		if (valueClass == null)
		{
			throw new NullPointerException("valueClass is null");
		}

		this.formModel= formModel;
		this.valueClass= valueClass;
	}

	public FormModel getFormModel()
	{
		return formModel;
	}

	public Class<T> getValueClass()
	{
		return valueClass;
	}

	protected void verifyMutableSource()
	{
		if (!isMutableSource())
		{
			throw new IllegalStateException("source model doesn't implement MutableValueModel");
		}
	}

}
