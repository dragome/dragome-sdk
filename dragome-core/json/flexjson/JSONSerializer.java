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
package flexjson;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flexjson.transformer.Transformer;
import flexjson.transformer.TransformerWrapper;
import flexjson.transformer.TypeTransformerMap;

/**
 * <p>
 * JSONSerializer is the main class for performing serialization of Java objects
 * to JSON.  JSONSerializer by default performs a shallow serialization.  While
 * this might seem strange there is a method to this madness.  Shallow serialization
 * allows the developer to control what is serialized out of the object graph.
 * This helps with performance, but more importantly makes good OO possible, fixes
 * the circular reference problem, and doesn't require boiler plate translation code.
 * You don't have to change your object model to make JSON work so it reduces your
 * work load, and keeps you
 * <a href="http://en.wikipedia.org/wiki/Don't_repeat_yourself">DRY</a>.
 * </p>
 * <p/>
 * <p>
 * Let's go through a simple example:
 * </p>
 * <p/>
 * <pre>
 *    JSONSerializer serializer = new JSONSerializer();
 *    return serializer.serialize( person );
 * <p/>
 * </pre>
 * <p/>
 * <p>
 * What this statement does is output the json from the instance of person.  So
 * the JSON we might see for this could look like:
 * </p>
 * <p/>
 * <pre>
 *    { "class": "com.mysite.Person",
 *      "firstname": "Charlie",
 *      "lastname": "Rose",
 *      "age", 23
 *      "birthplace": "Big Sky, Montanna"
 *    }
 * <p/>
 * </pre>
 * <p>
 * In this case it's look like it's pretty standard stuff.  But, let's say
 * Person had many hobbies (i.e. Person.hobbies is a java.util.List).  In
 * this case if we executed the code above we'd still getTransformer the same output.
 * This is a very important feature of flexjson, and that is any instance
 * variable that is a Collection, Map, or Object reference won't be serialized
 * by default.  This is what gives flexjson the shallow serialization.
 * </p>
 * <p/>
 * <p>
 * How would we include the <em>hobbies</em> field?  Using the {@link JSONSerializer#include}
 * method allows us to include these fields in the serialization process.  Here is
 * how we'd do that:
 * </p>
 * <p/>
 * <pre>
 *    return new JSONSerializer().include("hobbies").serialize( person );
 * <p/>
 * </pre>
 * <p/>
 * That would produce output like:
 * <p/>
 * <pre>
 *    { "class": "com.mysite.Person",
 *      "firstname": "Charlie",
 *      "lastname": "Rose",
 *      "age", 23
 *      "birthplace": "Big Sky, Montanna",
 *      "hobbies", [
 *          "poker",
 *          "snowboarding",
 *          "kite surfing",
 *          "bull riding"
 *      ]
 *    }
 * <p/>
 * </pre>
 * <p/>
 * <p>
 * If the <em>hobbies</em> field contained objects, say Hobby instances, then a
 * shallow copy of those objects would be performed.  Let's go further and say
 * <em>hobbies</em> had a List of all the people who enjoyed this hobby.
 * This would create a circular reference between Person and Hobby.  Since the
 * shallow copy is being performed on Hobby JSONSerialize won't serialize the people
 * field when serializing Hobby instances thus breaking the chain of circular references.
 * </p>
 * <p/>
 * <p>
 * But, for the sake of argument and illustration let's say we wanted to send the
 * <em>people</em> field in Hobby.  We can do the following:
 * </p>
 * <p/>
 * <pre>
 *    return new JSONSerializer().include("hobbies.people").serialize( person );
 * <p/>
 * </pre>
 * <p/>
 * <p>
 * JSONSerializer is smart enough to know that you want <em>hobbies</em> field included and
 * the <em>people</em> field inside hobbies' instances too.  The dot notation allows you
 * do traverse the object graph specifying instance fields.  But, remember a shallow copy
 * will stop the code from getting into an infinte loop.
 * </p>
 * <p/>
 * <p>
 * You can also use the exclude method to exclude fields that would be included.  Say
 * we have a User object.  It would be a serious security risk if we sent the password
 * over the network.  We can use the exclude method to prevent the password field from
 * being sent.
 * </p>
 * <p/>
 * <pre>
 *   return new JSONSerialize().exclude("password").serialize(user);
 * <p/>
 * </pre>
 * <p/>
 * <p>
 * JSONSerializer will also pay attention to any method or field annotated by
 * {@link flexjson.JSON}.  You can include and exclude fields permenantly using the
 * annotation.  This is good like in the case of User.password which should never
 * ever be sent through JSON.  However, fields like <em>hobbies</em> or
 * <em>favoriteMovies</em> depends on the situation so it's best NOT to annotate
 * those fields, and use the {@link JSONSerializer#include} method.
 * </p>
 * <p/>
 * <p>
 * In a shallow copy only these types of instance fields will be sent:
 * <strong>String</strong>, <strong>Date</strong>, <strong>Number</strong>,
 * <strong>Boolean</strong>, <strong>Character</strong>, <strong>Enum</strong>,
 * <strong>Object</strong> and <strong>null</strong>.  Subclasses of Object will be serialized
 * except for Collection or Arrays.  Anything that would cause a N objects would not be sent.
 * All types will be excluded by default.  Fields marked static or transient are not serialized.
 * </p>
 * <p>
 * Includes and excludes can include wildcards.  Wildcards allow you to do things like exclude
 * all class attributes.  For example *.class would remove the class attribute that all objects
 * have when serializing.  A open ended wildcard like * would cause deep serialization to take
 * place.  Be careful with that one.  Although you can limit it's depth with an exclude like
 * *.foo.  The order of evaluation of includes and excludes is the order in which you called their
 * functions.  First call to those functions will cause those expressions to be evaluated first.
 * The first expression to match a path that action will be taken thus short circuiting all other
 * expressions defined later.
 * </p>
 * <p>
 * Transforers are a new addition that allow you to modify the values that are being serialized.
 * This allows you to create different output for certain conditions.  This is very important in
 * web applications.  Say you are saving your text to the DB that could contain &lt; and &gt;.  If
 * you plan to add that content to your HTML page you'll need to escape those characters.  Transformers
 * allow you to do this.  Flexjson ships with a simple HTML encoder {@link flexjson.transformer.HtmlEncoderTransformer}.
 * Transformers are specified in dot notation just like include and exclude methods, but it doesn't
 * support wildcards.
 * </p>
 * <p>
 * JSONSerializer is safe to use the serialize() methods from two seperate
 * threads.  It is NOT safe to use combination of {@link JSONSerializer#include(String...)}
 * {@link JSONSerializer#transform(flexjson.transformer.Transformer, String...)}, or {@link JSONSerializer#exclude(String...)}
 * from multiple threads at the same time.  It is also NOT safe to use
 * {@link JSONSerializer#serialize(Object)} and include/exclude/transform from
 * multiple threads.  The reason for not making them more thread safe is to boost performance.
 * Typical use case won't call for two threads to modify the JsonSerializer at the same type it's
 * trying to serialize.
 * </p>
 */
