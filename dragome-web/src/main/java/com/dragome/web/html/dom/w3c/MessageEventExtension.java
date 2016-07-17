package com.dragome.web.html.dom.w3c;

import org.w3c.dom.html.MessageEvent;

import com.dragome.commons.DelegateCode;

public interface MessageEventExtension extends MessageEvent
{
	@DelegateCode(eval= "this.node.data")
	String getDataAsString();
}
