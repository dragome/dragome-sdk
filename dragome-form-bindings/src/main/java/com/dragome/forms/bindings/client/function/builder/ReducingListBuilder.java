package com.dragome.forms.bindings.client.function.builder;

import com.dragome.forms.bindings.client.function.Reduce;
import com.dragome.forms.bindings.client.list.ReducingValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.list.ListModel;

/**
* Created by IntelliJ IDEA.
* User: andrew
* Date: Jul 16, 2010
* Time: 12:51:41 PM
* To change this template use File | Settings | File Templates.
*/
public class ReducingListBuilder<S>
{
	private ListModel<S> model;

	public ReducingListBuilder(ListModel<S> model)
	{
		this.model= model;
	}

	public <T> ValueModel<T> using(Reduce<T, ? super S> function)
	{
		return new ReducingValueModel<T, S>(model, function);
	}
}