public class JSONSerializer
{

	public final static char[] HEX= "0123456789ABCDEF".toCharArray();

	private TypeTransformerMap typeTransformerMap= new TypeTransformerMap(TransformerUtil.getDefaultTypeTransformers());
	private Map<Path, Transformer> pathTransformerMap= new HashMap<Path, Transformer>();

	private List<PathExpression> pathExpressions= new ArrayList<PathExpression>();

	private boolean prettyPrint;
	private String rootName;

	// OutputHander Configuration

	/**
	 * format output with indentations
	 *
	 * @param prettyPrint - should out put cleanfly formatted Json
	 * @return JsonSerializer for chaining configuration
	 */
	public JSONSerializer prettyPrint(boolean prettyPrint)
	{
		this.prettyPrint= prettyPrint;
		return this;
	}

	/**
	 * This wraps the resulting JSON in a javascript object that contains a single
	 * field named rootName.  This is great to use in conjunction with other libraries
	 * like EXTJS whose data models require them to be wrapped in a JSON object.
	 *
	 * @param rootName - name to assign to root object
	 * @return this JsonSerializer for chaining configurations
	 */
	public JSONSerializer rootName(String rootName)
	{
		this.rootName= rootName;
		return this;
	}

	// SERIALIZATION

	/**
	 * This performs a shallow serialization of the target instance. It uses a StringBuilder to write output to.
	 *
	 * @param target - the instance to serialize to JSON
	 * @return returns JSON as a String
	 */
	public String serialize(Object target)
	{
		return serialize(target, SerializationType.SHALLOW, new StringBuilderOutputHandler(new StringBuilder()));
	}

