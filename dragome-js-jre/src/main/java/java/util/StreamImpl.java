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
package java.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StreamImpl<T, C> implements Stream<T>, Consumer<C>
{
	protected Consumer<C> downstream;
	protected StreamImpl<T, C> topStream;
	private Collection<T> items;

	public StreamImpl()
	{
		topStream= this;
	}
	public StreamImpl(StreamImpl<T, C> upstream)
	{
		topStream= upstream.topStream;
		upstream.downstream= this;
	}
	public StreamImpl(Collection<T> items)
	{
		this();
		this.items= items;
	}
	public Iterator<T> iterator()
	{
		return items.iterator();
	}
	public Spliterator<T> spliterator()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isParallel()
	{
		// TODO Auto-generated method stub
		return false;
	}
	public Stream<T> sequential()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> parallel()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> unordered()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> onClose(Runnable closeHandler)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void close()
	{
		// TODO Auto-generated method stub

	}
	public Stream<T> filter(Predicate<? super T> predicate)
	{
		return new StreamImpl<T, T>((StreamImpl<T, T>) this)
		{
			public void accept(T t)
			{
				if (predicate.test(t))
					downstream.accept(t);
			}
		};
	}
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper)
	{
		return new StreamImpl<R, T>((StreamImpl<R, T>) this)
		{
			public void accept(T t)
			{
				downstream.accept((T) mapper.apply(t));
			}
		};
	}
	public IntStream mapToInt(ToIntFunction<? super T> mapper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public LongStream mapToLong(ToLongFunction<? super T> mapper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> distinct()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> sorted()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> sorted(Comparator<? super T> comparator)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> peek(Consumer<? super T> action)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> limit(long maxSize)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Stream<T> skip(long n)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void forEach(Consumer<? super T> action)
	{
		new StreamImpl<T, T>((StreamImpl<T, T>) this)
		{
			public void accept(T t)
			{
				action.accept(t);
			}
		};

		for (Iterator<T> iterator= topStream.iterator(); iterator.hasNext();)
		{
			T type= (T) iterator.next();
			topStream.accept((C) type);
		}
	}
	public void forEachOrdered(Consumer<? super T> action)
	{
		// TODO Auto-generated method stub

	}
	public Object[] toArray()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public <A> A[] toArray(IntFunction<A[]> generator)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public T reduce(T identity, BinaryOperator<T> accumulator)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Optional<T> reduce(BinaryOperator<T> accumulator)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public <R, A> R collect(Collector<? super T, A, R> collector)
	{
		forEach(v -> collector.accumulator().accept(null, v));
		return collector.finisher().apply(null);
	}
	public Optional<T> min(Comparator<? super T> comparator)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public Optional<T> max(Comparator<? super T> comparator)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public long count()
	{
		int[] counter= new int[1];
		counter[0]= 0;
		forEach(new Consumer<T>()
		{
			public void accept(T t)
			{
				counter[0]++;
			}
		});

		return counter[0];
	}
	public boolean anyMatch(Predicate<? super T> predicate)
	{
		boolean[] found= new boolean[1];

		for (Iterator<T> iterator= topStream.iterator(); iterator.hasNext();)
		{
			T type= (T) iterator.next();

			if (!found[0] && predicate.test(type))
				found[0]= true;
		}

		return found[0];
	}
	public boolean allMatch(Predicate<? super T> predicate)
	{
		// TODO Auto-generated method stub
		return false;
	}
	public boolean noneMatch(Predicate<? super T> predicate)
	{
		// TODO Auto-generated method stub
		return false;
	}
	public Optional<T> findFirst()
	{
		T[] counter= (T[]) new Object[1];
		counter[0]= null;
		forEach(new Consumer<T>()
		{
			public void accept(T t)
			{
				if (counter[0] == null)
					counter[0]= t;
			}
		});

		if (counter[0] == null)
			return Optional.empty();
		else
			return Optional.of(counter[0]);
	}
	public Optional<T> findAny()
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void accept(C t)
	{
		downstream.accept(t);
	}
}
