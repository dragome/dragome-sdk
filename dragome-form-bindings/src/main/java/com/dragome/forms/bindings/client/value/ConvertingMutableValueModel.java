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

package com.dragome.forms.bindings.client.value;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 12:12:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertingMutableValueModel<T, S> extends ConvertingValueModel<T, S> implements MutableValueModel<T>
{
	public ConvertingMutableValueModel(MutableValueModel<S> source, Converter<T, S> converter)
	{
		super(source, converter);
	}

	public void setValue(T value)
	{
		// we could improve performance by disabling the change monitor
		// and firing the change event ourselves (to avoid a double conversion), 
		// but we'd need to disable the monitor while setting the source value.
		getSource().setValue(getConverter().toSource(value));
	}

	protected MutableValueModel<S> getSource()
	{
		return (MutableValueModel<S>) super.getSource();
	}

}
