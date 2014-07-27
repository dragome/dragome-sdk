package flexjson;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import flexjson.factories.ClassLocatorObjectFactory;
import flexjson.factories.ExistingObjectFactory;
import flexjson.locators.StaticClassLocator;

/**
 * <p>
 * JSONDeserializer takes as input a json string and produces a static typed object graph from that
 * json representation.  By default it uses the class property in the json data in order to map the
 * untyped generic json data into a specific Java type.  However, you are limited to only json strings
 * with class information embedded when resolving it into a Java type.  But, for now let's just look at
 * the simplest case of class attributes in your json.  We'll look at how {@link JSONSerializer} and
 * JSONDeserializer pair together out of the box.
 * </p>
 * <p>
 * Say we have a simple object like Hero (see the superhero package under the test and mock).
 * To create a json represenation of Hero we'd do the following:
 * </p>
 *
 * <pre>
 *   Hero harveyBirdman = new Hero("Harvey Birdman", new SecretIdentity("Attorney At Law"), new SecretLair("Sebben & Sebben") );
 *   String jsonHarvey = new JSONSerialize().serialize(hero);
 * </pre>
 * <p>
 * Now to reconsitute Harvey to fight for the law we'd use JSONDeserializer like so:
 * </p>
 * <pre>
 *   Hero hero = new JSONDeserializer<Hero>().deserialize( jsonHarvey );
 * </pre>
 * <p>
 * Pretty easy when all the type information is included with the JSON data.  Now let's look at the more difficult
 * case of how we might reconstitute something missing type info.
 * </p>
 * <p>
 * Let's exclude the class attribute in our json like so:
 * </p>
 *
 * <pre>
 *   String jsonHarvey = new JSONSerialize().exclude("*.class").serialize(hero);
 * </pre>
 * <p>
 * The big trick here is to replace that type information when we instantiate the deserializer.
 * To do that we'll use the {@link flexjson.JSONDeserializer#use(String, Class)} method like so:
 * </p>
 * <pre>
 *   Hero hero = new JSONDeserializer<Hero>().use( null, Hero.class ).deserialize( jsonHarvey );
 * </pre>
 * <p>
 * Like riding a horse with no saddle without our type information.  So what is happening here is we've registered
 * the Hero class to the root of the json.  The {@link flexjson.JSONDeserializer#use(String, Class)} method  uses
 * the object graph path to attach certain classes to those locations.  So, when the deserializer is deserializing
 * it knows where it is in the object graph.  It uses that graph path to look up the java class it should use
 * when reconstituting the object.
 * </p>
 * <p>
 * Notice that in our json you'd see there is no type information in the stream.  However, all we had to do is point
 * the class at the Hero object, and it figured it out.  That's because it uses the target type (in this case Hero)
 * to figure out the other types by inspecting that class.  Meaning notice that we didn't have to tell it about
 * SecretLair or SecretIdentity.  That's because it can figure that out from the Hero class.
 * </p>
 * <p>
 * Pretty cool.  Where this fails is when we starting working with interfaces, abstract classes, and subclasses.
 * Yea our friend polymorphism can be a pain when deserializing.  Why?  Well if you haven't realized by now
 * inspecting the type from our target class won't help us because either it's not a concrete class or we
 * can't tell the subclass by looking at the super class alone.  Next section we're going to stand up on our
 * bare back horse.  Ready?  Let's do it.
 * </p>
 * <p>
 * Before we showed how the {@link flexjson.JSONDeserializer#use(String, Class)} method would allow us to
 * plug in a single class for a given path.  That might work when you know exactly which class you want to
 * instantiate, but when the class type depends on external factors we really need a way to specify several
 * possibilities.  That's where the second version of {@link flexjson.JSONDeserializer#use(String, ClassLocator)}
 * comes into play.  {@link flexjson.ClassLocator} allow you to use a stradegy for finding which java Class
 * you want to attach at a particular object path.
 * </p>
 * <p>
 * {@link flexjson.JSONDeserializer#use(String, ClassLocator)} have access to the intermediate form of
 * the object as a Map.  Given the Map at the object path the ClassLocator figures out which Class
 * Flexjson will bind the parameters into that object.
 * </p>
 * <p>
 * Let's take a look at how this can be done using our Hero class.  All Heros have a list of super powers.
 * These super powers are things like X Ray Vision, Heat Vision, Flight, etc.  Each super power is represented
 * by a subclass of SuperPower.  If we serialize a Hero without class information embedded we'll need a way to
 * figure out which instance to instantiate when we deserialize.  In this example I'm going to use a Transformer
 * during serialization to embed a special type information into the object.  All this transformer does is strip
 * off the package information on the class property.
 * </p>
 * <pre>
 * String json = new JSONSerializer()
 *      .include("powers.class")
 *      .transform( new SimpleTransformer(), "powers.class")
 *      .exclude("*.class")
 *      .serialize( superhero );
 * Hero hero = new JSONDeserializer<Hero>()
 *      .use("powers.class", new PackageClassLocator())
 *      .deserialize( json );
 * </pre>
 * <p>
 *
 * </p>
 * <p>
 * All objects that pass through the deserializer must have a no argument constructor.  The no argument
 * constructor does not have to be public.  That allows you to maintain some encapsulation.  JSONDeserializer
 * will bind parameters using setter methods of the objects instantiated if available.  If a setter method
 * is not available it will using reflection to set the value directly into the field.  You can use setter
 * methods transform the any data from json into the object structure you want.  That way json structure
 * can be different from your Java object structure.  The works very much in the same way getters do for
 * the {@link flexjson.JSONSerializer}.
 * </p>
 * <p>
 * Collections and Maps have changed the path structure in order to specify concrete classes for both
 * the Collection implementation and the contained values.  Normally you would use generics to specify
 * the concrete class to load.  However, if you're contained class is an interface or abstract class
 * then you'll need to define those concrete classes using paths.  To specify the concrete class for
 * a Collection use the path to the collection.  To specify the contained instance's concrete class
 * append "values" onto the path.  For example, if your collection path is "person.friends" you can
 * specify the collection type using:
 * </p>
 * <pre>
 * new JSONDeserializer().use("person.friends", ArrayList.class).use("person.friends.values", Frienemies.class)
 * </pre>
 * <p>
 * Notice that append "values" onto the "person.friends" to specify the class to use inside the
 * Collection.  Maps have both keys and values within them.  For Maps you can specify those by
 * appending "keys" and "values" to the path.
 * </p>
 * <p>
 * Now onto the advanced topics of the deserializer.  {@link flexjson.ObjectFactory} interface is the
 * underpinnings of the deserializer.  All object creation is controlled by ObjectFactories.  By default
 * there are many ObjectFactories registered to handle all of the default types supported.  However, you
 * can add your own implementations to handle specialized formats.  For example, say you've encoded your
 * Dates using yyyy.MM.dd.  If you want to read these into java.util.Date objects you can register a
 * {@link flexjson.transformer.DateTransformer} to deserialize dates into Date objects.
 * </p>
 */