	/**
	 * This performs a shallow serialization of the target instance and
	 * passes the generated JSON into the provided Writer.
	 * This can be used to stream JSON back to a browser rather
	 * than wait for it to all complete and then dump it all at
	 * once like the StringBufferOutputHandler and StringBuilderOutputHandler
	 *
	 * @param target - the instance to serialize to JSON
	 * @param out - Writer to write output to
	 */
	public void serialize(Object target, Writer out)
	{
		serialize(target, SerializationType.SHALLOW, new WriterOutputHandler(out));
	}

	/**
	 * This performs a shallow serialization of the target instance and
	 * passes the generated JSON into the provided StringBuilder.
	 *
	 * @param target - the instance to serialize to JSON
	 * @param out - StringBuilder to write output to
	 * @return returns JSON as a String
	 */
	public String serialize(Object target, StringBuilder out)
	{
		return serialize(target, SerializationType.SHALLOW, new StringBuilderOutputHandler(out));
	}

	/**
	 * This performs a shallow serialization of the target instance and
	 * passes the generated JSON into the provided StringBuffer.
	 *
	 * @param target - the instance to serialize to JSON
	 * @param out - StringBuffer to write output to
	 * @return returns JSON as a String
	 */
	public String serialize(Object target, StringBuffer out)
	{
		return serialize(target, SerializationType.SHALLOW, new StringBufferOutputHandler(out));
	}

	/**
	  * This performs a shallow serialization of the target instance and
	  * passes the generated JSON into the provided OutputHandler.
	  *
	  * @param target - the instance to serialize to JSON
	  * @param out - OutputHandler to write output to
	  * @return returns JSON as a String
	  */
	public String serialize(Object target, OutputHandler out)
	{
		return serialize(target, SerializationType.SHALLOW, out);
	}

	/**
	 * This performs a deep serialization of the target instance.  It will include
	 * all collections, maps, and arrays by default so includes are ignored except
	 * if you want to include something being excluded by an annotation.  Excludes
	 * are honored.  However, cycles in the target's graph are NOT followed.  This
	 * means some members won't be included in the JSON if they would create a cycle.
	 * Rather than throwing an exception the cycle creating members are simply not
	 * followed. This uses a StringBuilder to output JSON to.
	 *
	 * @param target the instance to serialize to JSON.
	 * @return returns JSON as a String
	 */
	public String deepSerialize(Object target)
	{
		return serialize(target, SerializationType.DEEP, new StringBuilderOutputHandler(new StringBuilder()));
	}

	/**
	 * This performs a deep serialization of the target instance and
	 * passes the generated JSON into the provided Writer.
	 * This can be used to stream JSON back to a browser rather
	 * than wait for it to all complete and then dump it all at
	 * once like the StringBufferOutputHandler and StringBuilderOutputHandler
	 * 
	 * @param target - the instance to serialize to JSON
	 * @param out - Writer
	 */
	public void deepSerialize(Object target, Writer out)
	{
		serialize(target, SerializationType.DEEP, new WriterOutputHandler(out));
	}

	/**
	 * This performs a deep serialization of the target instance and
	 * passes the generated JSON into the provided StringBuilder.
	 * 
	 * @param target - the instance to serialize to JSON
	 * @param out - StringBuilder
	 * @return returns JSON as a String
	 */
	public String deepSerialize(Object target, StringBuilder out)
	{
		return serialize(target, SerializationType.DEEP, new StringBuilderOutputHandler(out));
	}

