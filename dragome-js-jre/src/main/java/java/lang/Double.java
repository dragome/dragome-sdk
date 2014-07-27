/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package java.lang;

import javascript.Utils;

import com.dragome.commons.javascript.ScriptHelper;

/*
 * Copyright (c) 2006 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public final class Double extends Number implements Comparable<Double>
{
	public static final double POSITIVE_INFINITY= 1.0 / 0.0;

	/**
	 * A constant holding the negative infinity of type
	 * <code>double</code>. It is equal to the value returned by
	 * <code>Double.longBitsToDouble(0xfff0000000000000L)</code>.
	 */
	public static final double NEGATIVE_INFINITY= -1.0 / 0.0;

	// 2^512, 2^-512
	private static final double POWER_512= 1.3407807929942597E154;
	private static final double POWER_MINUS_512= 7.458340731200207E-155;
	// 2^256, 2^-256
	private static final double POWER_256= 1.157920892373162E77;
	private static final double POWER_MINUS_256= 8.636168555094445E-78;
	// 2^128, 2^-128
	private static final double POWER_128= 3.4028236692093846E38;
	private static final double POWER_MINUS_128= 2.9387358770557188E-39;
	// 2^64, 2^-64
	private static final double POWER_64= 18446744073709551616.0;
	private static final double POWER_MINUS_64= 5.421010862427522E-20;
	// 2^52, 2^-52
	private static final double POWER_52= 4503599627370496.0;
	private static final double POWER_MINUS_52= 2.220446049250313E-16;
	// 2^32, 2^-32
	private static final double POWER_32= 4294967296.0;
	private static final double POWER_MINUS_32= 2.3283064365386963E-10;
	// 2^31
	private static final double POWER_31= 2147483648.0;
	// 2^20, 2^-20
	private static final double POWER_20= 1048576.0;
	private static final double POWER_MINUS_20= 9.5367431640625E-7;
	// 2^16, 2^-16
	private static final double POWER_16= 65536.0;
	private static final double POWER_MINUS_16= 0.0000152587890625;
	// 2^8, 2^-8
	private static final double POWER_8= 256.0;
	private static final double POWER_MINUS_8= 0.00390625;
	// 2^4, 2^-4
	private static final double POWER_4= 16.0;
	private static final double POWER_MINUS_4= 0.0625;
	// 2^2, 2^-2
	private static final double POWER_2= 4.0;
	private static final double POWER_MINUS_2= 0.25;
	// 2^1, 2^-1
	private static final double POWER_1= 2.0;
	private static final double POWER_MINUS_1= 0.5;
	// 2^-1022 (smallest double non-denorm)
	private static final double POWER_MINUS_1022= 2.2250738585072014E-308;

	private static final double[] powers= { POWER_512, POWER_256, POWER_128, POWER_64, POWER_32, POWER_16, POWER_8, POWER_4, POWER_2, POWER_1 };

	private static final double[] invPowers= { POWER_MINUS_512, POWER_MINUS_256, POWER_MINUS_128, POWER_MINUS_64, POWER_MINUS_32, POWER_MINUS_16, POWER_MINUS_8, POWER_MINUS_4, POWER_MINUS_2, POWER_MINUS_1 };

	public static final double NaN= 0d / 0d;

	public static final Class<Double> TYPE= Class.getType("double");

	private double value;

	/**
	 * Constructs a newly allocated Double object that represents the primitive double argument.
	 */
	public Double(double d)
	{
		value= d;
	}

	/**
	 * Constructs a newly allocated Double object that represents the floating-point value of type double represented by the string.    * @param s
	 */
	public Double(String s)
	{
		value= parseDouble(s);
	}

	/**
	 * Compares this object to the specified object.
	 */
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Double))
			return false;
		return ((Double) obj).value == value;
	}

	/**
	 * Compares the two specified double values.
	 */
	public int compareTo(Double o)
	{
		if (!(o instanceof Double))
			throw new ClassCastException();
		Double d= (Double) o;
		if (isNaN() || d.isNaN())
		{
			if (isNaN() && d.isNaN())
				return 0;
			if (isNaN())
				return 1;
			return -1;
		}
		return Utils.cmp(value, d.doubleValue(), 0);
	}

	public double doubleValue()
	{
		return value;
	}

	public float floatValue()
	{
		return (float) value;
	}

	public int intValue()
	{
		return (int) value;
	}

	public long longValue()
	{
		return (long) value;
	}

	/**
	 * Returns true if this Double value is a Not-a-Number (NaN), false otherwise.
	 */
	public boolean isNaN()
	{
		return isNaN(value);
	}

	/**
	 * Returns true if the specified number is a Not-a-Number (NaN) value, false otherwise.
	 */
	public static boolean isNaN(double v)
	{
		ScriptHelper.put("value", v, null);
		return ScriptHelper.evalBoolean("isNaN(value)", null);
	}

	/**
	 * Returns a new double initialized to the value represented by the specified String, as performed by the valueOf method of class Double.
	 */
	public static double parseDouble(String s) throws NumberFormatException
	{
		if (s == null)
			throw new NullPointerException();
		s= s.trim();
		if (s.matches("(\\+|\\-)?NaN"))
			return NaN;

		if (!s.matches("(\\+|\\-)?\\d+(\\.\\d+)?"))
			throw new NumberFormatException("Invalid double: " + s);

		ScriptHelper.put("s", s, null);
		double d= ScriptHelper.evalDouble("parseFloat(s)", null);
		if (isNaN(d))
		{
			throw new NumberFormatException("Not a parsable double: " + s);
		}
		return d;
	}

	/**
	 * Returns an Double object holding the specified value. Calls to this
	 * method may be generated by the autoboxing feature.
	 */
	public static Double valueOf(double value)
	{
		return new Double(value);
	}

	/**
	 * Returns a Double object holding the double value represented by the argument string s.
	 */
	public static Double valueOf(String s) throws NumberFormatException
	{
		return new Double(parseDouble(s));
	}

	/**
	 * Returns a string representation of this Double object.
	 */
	public String toString()
	{
		ScriptHelper.put("value", value, this);
		return (String) ScriptHelper.eval("String(value)", this);
	}

	//    public static long doubleToLongBits(double value)
	//    {
	//	long result= doubleToRawLongBits(value);
	//	// Check for NaN based on values of bit fields, maximum
	//	// exponent and nonzero significand.
	//	if (((result & DoubleConsts.EXP_BIT_MASK) == DoubleConsts.EXP_BIT_MASK) && (result & DoubleConsts.SIGNIF_BIT_MASK) != 0L)
	//	    result= 0x7ff8000000000000L;
	//	return result;
	//    }

	public static double longBitsToDouble(long bits)
	{
		long ihi= (long) (bits >> 32);
		long ilo= (long) (bits & 0xffffffffL);
		if (ihi < 0)
		{
			ihi+= 0x100000000L;
		}
		if (ilo < 0)
		{
			ilo+= 0x100000000L;
		}

		boolean negative= (ihi & 0x80000000) != 0;
		int exp= (int) ((ihi >> 20) & 0x7ff);
		ihi&= 0xfffff; // remove sign bit and exponent

		if (exp == 0x0)
		{
			double d= (ihi * POWER_MINUS_20) + (ilo * POWER_MINUS_52);
			d*= POWER_MINUS_1022;
			return negative ? (d == 0.0 ? -0.0 : -d) : d;
		}
		else if (exp == 0x7ff)
		{
			if (ihi == 0 && ilo == 0)
			{
				return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			}
			else
			{
				return Double.NaN;
			}
		}

		// Normalize exponent
		exp-= 1023;

		double d= 1.0 + (ihi * POWER_MINUS_20) + (ilo * POWER_MINUS_52);
		if (exp > 0)
		{
			int bit= 512;
			for (int i= 0; i < 10; i++, bit>>= 1)
			{
				if (exp >= bit)
				{
					d*= powers[i];
					exp-= bit;
				}
			}
		}
		else if (exp < 0)
		{
			while (exp < 0)
			{
				int bit= 512;
				for (int i= 0; i < 10; i++, bit>>= 1)
				{
					if (exp <= -bit)
					{
						d*= invPowers[i];
						exp+= bit;
					}
				}
			}
		}
		return negative ? -d : d;
	}

	//    public static double longBitsToDouble(long bits)
	//    {
	//	System.out.println("longBitsToDouble:"+bits);
	//	return 0.0;
	////	throw new NotImplementedMethod("Double.longBitsToDouble");
	//    }

	public static long doubleToLongBits(final double v)
	{
		if (Double.isNaN(v))
		{
			// IEEE754, NaN exponent bits all 1s, and mantissa is	non-zero
			return 0x0FFFl << 51;
		}

		long sign= (v < 0 ? 0x1l << 63 : 0);
		long exponent= 0;

		double absV= Math.abs(v);
		// IEEE754 infinite numbers, exponent all 1s, mantissa is 0
		if (Double.isInfinite(v))
		{
			exponent= 0x07FFl << 52;
		}
		else
		{
			if (absV == 0.0)
			{
				// IEEE754, exponent is 0, mantissa is zero
				// we don't handle negative zero at the moment, it istreated as
				// positive zero
				exponent= 0l;
			}
			else
			{
				// get an approximation to the exponent
				int guess= (int) Math.floor(Math.log(absV) / Math.log(2));
				// force it to -1023, 1023 interval (<= -1023 =	denorm/zero)
				guess= Math.max(-1023, Math.min(guess, 1023));

				// divide away exponent guess
				double exp= Math.pow(2, guess);
				absV= absV / exp;

				// while the number is still bigger than a normalized	number
				// increment exponent guess
				while (absV > 2.0)
				{
					guess++;
					absV/= 2.0;
				}
				// if the number is smaller than a normalized number
				// decrement exponent
				while (absV < 1 && guess > 1024)
				{
					guess--;
					absV*= 2;
				}
				exponent= (guess + 1023l) << 52;
			}
		}
		// if denorm
		if (exponent <= 0)
		{
			absV/= 2;
		}

		// the input value has now been stripped of its exponent
		// and is in the range [0,2), we strip off the leading	decimal
		// and use the remainer as a percentage of the significand	value (2^52)
		long mantissa= (long) ((absV % 1) * Math.pow(2, 52));
		return sign | exponent | (mantissa & 0xfffffffffffffl);
	}

	static public boolean isInfinite(double v)
	{
		return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
	}

	public static String toString(double d)
	{
		return d + "";
	}

	public static int compare(double d1, double d2)
	{
		if (d1 < d2)
			return -1; // Neither val is NaN, thisVal is smaller
		if (d1 > d2)
			return 1; // Neither val is NaN, thisVal is larger

		// Cannot use doubleToRawLongBits because of possibility of NaNs.
		long thisBits= Double.doubleToLongBits(d1);
		long anotherBits= Double.doubleToLongBits(d2);

		return (thisBits == anotherBits ? 0 : // Values are equal
		        (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
		                1)); // (0.0, -0.0) or (NaN, !NaN)
	}
	
	public static int hashCode(double value)
	{
		return hashCode((long)value);
	}
}
