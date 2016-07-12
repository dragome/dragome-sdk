package com.dragome.web.html.dom.w3c;

import com.dragome.commons.DelegateCode;
import com.dragome.w3c.dom.html.MessageEvent;

public interface MessageEventExtension extends MessageEvent
{
	@DelegateCode(eval= "this.node.data")
	String getDataAsString();
}
