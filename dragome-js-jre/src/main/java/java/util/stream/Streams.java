package java.util.stream;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.IntConsumer;

final class Streams
{
	private Streams()
	{
		throw new Error("no instances");
	}

	static final Object NONE= new Object();

	static final class RangeIntSpliterator implements Spliterator.OfInt
	{
		private int from;
		private final int upTo;
		private int last;

		RangeIntSpliterator(int from, int upTo, boolean closed)
		{
			this(from, upTo, closed ? 1 : 0);
		}

		private RangeIntSpliterator(int from, int upTo, int last)
		{
			this.from= from;
			this.upTo= upTo;
			this.last= last;
		}

		public boolean tryAdvance(IntConsumer consumer)
		{
			return false;
		}

		public void forEachRemaining(IntConsumer consumer)
		{
		}

		public long estimateSize()
		{
			return ((long) upTo) - from + last;
		}

		public int characteristics()
		{
			return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.DISTINCT | Spliterator.SORTED;
		}

		public Comparator<? super Integer> getComparator()
		{
			return null;
		}

		public Spliterator.OfInt trySplit()
		{
			long size= estimateSize();
			return size <= 1 ? null : new RangeIntSpliterator(from, from= from + splitPoint(size), 0);
		}

		private static final int BALANCED_SPLIT_THRESHOLD= 1 << 24;
		private static final int RIGHT_BALANCED_SPLIT_RATIO= 1 << 3;

		private int splitPoint(long size)
		{
			int d= (size < BALANCED_SPLIT_THRESHOLD) ? 2 : RIGHT_BALANCED_SPLIT_RATIO;
			return (int) (size / d);
		}
	}
}
