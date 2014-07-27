package com.dragome.forms.bindings.client.bean;

import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 30, 2010
 * Time: 12:06:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HasMutableModel
{
	ValueModel<Boolean> mutable();
}
