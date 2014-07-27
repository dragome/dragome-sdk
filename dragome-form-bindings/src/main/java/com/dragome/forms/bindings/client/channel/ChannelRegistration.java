package com.dragome.forms.bindings.client.channel;

import com.dragome.forms.bindings.client.binding.Disposable;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 1, 2010
 * Time: 7:53:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ChannelRegistration extends Disposable
{
	// I need to think about the whole 'gc' issue a bit more closely. 
	//   public ChannelRegistration onlyOnce();
}
