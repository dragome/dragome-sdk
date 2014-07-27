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
package com.dragome.services.interfaces;

import flexjson.ObjectFactory;
import flexjson.transformer.Transformer;

public interface SerializationService
{
	Object deserialize(String resourcePath);
	String serialize(Object object);
	void addTransformer(Transformer transformer, Class<?>... types);
	void addFactory(Class<?> clazz, ObjectFactory objectFactory);
}
