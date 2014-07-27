package com.dragome.forms.bindings.client.channel;

/**
 * Represents a Channel destination.
 * @see Channel#sendTo(Destination)
 */
public interface Destination<T>
{
	public void receive(T value);
}
