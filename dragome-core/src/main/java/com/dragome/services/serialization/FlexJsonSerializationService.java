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

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.dragome.services.interfaces.SerializationService;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.ObjectFactory;
import flexjson.transformer.Transformer;

public class FlexJsonSerializationService implements SerializationService
{
	JSONSerializer jsonSerializer;
	JSONDeserializer<?> jsonDeserializer;

	public FlexJsonSerializationService()
	{
		createSerializer();
		createDeserializer();
	}

	public Object deserialize(String resourcePath)
	{
		return jsonDeserializer.deserialize(resourcePath);
	}

	public String serialize(Object object)
	{
		return jsonSerializer.deepSerialize(object);
	}

	private JSONSerializer createSerializer()
	{
		jsonSerializer= new JSONSerializer();
		jsonSerializer.transform(new ElementTrasformer(), Element.class);
		jsonSerializer.transform(new MethodTrasformer(), Method.class);
		jsonSerializer.transform(new DragomeClassTransformer(), Class.class);
		return jsonSerializer;
	}

	private JSONDeserializer<?> createDeserializer()
	{
		jsonDeserializer= new JSONDeserializer<Object>();
		jsonDeserializer.use(Method.class, new MethodFactory());
		jsonDeserializer.use(Class.class, new DragomeClassFactory());
		return jsonDeserializer;
	}

	public void addTransformer(Transformer transformer, Class<?>... types)
	{
		jsonSerializer.transform(transformer, types);
	}

	public void addFactory(Class<?> clazz, ObjectFactory objectFactory)
	{
		jsonDeserializer.use(clazz, objectFactory);
	}

}
