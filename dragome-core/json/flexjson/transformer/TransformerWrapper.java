package flexjson.transformer;

/**
 * This class quietly wraps all transformers so that FlexJSON
 * can perform certain functionality consistently across all
 * transformers.
 */
public class TransformerWrapper extends AbstractTransformer
{

	protected Transformer transformer;

	public Transformer getTransformer()
	{
		return transformer;
	}

	public void setTransformer(Transformer transformer)
	{
		this.transformer= transformer;
	}

	protected Boolean isInterceptorTransformer= Boolean.FALSE;

	public TransformerWrapper(Transformer transformer)
	{
		this.transformer= transformer;
	}

	public void transform(Object object)
	{

		// push object onto stack so object has reference before starting
		getContext().getObjectStack().push(object);

		this.transformer.transform(object);

		// Call FlexJSON interceptors afterTranform last
		getContext().getObjectStack().pop();

	}

	@Override
	public Boolean isInline()
	{
		return transformer instanceof Inline && ((Inline) transformer).isInline();
	}

}
