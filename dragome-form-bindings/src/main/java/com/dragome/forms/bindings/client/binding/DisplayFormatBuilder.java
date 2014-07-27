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

import com.dragome.forms.bindings.client.format.DisplayFormat;
import com.dragome.forms.bindings.client.function.Function;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jan 2, 2010
 * Time: 4:40:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class DisplayFormatBuilder<T>
{
	private HasDisplayFormat<T> binding;

	public DisplayFormatBuilder(HasDisplayFormat<T> binding)
	{
		this.binding= binding;
	}

	public void withFormat(DisplayFormat<? super T> format)
	{
		binding.setFormat(format);
	}

	public void withFormat(final Function<String, ? super T> format)
	{
		withFormat(new DisplayFormat<T>()
		{
			public String format(T value)
			{
				return format.compute(value);
			}
		});
	}
}