package flexjson;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import flexjson.transformer.BasicDateTransformer;
import flexjson.transformer.BooleanTransformer;
import flexjson.transformer.CharacterTransformer;
import flexjson.transformer.ClassTransformer;
import flexjson.transformer.EnumTransformer;
import flexjson.transformer.IterableTransformer;
import flexjson.transformer.MapTransformer;
import flexjson.transformer.NullTransformer;
import flexjson.transformer.NumberTransformer;
import flexjson.transformer.ObjectTransformer;
import flexjson.transformer.StringTransformer;
import flexjson.transformer.Transformer;
import flexjson.transformer.TransformerWrapper;
import flexjson.transformer.TypeTransformerMap;

public class TransformerUtil
{

	private static final TypeTransformerMap defaultTransformers= new TypeTransformerMap();

	static
	{
		// define all standard type transformers
		Transformer transformer= new NullTransformer();
		putTransformer(null, new TransformerWrapper(transformer), false);

		transformer= new ObjectTransformer();
		putTransformer(Object.class, new TransformerWrapper(transformer), true);

		transformer= new ClassTransformer();
		putTransformer(Class.class, new TransformerWrapper(transformer), true);

		transformer= new BooleanTransformer();
		putTransformer(boolean.class, new TransformerWrapper(transformer), false);
		putTransformer(Boolean.class, new TransformerWrapper(transformer), true);

		transformer= new NumberTransformer();
		putTransformer(Number.class, new TransformerWrapper(transformer), true);

		putTransformer(Integer.class, new TransformerWrapper(transformer), true);
		putTransformer(int.class, new TransformerWrapper(transformer), false);

		putTransformer(Long.class, new TransformerWrapper(transformer), true);
		putTransformer(long.class, new TransformerWrapper(transformer), false);

		putTransformer(Double.class, new TransformerWrapper(transformer), true);
		putTransformer(double.class, new TransformerWrapper(transformer), false);

		putTransformer(Float.class, new TransformerWrapper(transformer), true);
		putTransformer(float.class, new TransformerWrapper(transformer), false);

		//        putTransformer(BigDecimal.class, new TransformerWrapper(transformer));
		//        putTransformer(BigInteger.class, new TransformerWrapper(transformer));

		transformer= new StringTransformer();
		putTransformer(String.class, new TransformerWrapper(transformer), true);

		transformer= new CharacterTransformer();
		putTransformer(Character.class, new TransformerWrapper(transformer), true);
		putTransformer(char.class, new TransformerWrapper(transformer), false);

		transformer= new BasicDateTransformer();
		putTransformer(Date.class, new TransformerWrapper(transformer), true);

		transformer= new EnumTransformer();
		putTransformer(Enum.class, new TransformerWrapper(transformer), true);

		transformer= new IterableTransformer();
		putTransformer(Iterable.class, new TransformerWrapper(transformer), true);

		transformer= new MapTransformer();
		putTransformer(Map.class, new TransformerWrapper(transformer), true);

		transformer= new NullTransformer();
		putTransformer(void.class, new TransformerWrapper(transformer), false);

		//        transformer = new ArrayTransformer();
		//        putTransformer(Arrays.class, new TransformerWrapper(transformer));

		//	try
		//	{
		//	    Class hibernateProxy= Class.forName("org.hibernate.proxy.HibernateProxy");
		//	    putTransformer(hibernateProxy, new TransformerWrapper(new HibernateTransformer()), true);
		//	}
		//	catch (ClassNotFoundException ex)
		//	{
		//	    // no hibernate so ignore.
		//	}
		//
		Collections.unmodifiableMap(defaultTransformers);
	}

	private static void putTransformer(Class<?> type, Transformer transformer, boolean find)
	{
		try
		{
			Class<?> type2= find ? Class.forName(type.getName()) : type;
			defaultTransformers.put(type2, transformer);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public static TypeTransformerMap getDefaultTypeTransformers()
	{
		return defaultTransformers;
	}

}
