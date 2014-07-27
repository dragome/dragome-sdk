package com.dragome.forms.bindings.client.function.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.dragome.forms.bindings.client.function.Reduce;
import com.dragome.forms.bindings.client.util.Utils;
import com.dragome.forms.bindings.client.value.ReducingValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
* Created by IntelliJ IDEA.
* User: andrew
* Date: Jul 16, 2010
* Time: 12:52:15 PM
* To change this template use File | Settings | File Templates.
*/
public class ReducingBuilder<S>
{
	private List<ValueModel<S>> models;

	public ReducingBuilder(ValueModel<S> first, ValueModel<S>... others)
	{
		this.models= Utils.asList(first, others);
	}

	public ReducingBuilder(Collection<ValueModel<S>> models)
	{
		this.models= new ArrayList<ValueModel<S>>(models);
	}

	public <T> ValueModel<T> using(Reduce<T, ? super S> function)
	{
		return new ReducingValueModel<T, S>(function, models);
	}
}
