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

import flexjson.JSONContext;
import flexjson.Path;
import flexjson.TypeContext;
import flexjson.transformer.AbstractTransformer;

public class DragomeClassTransformer extends AbstractTransformer
{
	public void transform(Object object)
	{
		JSONContext context= getContext();
		Path path= context.getPath();
		String name= ((Class) object).getName();
		name= name.replaceAll("_", ".");//TODO mejorar!!!
		TypeContext typeContext= context.writeOpenObject();

		context.writeName("name");
		context.writeQuoted(name);
		context.writeComma();
		context.writeName("class");
		context.writeQuoted("java.lang.Class");

		context.writeCloseObject();
	}
}
