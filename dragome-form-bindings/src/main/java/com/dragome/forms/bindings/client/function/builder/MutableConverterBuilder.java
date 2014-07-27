package com.dragome.forms.bindings.client.function.builder;

import com.dragome.forms.bindings.client.value.Converter;
import com.dragome.forms.bindings.client.value.ConvertingMutableValueModel;
import com.dragome.forms.bindings.client.value.MutableValueModel;

/**
* Created by IntelliJ IDEA.
* User: andrew
* Date: Jul 16, 2010
* Time: 12:52:46 PM
* To change this template use File | Settings | File Templates.
*/
public class MutableConverterBuilder<S>
{
	private MutableValueModel<S> source;

	public MutableConverterBuilder(MutableValueModel<S> source)
	{
		this.source= source;
	}

	public <T> MutableValueModel<T> using(Converter<T, S> converter)
	{
		return new ConvertingMutableValueModel<T, S>(source, converter);
	}
}
