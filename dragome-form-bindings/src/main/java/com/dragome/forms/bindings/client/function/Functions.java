package com.dragome.forms.bindings.client.function;

import java.util.Collection;

import com.dragome.forms.bindings.client.function.builder.ConverterBuilder;
import com.dragome.forms.bindings.client.function.builder.MutableConverterBuilder;
import com.dragome.forms.bindings.client.function.builder.ReducingBuilder;
import com.dragome.forms.bindings.client.function.builder.ReducingListBuilder;
import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.list.ListModel;

/**
 * 
 */
public class Functions
{
	public static <S> ConverterBuilder<S> convert(ValueModel<S> source)
	{
		return new ConverterBuilder<S>(source);
	}

	public static <S> MutableConverterBuilder<S> convert(MutableValueModel<S> source)
	{
		return new MutableConverterBuilder<S>(source);
	}

	public static <S> ReducingBuilder<S> computedFrom(ValueModel<S> source, ValueModel<S>... others)
	{
		return new ReducingBuilder<S>(source, others);
	}

	public static <S> ReducingBuilder<S> computedFrom(Collection<ValueModel<S>> models)
	{
		return new ReducingBuilder<S>(models);
	}

	public static <S> ReducingListBuilder<S> computedFrom(ListModel<S> source)
	{
		return new ReducingListBuilder<S>(source);
	}

}
