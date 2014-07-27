package flexjson.transformer;

public class ValueTransformer extends AbstractTransformer
{
	public void transform(Object object)
	{
		getContext().writeQuoted(object.toString());
	}
}
