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

import flexjson.BeanAnalyzer;
import flexjson.BeanProperty;
import flexjson.ChainedSet;
import flexjson.JSONContext;
import flexjson.JSONException;
import flexjson.Path;
import flexjson.TypeContext;

public class ObjectTransformer extends AbstractTransformer
{
	public void transform(Object object)
	{
		JSONContext context= getContext();
		Path path= context.getPath();
		ChainedSet visits= context.getVisits();
		try
		{
			if (!visits.contains(object))
			{
				context.setVisits(new ChainedSet(visits));
				context.getVisits().add(object);
				// traverse object
				BeanAnalyzer analyzer= BeanAnalyzer.analyze(resolveClass(object));
				TypeContext typeContext= context.writeOpenObject();

				context.writeName("@id");
				Integer objectId= getObjectId(object, context);
				context.getReferences().put(System.identityHashCode(object), objectId);
				context.write("" + objectId);

				context.writeComma();
				for (BeanProperty prop : analyzer.getProperties())
				{
					String name= prop.getName();
					path.enqueue(name);
					if (context.isIncluded(prop))
					{
						Object value= prop.getValue(object);
						//if (!context.getVisits().contains(value))
						{
							TransformerWrapper transformer= (TransformerWrapper) context.getTransformer(value);
							if ("class".equals(name)) //revisar esto!!!!
								transformer= new TransformerWrapper(new ClassTransformer());

							if (transformer == null)
								transformer= new TransformerWrapper(new NullTransformer());

							if ((transformer.getTransformer() instanceof ObjectTransformer) || !context.getVisits().contains(value))
							{
								if (!transformer.isInline())
								{
									if (!typeContext.isFirst())
										context.writeComma();
									typeContext.setFirst(false);
									context.writeName(name);
								}
								typeContext.setPropertyName(name);

								transformer.transform(value);
							}
						}
					}
					path.pop();
				}
				context.writeCloseObject();
				context.setVisits((ChainedSet) context.getVisits().getParent());

			}
			else
			{
				writeReference(object, context);
			}
		}
		catch (JSONException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JSONException("Error trying to deepSerialize", e);
		}
	}

	public static Integer getObjectId(Object object, JSONContext context)
	{
		return context.isSerializingWithUniqueIds() ? System.identityHashCode(object) : context.ids++;
	}

	private void writeReference(Object object, JSONContext context)
	{
		TypeContext typeContext= context.writeOpenObject();
		context.writeName("@ref");
		context.write(context.getReferences().get(System.identityHashCode(object)) + "");
		context.writeCloseObject();
	}

	protected Class resolveClass(Object obj)
	{
		return obj.getClass();
	}

}
