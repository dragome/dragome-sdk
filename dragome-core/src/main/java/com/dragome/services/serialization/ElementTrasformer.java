/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.services.serialization;

import org.w3c.dom.Element;

import flexjson.JSONContext;
import flexjson.TypeContext;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.ClassTransformer;

public class ElementTrasformer extends AbstractTransformer
{
	public void transform(Object object)
	{
		JSONContext context= getContext();
		Element element= (Element) object;
		String id= System.identityHashCode(element) + "";
		element.setAttribute("data-debug-id", id);
		TypeContext typeContext= context.writeOpenObject();

		context.writeName("id");
		context.writeQuoted(id);
		context.writeComma();
		context.writeName("class");
		ClassTransformer classTransformer= new ClassTransformer();
		classTransformer.transform(object.getClass());
		//context.writeQuoted(object.getClass().getName());

		//	context.writeComma();
		//	context.writeName("elements");
		//
		//	NodeList childNodes= element.getChildNodes();
		//	List<Element> elements= new ArrayList<Element>();
		//	for (int i= 0; i < childNodes.getLength(); i++)
		//	    elements.add((Element) childNodes.item(i));
		//
		//
		//	TransformerWrapper transformer= (TransformerWrapper) context.getTransformer(elements);
		//
		//	path.enqueue("elements");
		//	transformer.transform(elements);
		//	path.pop();

		context.writeCloseObject();
	}
}
