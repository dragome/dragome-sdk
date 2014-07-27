/*
 * Copyright 2010 Andrew Pietsch
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

import java.util.List;

import com.dragome.forms.bindings.client.format.ListDisplayFormat;
import com.dragome.forms.bindings.client.function.Reduce;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jan 2, 2010
 * Time: 4:40:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListDisplayFormatBuilder<T>
{
	private HasListDisplayFormat<T> binding;

	public ListDisplayFormatBuilder(HasListDisplayFormat<T> binding)
	{
		this.binding= binding;
	}

	public void withFormat(ListDisplayFormat<? super T> format)
	{
		binding.setFormat(format);
	}

	public void withFormat(final Reduce<String, ? super T> format)
	{
		withFormat(new ListDisplayFormat<T>()
		{
			public String format(List<? extends T> values)
			{
				return format.compute(values);
			}
		});
	}
}