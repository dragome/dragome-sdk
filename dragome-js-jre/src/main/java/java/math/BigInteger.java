package java.math;

public class BigInteger extends BigAny<BigInteger>
{
	public BigInteger(String aString)
	{
		super(aString);
	}

	public BigInteger(double doubleValue)
	{
		super(doubleValue);
	}

	public static BigInteger valueOf(long val)
	{
		return new BigInteger(val);
	}
}
