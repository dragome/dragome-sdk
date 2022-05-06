/**
 * Copyright 2007 Charlie Hubbard and Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package flexjson.transformer;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

import flexjson.TypeContext;

public class ArrayTransformer extends AbstractTransformer
{

	public void transform(Object object)
	{
		
		getContext().writeOpenObject();
		getContext().writeName("class");
		getContext().writeQuoted(Array.class.getName());
		getContext().writeComma();
		getContext().writeName("type");
		
		ClassTransformer classTransformer= new ClassTransformer();
		classTransformer.transform(object.getClass().getComponentType());
		getContext().writeComma();

		getContext().writeName("items");

		TypeContext typeContext= getContext().writeOpenArray();
		int length= Array.getLength(object);
		for (int i= 0; i < length; ++i)
		{
			if (!typeContext.isFirst())
				getContext().writeComma();
			typeContext.setFirst(false);
			getContext().transform(Array.get(object, i));
		}
		getContext().writeCloseArray();
		
		getContext().writeCloseObject();

	}

}
