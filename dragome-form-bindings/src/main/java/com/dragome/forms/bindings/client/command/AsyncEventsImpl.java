package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.binding.Disposable;
import com.dragome.forms.bindings.client.channel.Channel;
import com.dragome.forms.bindings.client.channel.DefaultChannel;
import com.dragome.forms.bindings.client.channel.Destination;
import com.dragome.forms.bindings.client.channel.Publisher;
import com.dragome.forms.bindings.client.util.SubscriptionList;
import com.dragome.forms.bindings.client.value.ValueTarget;
import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * AbstractAsyncEvents provides a the default implementation of {@link com.pietschy.gwt.pectin.client.command.AsyncEvents}.
 */
public class AsyncEventsImpl<R, E> extends AbstractEvents implements AsyncEvents<R, E>
{
	private DefaultChannel<R> resultChannel= new DefaultChannel<R>();
	private DefaultChannel<E> errorChannel= new DefaultChannel<E>();

	protected AsyncEventsImpl()
	{
	}

	protected Channel<R> getResultChannel()
	{
		return resultChannel;
	}

	protected Channel<E> getErrorChannel()
	{
		return errorChannel;
	}

	protected void fireSuccess(final R result)
	{
		resultChannel.publish(result);
		visitAsyncSubscribers(new SubscriptionList.Visitor<AsyncLifeCycleCallback<R, E>>()
		{
			public void visit(AsyncLifeCycleCallback<R, E> subscriber)
			{
				subscriber.onSuccess(result);
			}
		});
	}

	protected void fireError(final E error)
	{
		errorChannel.publish(error);
		visitAsyncSubscribers(new SubscriptionList.Visitor<AsyncLifeCycleCallback<R, E>>()
		{
			public void visit(AsyncLifeCycleCallback<R, E> subscriber)
			{
				subscriber.onError(error);
			}
		});

	}

	public <T> SendToBuilder<T> onSuccessSend(final T value)
	{
		return new SendToBuilderImpl<T>(resultChannel, value);
	}

	public <T> SendToBuilder<T> onErrorSend(final T value)
	{
		return new SendToBuilderImpl<T>(errorChannel, value);
	}

	public Disposable sendResultTo(Destination<? super R> destination)
	{
		return resultChannel.sendTo(destination);
	}

	public Disposable sendResultTo(ValueTarget<? super R> destination)
	{
		return resultChannel.sendTo(destination);
	}

	public Disposable sendResultTo(Publisher<? super R> publisher)
	{
		return resultChannel.sendTo(publisher);
	}

	public Disposable onSuccessInvoke(final Command command)
	{
		if (command == null)
		{
			throw new NullPointerException("command is null");
		}
		return resultChannel.sendTo(new Destination<R>()
		{
			public void receive(R value)
			{
				command.execute();
			}
		});
	}

	public Disposable onSuccessInvoke(final ParameterisedCommand<? super R> command)
	{
		return resultChannel.sendTo(command);
	}

	public Disposable sendErrorTo(Destination<? super E> destination)
	{
		return errorChannel.sendTo(destination);
	}

	public Disposable sendErrorTo(ValueTarget<? super E> destination)
	{
		return errorChannel.sendTo(destination);
	}

	public Disposable sendErrorTo(Publisher<? super E> publisher)
	{
		return errorChannel.sendTo(publisher);
	}

	public Disposable onErrorInvoke(final ParameterisedCommand<? super E> command)
	{
		return errorChannel.sendTo(command);
	}

	public Disposable onErrorInvoke(final Command command)
	{
		if (command == null)
		{
			throw new NullPointerException("command is null");
		}
		return errorChannel.sendTo(new Destination<E>()
		{
			public void receive(E value)
			{
				command.execute();
			}
		});
	}

	public Disposable sendAllEventsTo(AsyncLifeCycleCallback<R, E> callback)
	{
		// we all all our async subscribers as regulars subscribers so they
		// automatically pickup the start and finished events.
		return super.sendAllEventsTo(callback);
	}

	protected void visitAsyncSubscribers(final SubscriptionList.Visitor<AsyncLifeCycleCallback<R, E>> visitor)
	{
		// now we fire async events to those subscribers that implement AsyncLifeCycleCallback
		super.visitSubscribers(new SubscriptionList.Visitor<LifeCycleCallback>()
		{
			public void visit(LifeCycleCallback subscriber)
			{
				if (visitor instanceof AsyncLifeCycleCallback)
				{
					visitor.visit((AsyncLifeCycleCallback<R, E>) subscriber);
				}
			}
		});
	}
}
