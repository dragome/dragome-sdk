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

import java.util.NotImplementedMethod;

import com.dragome.commons.javascript.ScriptHelper;

public final class Float extends Number
{
	public static final Class<Float> TYPE= Class.getType("float");

	private float value;

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

    public static Float valueOf(String aFloat) {
        return new Float(parseFloat(aFloat));
    }

	static public boolean isNaN(float v)
	{
		return (v != v);
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

	public static int compare(float f1, float f2)
	{
		if (f1 < f2)
			return -1; // Neither val is NaN, thisVal is smaller
		if (f1 > f2)
			return 1; // Neither val is NaN, thisVal is larger

		// Cannot use floatToRawIntBits because of possibility of NaNs.
		int thisBits= Float.floatToIntBits(f1);
		int anotherBits= Float.floatToIntBits(f2);

		return (thisBits == anotherBits ? 0 : // Values are equal
		        (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
		                1)); // (0.0, -0.0) or (NaN, !NaN)
	}
	
	
	public String toString()
	{
		ScriptHelper.put("value", value, this);
		return (String) ScriptHelper.eval("String(value)", this);
	}
	
}
