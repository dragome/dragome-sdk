package com.dragome.forms.bindings.client.style;

import com.dragome.model.interfaces.UIObject;
import com.dragome.model.interfaces.VisualComponent;

public class VisualComponentUIObjectWrapper extends UIObject
{
	private VisualComponent visualComponent;

	public VisualComponentUIObjectWrapper(VisualComponent visualComponent)
	{
		this.visualComponent= visualComponent;
	}

	public void addStyleName(String name)
	{
		visualComponent.getStyle().addClass(name);
	}

	public void removeStyleName(String styleName)
	{
		visualComponent.getStyle().removeClass(styleName);
	}
}
