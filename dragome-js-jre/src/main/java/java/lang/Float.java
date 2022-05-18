/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package java.lang;

import java.io.Numbers;
import java.util.NotImplementedMethod;

import com.dragome.commons.javascript.ScriptHelper;

public final class Float extends Number
{
	public static final Class<Float> TYPE= Class.getType("float");

	public static final float POSITIVE_INFINITY= 1.0f / 0.0f;
	public static final float NEGATIVE_INFINITY= -1.0f / 0.0f;
	public static final float NaN= 0.0f / 0.0f;
	public static final float MAX_VALUE= 0x1.fffffeP+127f; // 3.4028235e+38f
	public static final float MIN_NORMAL= 0x1.0p-126f; // 1.17549435E-38f
	public static final float MIN_VALUE= 0x0.000002P-126f; // 1.4e-45f
	public static final int MAX_EXPONENT= 127;
	public static final int MIN_EXPONENT= -126;
	public static final int SIZE= 32;

	private float value= 0;

	public Float()
	{
	}
	/**
	 * Allocates a Float object representing the value argument.
	 */
	public Float(float theValue)
	{
		value= theValue;
	}

	public Float(String aString)
	{//TODO revisar
		this((float) Double.parseDouble(aString));
	}

	public double doubleValue()
	{
		return value;
	}

	public float floatValue()
	{
		return value;
	}

	public int intValue()
	{
		return (int) value;
	}

	public long longValue()
	{
		return (long) value;
	}

	public static float parseFloat(String string) throws NumberFormatException
	{
		return (float) Double.parseDouble(string);
	}

	public static Float valueOf(float f)
	{
		return new Float(f);
	}

	public static Float valueOf(String aFloat)
	{
		return new Float(parseFloat(aFloat));
	}

	public static int floatToIntBits(float value)
	{
		throw new NotImplementedMethod("Float.floatToIntBits");
		//
		//
		//	int result= floatToRawIntBits(value);
		//	// Check for NaN based on values of bit fields, maximum
		//	// exponent and nonzero significand.
		//	if (((result & FloatConsts.EXP_BIT_MASK) == FloatConsts.EXP_BIT_MASK) && (result & FloatConsts.SIGNIF_BIT_MASK) != 0)
		//	    result= 0x7fc00000;
		//	return result;
	}

	public static int compare(float x, float y)
	{
		if (x < y)
			return -1;
		else if (x > y)
			return 1;
		else
			return 0;
	}

	//	public static int compare(float f1, float f2)
	//	{
	//		if (f1 < f2)
	//			return -1; // Neither val is NaN, thisVal is smaller
	//		if (f1 > f2)
	//			return 1; // Neither val is NaN, thisVal is larger
	//
	//		// Cannot use floatToRawIntBits because of possibility of NaNs.
	//		int thisBits= Float.floatToIntBits(f1);
	//		int anotherBits= Float.floatToIntBits(f2);
	//
	//		return (thisBits == anotherBits ? 0 : // Values are equal
	//				(thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
	//						1)); // (0.0, -0.0) or (NaN, !NaN)
	//	}

	public String toString()
	{
		ScriptHelper.put("value", value, this);
		return (String) ScriptHelper.eval("String(value)", this);
	}

	public static String toString(float f)
	{
		return f + "";
	}

	public static int floatToRawIntBits(float value)
	{
		return Numbers.floatToIntBits(value);
	}

	public static boolean isInfinite(float v)
	{
		return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
	}

	public boolean isInfinite()
	{
		return isInfinite(value);
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
	public static boolean isNaN(float v)
	{
		ScriptHelper.put("value", v, null);
		return ScriptHelper.evalBoolean("isNaN(value)", null);
	}
}
