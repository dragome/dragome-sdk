/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.model.interfaces;

public class TakesValueEditor<T> implements LeafValueEditor<T>
{
	/**
	 * Returns a new ValueEditor that modifies the given {@link TakesValue} peer
	 * instance.
	 * 
	 * @param peer a {@link TakesValue} instance
	 * @return a TakesValueEditor instance of the same type as its peer
	 */
	public static <T> TakesValueEditor<T> of(TakesValue<T> peer)
	{
		return new TakesValueEditor<T>(peer);
	}

	private final TakesValue<T> peer;

	/**
	 * Returns a new ValueEditor that modifies the given {@link TakesValue} peer
	 * instance.
	 * 
	 * @param peer a {@link TakesValue} instance
	 */
	protected TakesValueEditor(TakesValue<T> peer)
	{
		this.peer= peer;
	}

	public T getValue()
	{
		return peer.getValue();
	}

	public void setValue(T value)
	{
		peer.setValue(value);
	}
}
