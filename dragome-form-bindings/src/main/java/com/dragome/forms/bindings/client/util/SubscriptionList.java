package com.dragome.forms.bindings.client.util;

import java.util.ArrayList;
import java.util.List;

import com.dragome.forms.bindings.client.binding.Disposable;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 1, 2010
 * Time: 2:48:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionList<T>
{
	private ArrayList<Subscription<T>> subscriptions= new ArrayList<Subscription<T>>();

	private List<Subscription<T>> copySubscriptions()
	{
		return new ArrayList<Subscription<T>>(subscriptions);
	}

	public void visitSubscribers(Visitor<T> visitor)
	{
		for (Subscription<T> subscription : copySubscriptions())
		{
			visitor.visit(subscription.getTarget());
		}
	}

	public <S extends T> Disposable subscribe(S subscriber)
	{
		Subscription<T> subscription= new Subscription<T>(subscriber);
		subscriptions.add(subscription);
		return subscription;
	}

	private class Subscription<T> implements Disposable
	{
		private T target;

		private Subscription(T target)
		{
			this.target= target;
		}

		public T getTarget()
		{
			return target;
		}

		public void dispose()
		{
			subscriptions.remove(this);
		}
	}

	public interface Visitor<T>
	{
		void visit(T subscriber);
	}
}