	/**
	 * This performs a deep serialization of the target instance and
	 * passes the generated JSON into the provided StringBuffer.
	 *
	 * @param target - the instance to serialize to JSON
	 * @param out - StringBuffer
	 * @return returns JSON as a String
	 */
	public String deepSerialize(Object target, StringBuffer out)
	{
		return serialize(target, SerializationType.DEEP, new StringBufferOutputHandler(out));
	}

	/**
	 * This performs a deep serialization of the target instance and
	 * passes the generated JSON into the provided OutputHandler.
	 *
	 * @param target - the instance to serialize to JSON
	 * @param out - OutputHandler to write to
	 * @return returns JSON as a String
	 */
	public String deepSerialize(Object target, OutputHandler out)
	{
		return serialize(target, SerializationType.DEEP, out);
	}

	/**
	 *
	 * @param target - the instance to serialize to JSON
	 * @param serializationType - serialize deep or shallow
	 * @param out - output handler
	 * @return returns JSON as a String
	 */
	protected String serialize(Object target, SerializationType serializationType, OutputHandler out)
	{
		String output= "";
		// initialize context
		JSONContext context= JSONContext.get();
		context.setRootName(rootName);
		context.setPrettyPrint(prettyPrint);
		context.setOut(out);
		context.serializationType(serializationType);
		context.setTypeTransformers(typeTransformerMap);
		context.setPathTransformers(pathTransformerMap);
		context.setPathExpressions(pathExpressions);

		try
		{
			//initiate serialization of target tree
			String rootName= context.getRootName();
			if (rootName == null || rootName.trim().equals(""))
			{
				context.transform(target);
			}
			else
			{
				context.writeOpenObject();
				context.writeName(rootName);
				context.transform(target);
				context.writeCloseObject();
			}

			output= context.getOut().toString();
		}
		finally
		{
			// cleanup context
			JSONContext.cleanup();

		}
		return output;
	}

	// TRANSFORMER CONFIGURATIONS

	/**
	 * This adds a Transformer used to manipulate the value of all the fields you give it.
	 * Fields can be in dot notation just like {@link JSONSerializer#include} and
	 * {@link JSONSerializer#exclude } methods.  However, transform doesn't support wildcards.
	 * Specifying more than one field allows you to add a single instance to multiple fields.
	 * It's there for handiness. :-)
	 *
	 * @param transformer the instance used to transform values
	 * @param fields      the paths to the fields you want to transform.  They can be in dot notation.
	 * @return Hit you back with the JSONSerializer for method chain goodness.
	 */
	public JSONSerializer transform(Transformer transformer, String... fields)
	{
		transformer= new TransformerWrapper(transformer);
		for (String field : fields)
		{
			if (field.length() == 0)
			{
				pathTransformerMap.put(new Path(), transformer);
			}
			else
			{
				pathTransformerMap.put(new Path(field.split("\\.")), transformer);
			}
		}
		return this;
	}

	/**
	 * This adds a Transformer used to manipulate the value of all fields that match the type.
	 *
	 * @param transformer the instance used to transform values
	 * @param types       you want to transform.
	 * @return Hit you back with the JSONSerializer for method chain goodness.
	 */
	public JSONSerializer transform(Transformer transformer, Class... types)
	{

		transformer= new TransformerWrapper(transformer);

		for (Class type : types)
		{
			typeTransformerMap.put(type, transformer);
		}

		return this;
	}

	// INCLUDE/EXCLUDE CONFIGURATION

	protected void addExclude(String field)
	{
		int index= field.lastIndexOf('.');
		if (index > 0)
		{
			PathExpression expression= new PathExpression(field.substring(0, index), true);
			if (!expression.isWildcard())
			{
				pathExpressions.add(expression);
			}
		}
		pathExpressions.add(new PathExpression(field, false));
	}

