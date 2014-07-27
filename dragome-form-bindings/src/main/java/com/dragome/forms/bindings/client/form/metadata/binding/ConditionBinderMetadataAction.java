package com.dragome.forms.bindings.client.form.metadata.binding;

import com.dragome.forms.bindings.client.form.metadata.Metadata;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
* Created by IntelliJ IDEA.
* User: andrew
* Date: Feb 24, 2010
* Time: 4:16:14 PM
* To change this template use File | Settings | File Templates.
*/
public interface ConditionBinderMetadataAction<T> extends ConditionBinderWidgetAction<T>
{
	ValueModel<Boolean> getModel(Metadata metadata);
}
