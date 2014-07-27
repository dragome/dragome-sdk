package com.dragome.forms.bindings.client.channel;

import com.dragome.forms.bindings.client.binding.Disposable;
import com.dragome.forms.bindings.client.command.ParameterisedCommand;
import com.dragome.forms.bindings.client.function.Function;
import com.dragome.forms.bindings.client.util.SubscriptionList;
import com.dragome.forms.bindings.client.value.ValueTarget;
import com.dragome.model.interfaces.HasValue;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Feb 23, 2010
 * Time: 2:32:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultChannel<T> extends SubscriptionList<Destination<? super T>> implements Channel<T>
{
	public void publish(final T value)
	{
		visitSubscribers(new Visitor<Destination<? super T>>()
		{
			public void visit(Destination<? super T> subscriber)
			{
				subscriber.receive(value);
			}
		});
	}

	public <S> Publisher<S> getFormattedPublisher(final Function<T, S> function)
	{
		if (function == null)
		{
			throw new NullPointerException("function is null");
		}
		return new Publisher<S>()
		{
			public void publish(S value)
			{
				DefaultChannel.this.publish(function.compute(value));
			}
		};
	}

	public <S> Destination<S> asFormattedDestination(final Function<T, S> function)
	{
		if (function == null)
		{
			throw new NullPointerException("function is null");
		}
		return new Destination<S>()
		{
			public void receive(S value)
			{
				publish(function.compute(value));
			}
		};
	}

	public Destination<T> asDestination()
	{
		return new Destination<T>()
		{
			public void receive(T value)
			{
				publish(value);
			}
		};
	}

	public Disposable sendTo(Destination<? super T> destination)
	{
		if (destination == null)
		{
			throw new NullPointerException("destination is null");
		}
		return subscribe(destination);
	}

	public Disposable sendTo(final Publisher<? super T> publisher)
	{
		return sendTo(new Destination<T>()
		{
			public void receive(T value)
			{
				publisher.publish(value);
			}
		});
	}

	public Disposable sendTo(final ValueTarget<? super T> destination)
	{
		return sendTo(new Destination<T>()
		{
			public void receive(T value)
			{
				destination.setValue(value);
			}
		});
	}

	public Disposable sendTo(final HasValue<? super T> destination)
	{
		return sendTo(new Destination<T>()
		{
			public void receive(T value)
			{
				destination.setValue(value);
			}
		});
	}

	public Disposable sendTo(final ParameterisedCommand<? super T> destination)
	{
		return sendTo(new Destination<T>()
		{
			public void receive(T value)
			{
				destination.execute(value);
			}
		});
	}

}