	protected void addInclude(String field)
	{
		pathExpressions.add(new PathExpression(field, true));
	}

	/**
	 * This takes in a dot expression representing fields
	 * to exclude when serialize method is called.  You
	 * can hand it one or more fields.  Example are: "password",
	 * "bankaccounts.number", "people.socialsecurity", or
	 * "people.medicalHistory".  In exclude method dot notations
	 * will only exclude the final field (i.e. rightmost field).
	 * All the fields to the left of the last field will be included.
	 * In order to exclude the medicalHistory field we have to
	 * include the people field since people would've been excluded
	 * anyway since it's a Collection of Person objects.  The order of
	 * evaluation is the order in which you call the exclude method.
	 * The first call to exclude will be evaluated before other calls to
	 * include or exclude.  The field expressions are evaluated in order
	 * you pass to this method.
	 *
	 * @param fields one or more field expressions to exclude.
	 * @return this instance for method chaining.
	 */
	public JSONSerializer exclude(String... fields)
	{
		for (String field : fields)
		{
			addExclude(field);
		}
		return this;
	}

	/**
	 * This takes in a dot expression representing fields to
	 * include when serialize method is called.  You can hand
	 * it one or more fields.  Examples are: "hobbies",
	 * "hobbies.people", "people.emails", or "character.inventory".
	 * When using dot notation each field between the dots will
	 * be included in the serialization process.  The order of
	 * evaluation is the order in which you call the include method.
	 * The first call to include will be evaluated before other calls to
	 * include or exclude.  The field expressions are evaluated in order
	 * you pass to this method.
	 *
	 * @param fields one or more field expressions to include.
	 * @return this instance for method chaining.
	 */
	public JSONSerializer include(String... fields)
	{
		for (String field : fields)
		{
			addInclude(field);
		}
		return this;
	}

	// INCLUDE/EXCLUDE TEST/DEBUG HOOKS

	/**
	 * Return the fields included in serialization.  These fields will be in dot notation.
	 *
	 * @return A List of dot notation fields included in serialization.
	 */
	public List<PathExpression> getIncludes()
	{
		List<PathExpression> expressions= new ArrayList<PathExpression>();
		for (PathExpression expression : pathExpressions)
		{
			if (expression.isIncluded())
			{
				expressions.add(expression);
			}
		}
		return expressions;
	}

	/**
	 * Return the fields excluded from serialization.  These fields will be in dot notation.
	 *
	 * @return A List of dot notation fields excluded from serialization.
	 */
	public List<PathExpression> getExcludes()
	{
		List<PathExpression> excludes= new ArrayList<PathExpression>();
		for (PathExpression expression : pathExpressions)
		{
			if (!expression.isIncluded())
			{
				excludes.add(expression);
			}
		}
		return excludes;
	}

	/**
	 * Sets the fields included in serialization.  These fields must be in dot notation.
	 * This is just here so that JSONSerializer can be treated like a bean so it will
	 * integrate with Spring or other frameworks.  <strong>This is not ment to be used
	 * in code use include method for that.</strong>
	 *
	 * @param fields the list of fields to be included for serialization.  The fields arg should be a
	 *               list of strings in dot notation.
	 */
	public void setIncludes(List<String> fields)
	{
		for (String field : fields)
		{
			pathExpressions.add(new PathExpression(field, true));
		}
	}

	/**
	 * Sets the fields excluded in serialization.  These fields must be in dot notation.
	 * This is just here so that JSONSerializer can be treated like a bean so it will
	 * integrate with Spring or other frameworks.  <strong>This is not ment to be used
	 * in code use exclude method for that.</strong>
	 *
	 * @param fields the list of fields to be excluded for serialization.  The fields arg should be a
	 *               list of strings in dot notation.
	 */
	public void setExcludes(List<String> fields)
	{
		for (String field : fields)
		{
			addExclude(field);
		}
	}

	public void setSerializingWithUniqueIds(boolean serializingWithUniqueIds)
	{
		JSONContext.get().setSerializingWithUniqueIds(serializingWithUniqueIds);
	}
}
