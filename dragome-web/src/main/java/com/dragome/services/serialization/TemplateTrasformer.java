/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.services.serialization;

import org.w3c.dom.Element;

import com.dragome.helpers.DragomeEntityManager;

import flexjson.JSONContext;
import flexjson.TypeContext;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.ClassTransformer;

public class TemplateTrasformer extends AbstractTransformer
{
	public void transform(Object object)
	{
		JSONContext context= getContext();
		Element element= (Element) object;
		
		String id= DragomeEntityManager.add(element);
		
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
