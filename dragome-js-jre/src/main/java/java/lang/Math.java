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

import com.dragome.commons.javascript.ScriptHelper;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
/**
 * The class Math contains methods for performing basic numeric operations such as the elementary exponential,
 * logarithm, square root, and trigonometric functions.
 *
 *
 */
public final class Math
{

	/**
	 * The double value that is closer than any other to e, the base of the natural logarithms.
	 */
	public static double E;

	/**
	 * The double value that is closer than any other to pi, the ratio of the circumference
	 * of a circle to its diameter.
	 */
	public static double PI;

	static
	{
		E= ScriptHelper.evalDouble("Math.E", null);
		PI= ScriptHelper.evalDouble("Math.PI", null);
	}

	private Math()
	{
	}

	/**
	 * Returns the trigonometric sine of an angle.
	 */
	public static double sin(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.sin(a)", null);
	}

	/**
	 * Returns the trigonometric cosine of an angle.
	 */
	public static double cos(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.cos(a)", null);
	}

	/**
	 * Returns the trigonometric tangent of an angle.
	 */
	public static double tan(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.tan(a)", null);
	}

	/**
	 * Returns the arc sine of a value; the returned angle is in the range -pi/2 through pi/2.
	 */
	public static double asin(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.asin(a)", null);
	}

	/**
	 * Returns the arc cosine of a value; the returned angle is in the range 0.0 through pi.
	 */
	public static double acos(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.acos(a)", null);
	}

	/**
	 * Returns the arc tangent of a value; the returned angle is in the range -pi/2 through pi/2.
	 */
	public static double atan(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.atan(a)", null);
	}

	/**
	 * Converts an angle measured in degrees to an approximately equivalent angle measured in radians.
	 */
	public static double toRadians(double angdeg)
	{
		return angdeg * PI / 180;
	}

	/**
	 * Converts an angle measured in radians to an approximately equivalent angle measured in degrees.
	 */
	public static double toDegrees(double angrad)
	{
		return angrad * 180 / PI;
	}

	/**
	 * Returns the correctly rounded positive square root of a double value.
	 */
	public static double sqrt(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.sqrt(a)", null);
	}

	/**
	 * Returns the smallest (closest to negative infinity) double value that is greater than
	 * or equal to the argument and is equal to a mathematical integer.
	 */
	public static double ceil(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.ceil(a)", null);
	}

	/**
	 * Returns the largest (closest to positive infinity) double value that is less than
	 * or equal to the argument and is equal to a mathematical integer.
	 */
	public static double floor(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.floor(a)", null);
	}

	public static long round(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalInt("Math.round(a)", null);
	}

	/**
	 * Returns the absolute value of an int value.
	 */
	public static int abs(int a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalInt("Math.abs(a)", null);
	}

	/**
	 * Returns the absolute value of a long value.
	 */
	public static long abs(long a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalLong("Math.abs(a)", null);
	}

	/**
	 * Returns the absolute value of a float value.
	 */
	public static float abs(float a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalFloat("Math.abs(a)", null);
	}

	/**
	 * Returns the absolute value of a double value.
	 */
	public static double abs(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.abs(a)", null);
	}

	/**
	 * Returns the greater of two int values.
	 */
	public static int max(int a, int b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalInt("Math.max(a, b)", null);
	}

	/**
	 * Returns the greater of two long values.
	 */
	public static long max(long a, long b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalLong("Math.max(a, b)", null);
	}

	/**
	 * Returns the greater of two float values.
	 */
	public static float max(float a, float b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalFloat("Math.max(a, b)", null);
	}

	/**
	 * Returns the greater of two double values.
	 */
	public static double max(double a, double b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalDouble("Math.max(a, b)", null);
	}

	/**
	 * Returns the smaller of two int values.
	 */
	public static int min(int a, int b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalInt("Math.min(a, b)", null);
	}

	/**
	 * Returns the smaller of two long values.
	 */
	public static long min(long a, long b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalLong("Math.min(a, b)", null);
	}

	/**
	 * Returns the smaller of two float values.
	 */
	public static float min(float a, float b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalFloat("Math.min(a, b)", null);
	}

	/**
	 * Returns the smaller of two double values.
	 */
	public static double min(double a, double b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalDouble("Math.min(a, b)", null);
	}

	/**
	 * Returns a double value with a positive sign, greater than or equal to 0.0 and less than 1.0.
	 */
	public static double random()
	{
		return ScriptHelper.evalDouble("Math.random()", null);
	}

	public static double pow(double a, double b)
	{
		ScriptHelper.put("a", a, null);
		ScriptHelper.put("b", b, null);
		return ScriptHelper.evalDouble("Math.pow(a, b)", null);
	}

	public static double log(double a)
	{
		ScriptHelper.put("a", a, null);
		return ScriptHelper.evalDouble("Math.log(a)", null);
	}

	public static double atan2(double y, double x)
	{
		ScriptHelper.put("y", y, null);
		ScriptHelper.put("x", x, null);
		return ScriptHelper.evalDouble("Math.atan2(y, x)", null);
	}

	public static double exp(double x)
	{
		ScriptHelper.put("x", x, null);
		return ScriptHelper.evalDouble("Math.exp(x)", null);
	}

	public static double signum(double d)
	{
		ScriptHelper.put("x", d, null);
		return ScriptHelper.evalDouble("x?x<0?-1:1:0", null);
	}

	public static double log10(double a)
	{
		ScriptHelper.put("x", a, null);
		return ScriptHelper.evalDouble("(Math.log(x)) / (Math.log(10))", null);
	}

	public static int round(float a)
	{
		return (int) round((double) a);
	}
}
