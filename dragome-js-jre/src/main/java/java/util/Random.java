package java.util;

import com.dragome.commons.javascript.ScriptHelper;

public class Random
{

	/**
	 * Creates a new random number generator.
	 */
	public Random()
	{
	}

	/**
	 * Creates a new random number generator using a single long seed.
	 */
	public Random(long seed)
	{
	}

	/**
	 * Sets the seed of this random number generator using a single long seed.
	 */
	public void setSeed(long seed)
	{
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed int value from this random number
	 * generator's sequence. 
	 */
	public int nextInt()
	{
		return nextInt(Integer.MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and
	 * the specified value (exclusive), drawn from this random number generator's sequence.
	 */
	public int nextInt(int n)
	{
		return (int) (Math.abs(nextDouble()) * n);
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed long  value from this random
	 * number generator's sequence.
	 */
	public long nextLong()
	{
		return nextInt();
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed float  value between 0.0 and 1.0
	 * from this random number generator's sequence.
	 */
	public float nextFloat()
	{
		return (float) nextDouble();
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
	 * from this random number generator's sequence.
	 */
	public double nextDouble()
	{
		return ScriptHelper.evalDouble("Math.random()", this);
	}
}
