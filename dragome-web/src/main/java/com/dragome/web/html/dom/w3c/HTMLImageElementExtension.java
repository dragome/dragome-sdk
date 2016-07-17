package com.dragome.web.html.dom.w3c;

import org.w3c.dom.html.HTMLImageElement;

import com.dragome.commons.DelegateCode;

public interface HTMLImageElementExtension extends HTMLImageElement
{
	@DelegateCode(eval= "this.node.width")
	public int getWidthAsInteger();
	@DelegateCode(eval= "this.node.height")
	public int getHeightAsInteger();
}
