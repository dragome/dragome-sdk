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
package java.math;

public class BigAny<T extends BigAny> extends Number implements Comparable<T>
{
	public final static int ROUND_UP= 0;
	public final static int ROUND_DOWN= 1;
	public final static int ROUND_CEILING= 2;
	public final static int ROUND_FLOOR= 3;
	public final static int ROUND_HALF_UP= 4;
	public final static int ROUND_HALF_DOWN= 5;
	public final static int ROUND_HALF_EVEN= 6;
	public final static int ROUND_UNNECESSARY= 7;

	protected double doubleValue;

	public BigAny(String aString)
	{
	}

	public BigAny(double doubleValue)
	{
		this.doubleValue= doubleValue;
	}

	public static BigAny valueOf(long val)
	{
		return new BigAny(val);
	}

	public long longValue()
	{
		return (long) doubleValue;
	}

	public int compareTo(T another)
	{
		return (int) (another.doubleValue - doubleValue);
	}

	public double doubleValue()
	{
		return doubleValue;
	}

	public float floatValue()
	{
		return (float) doubleValue;
	}

	public int intValue()
	{
		return (int) doubleValue;
	}
}