public class JSONDeserializer<T>
{

	private Map<Class, ObjectFactory> typeFactories= new HashMap<Class, ObjectFactory>();
	private Map<Path, ObjectFactory> pathFactories= new HashMap<Path, ObjectFactory>();
	public static ObjectBinder lastObjectBinder;

	public JSONDeserializer()
	{
	}

	/**
	 * Deserialize the given json formatted input into a Java object.
	 *
	 * @param input a json formatted string.
	 * @return an Java instance deserialized from the json input.
	 */
	public T deserialize(String input)
	{
		ObjectBinder binder= createObjectBinder();
		return (T) binder.bind(new JSONTokener(input).nextValue());
	}

	/**
	 * Same as {@link #deserialize(String)}, but uses an instance of
	 * java.io.Reader as json input.
	 *
	 * @param input the stream where the json input is coming from.
	 * @return an Java instance deserialized from the java.io.Reader's input.
	 */
	public T deserialize(Reader input)
	{
		ObjectBinder binder= createObjectBinder();
		return (T) binder.bind(new JSONTokener(input).nextValue());
	}

	/**
	 * Deserialize the given json input, and use the given Class as
	 * the type of the initial object to deserialize into.  This object
	 * must implement a no-arg constructor.
	 *
	 * @param input a json formatted string.
	 * @param root a Class used to create the initial object.
	 * @return the object created from the given json input.
	 */
	public T deserialize(String input, Class root)
	{
		ObjectBinder binder= createObjectBinder();
		return (T) binder.bind(new JSONTokener(input).nextValue(), root);
	}

