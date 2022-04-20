package com.dragome.forms.bindings.client.style;

import com.dragome.model.interfaces.UIObject;

public class GenericUIObjectWrapper extends UIObject
{
	private UIStyler uiStyler;

	public GenericUIObjectWrapper(UIStyler uiStyler)
	{
		this.uiStyler= uiStyler;
	}
	
	public void addStyleName(String name)
	{
		uiStyler.addStyleName(name);
	}

	public void removeStyleName(String name)
	{
		uiStyler.removeStyleName(name);
	}
}
