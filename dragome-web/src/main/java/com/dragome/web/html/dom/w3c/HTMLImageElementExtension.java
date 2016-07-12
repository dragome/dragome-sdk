package com.dragome.web.html.dom.w3c;

import com.dragome.commons.DelegateCode;
import com.dragome.w3c.dom.html.HTMLImageElement;

public interface HTMLImageElementExtension extends HTMLImageElement
{
	@DelegateCode(eval= "this.node.width")
	public int getWidthAsInteger();
	@DelegateCode(eval= "this.node.height")
	public int getHeightAsInteger();
}
