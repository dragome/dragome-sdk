package com.dragome.forms.bindings.client.interceptor;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 17, 2010
 * Time: 9:34:26 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HasInterceptors
{
	void interceptUsing(Interceptor interceptor);
}
