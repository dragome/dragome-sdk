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

import flexjson.TypeContext;

public class IterableTransformer extends AbstractTransformer
{

	public void transform(Object object)
	{
		Iterable iterable= (Iterable) object;
		TypeContext typeContext= getContext().writeOpenArray();
		for (Object item : iterable)
		{
			if (!typeContext.isFirst())
				getContext().writeComma();
			typeContext.setFirst(false);
			getContext().transform(item);
		}
		getContext().writeCloseArray();
	}

}
