package java.util.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Collectors
{
	private static <I, R> Function<I, R> castingIdentity()
	{
		return i -> (R) i;
	}

	static class CollectorImpl<T, A, R> implements Collector<T, A, R>
	{
		private final Supplier<A> supplier;
		private final BiConsumer<A, T> accumulator;
		private final BinaryOperator<A> combiner;
		private final Function<A, R> finisher;
		private final Set<Characteristics> characteristics;

		CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher, Set<Characteristics> characteristics)
		{
			this.supplier= supplier;
			this.accumulator= accumulator;
			this.combiner= combiner;
			this.finisher= finisher;
			this.characteristics= characteristics;
		}

		CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Set<Characteristics> characteristics)
		{
			this(supplier, accumulator, combiner, castingIdentity(), characteristics);
		}

		public BiConsumer<A, T> accumulator()
		{
			return accumulator;
		}

		public Supplier<A> supplier()
		{
			return supplier;
		}

		public BinaryOperator<A> combiner()
		{
			return combiner;
		}

		public Function<A, R> finisher()
		{
			return finisher;
		}

		public Set<Characteristics> characteristics()
		{
			return characteristics;
		}
	}

	public static <T> Collector<T, ?, List<T>> toList()
	{
		return new Collector<T, Object, List<T>>()
		{
			List<Object> result= new ArrayList<Object>();

			public Supplier<Object> supplier()
			{
				return null;
			}

			public BiConsumer<Object, T> accumulator()
			{
				return (o, t) -> result.add(t);
			}

			public BinaryOperator<Object> combiner()
			{
				return null;
			}

			public Function<Object, List<T>> finisher()
			{
				return o -> (List) result;
			}

			public Set<Characteristics> characteristics()
			{
				return null;
			}
		};
	}
}
