package java.math;

public class BigDecimal extends BigAny<BigDecimal>
{
	public BigDecimal(String aString)
	{
		super(aString);
	}

	public BigDecimal(double doubleValue)
	{
		super(doubleValue);
	}

	public static BigDecimal valueOf(long val)
	{
		return new BigDecimal(val);
	}
}
