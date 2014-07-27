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

package com.dragome.forms.bindings.client.form.metadata;

import java.util.Collection;

import com.dragome.forms.bindings.client.format.DisplayFormat;
import com.dragome.forms.bindings.client.function.Function;
import com.dragome.forms.bindings.client.function.ToStringFunction;
import com.dragome.forms.bindings.client.value.ComputedValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jan 2, 2010
 * Time: 4:40:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class WatermarkFormatBuilder<T>
{
	private ComputedValueModel<String, T> model;

	public WatermarkFormatBuilder(Collection<Metadata> allMetadata, ValueModel<T> model)
	{
		this.model= new ComputedValueModel<String, T>(model, new ToStringFunction());
		for (Metadata metadata : allMetadata)
		{
			metadata.setWatermarkModel(this.model);
		}
	}

	public void formattedBy(Function<String, ? super T> format)
	{
		model.setFunction(format);
	}

	public void formattedBy(final DisplayFormat<? super T> format)
	{
		formattedBy(new Function<String, T>()
		{
			public String compute(T value)
			{
				return format.format(value);
			}
		});
	}
}
