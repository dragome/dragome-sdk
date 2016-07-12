package com.dragome.web.config;

import com.dragome.w3c.dom.html.HTMLCanvasElement;
import com.dragome.web.enhancers.jsdelegate.DefaultDelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.interfaces.SubTypeFactory;

public class DomHandlerDelegateStrategy extends DefaultDelegateStrategy
{
	public String getSubTypeExtractorFor(Class<?> interface1, String methodName)
	{
		if (HTMLCanvasElement.class.isAssignableFrom(interface1) && methodName.equals("getContext"))
			return "$1";
		else if (methodName.equals("item") || methodName.equals("cloneNode"))
			return "temp.nodeType";

		return null;
	}

	public Class<? extends SubTypeFactory> getSubTypeFactoryClassFor(Class<?> interface1, String methodName)
	{
		if (methodName.equals("item") || methodName.equals("cloneNode"))
			return NodeSubTypeFactory.class;
		else if (HTMLCanvasElement.class.isAssignableFrom(interface1) && methodName.equals("getContext"))
			return ContextSubTypeFactory.class;

		return null;
	}
}