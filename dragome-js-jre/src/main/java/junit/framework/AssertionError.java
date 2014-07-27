package junit.framework;

/**
 * Thrown to indicate that an assertion has failed.
 * 
 */
public class AssertionError extends Error
{
	public AssertionError()
	{
		super();
	}

	/**
	 * Constructs an AssertionError with its detail message derived from the specified object,
	 * which is converted to a string as defined in The Java Language Specification, Second Edition, Section 15.18.1.1. 
	 */
	public AssertionError(Object detailMessage)
	{
		super(String.valueOf(detailMessage));
	}

}