	/**
	 * Same as {@link #deserialize(java.io.Reader, Class)}, but uses an instance of
	 * java.io.Reader as json input.
	 *
	 * @param input the stream where the json input is coming from.
	 * @param root a Class used to create the initial object.
	 * @return an Java instance deserialized from the java.io.Reader's input.
	 */
	public T deserialize(Reader input, Class root)
	{
		ObjectBinder binder= createObjectBinder();
		return (T) binder.bind(new JSONTokener(input).nextValue(), root);
	}

	/**
	 * Deserialize the given json input, and use the given ObjectFactory to
	 * create the initial object to deserialize into.
	 *
	 * @param input a json formatted string.
	 * @param factory an ObjectFactory used to create the initial object.
	 * @return the object created from the given json input.
	 */
	public T deserialize(String input, ObjectFactory factory)
	{
		use((String) null, factory);
		ObjectBinder binder= createObjectBinder();
		return (T) binder.bind(new JSONTokener(input).nextValue());
	}

	/**
	 * Same as {@link #deserialize(String, ObjectFactory)}, but uses an instance of
	 * java.io.Reader as json input.
	 *
	 * @param input the stream where the json input is coming from.
	 * @param factory an ObjectFactory used to create the initial object.
	 * @return an Java instance deserialized from the java.io.Reader's input.
	 */
	public T deserialize(Reader input, ObjectFactory factory)
	{
		use((String) null, factory);
		ObjectBinder binder= createObjectBinder();
		return (T) binder.bind(new JSONTokener(input).nextValue());
	}

	/**
	 * Deserialize the given input into the existing object target.
	 * Values in the json input will overwrite values in the
	 * target object.  This means if a value is included in json
	 * a new object will be created and set into the existing object. 
	 *
	 * @param input a json formatted string.
	 * @param target an instance to set values into from the json string.
	 * @return will return a reference to target.
	 */
	public T deserializeInto(String input, T target)
	{
		return deserialize(input, new ExistingObjectFactory(target));
	}

	/**
	 * Same as {@link #deserializeInto(String, Object)}, but uses an instance of
	 * java.io.Reader as json input.
	 *
	 * @param input the stream where the json input is coming from.
	 * @param target an instance to set values into from the json string.
	 * @return will return a reference to target.
	 */
	public T deserializeInto(Reader input, T target)
	{
		return deserialize(input, new ExistingObjectFactory(target));
	}

	public JSONDeserializer<T> use(String path, ClassLocator locator)
	{
		pathFactories.put(Path.parse(path), new ClassLocatorObjectFactory(locator));
		return this;
	}

	public JSONDeserializer<T> use(String path, Class clazz)
	{
		return use(path, new StaticClassLocator(clazz));
	}

	public JSONDeserializer<T> use(Class clazz, ObjectFactory factory)
	{
		typeFactories.put(clazz, factory);
		return this;
	}

	public JSONDeserializer<T> use(String path, ObjectFactory factory)
	{
		pathFactories.put(Path.parse(path), factory);
		return this;
	}

	public JSONDeserializer<T> use(ObjectFactory factory, String... paths)
	{
		for (String p : paths)
		{
			use(p, factory);
		}
		return this;
	}

	private ObjectBinder createObjectBinder()
	{
		ObjectBinder binder= new ObjectBinder();
		lastObjectBinder= binder;
		for (Object clazz : typeFactories.keySet())
		{
			binder.use(clazz, typeFactories.get(clazz));
		}
		for (Path p : pathFactories.keySet())
		{
			binder.use(p, pathFactories.get(p));
		}
		return binder;
	}

}
