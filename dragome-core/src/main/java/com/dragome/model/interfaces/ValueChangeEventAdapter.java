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

import java.lang.reflect.Method;

import com.dragome.model.interfaces.GwtEvent.Type;

public class ValueChangeEventAdapter
{

	/**
	 * Handler type.
	 */
	public static Type<ValueChangeHandler<?>> TYPE;

	/**
	     * Fires a value change event on all registered handlers in the handler
	     * manager. If no such handlers exist, this method will do nothing.
	     * 
	     * @param <T> the old value type
	     * @param source the source of the handlers
	     * @param value the value
	     */
	public static <T> void fire(HasValueChangeHandlers<T> source, T value)
	{
		if (TYPE != null)
		{
			final ValueChangeEvent<T> event= new ValueChangeEvent<T>(value);
			try
			{
				Method method= source.getClass().getMethod("fireEvent", GwtEvent.class);
				method.invoke(source, new GwtEvent()
				{
					public Type getAssociatedType()
					{
						return event.getAssociatedType();
					}

					public void dispatch(EventHandler handler)
					{
						event.dispatch((ValueChangeHandler<T>) handler);
					}
				});
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			//	    ((HasHandlers2) source).fireEvent(event);
		}
	}

	/**
	 * Fires value change event if the old value is not equal to the new value.
	 * Use this call rather than making the decision to short circuit yourself for
	 * safe handling of null.
	 * 
	 * @param <T> the old value type
	 * @param source the source of the handlers
	 * @param oldValue the oldValue, may be null
	 * @param newValue the newValue, may be null
	 */
	public static <T> void fireIfNotEqual(HasValueChangeHandlers<T> source, T oldValue, T newValue)
	{
		if (shouldFire(source, oldValue, newValue))
		{
			fire(source, newValue);
		}
	}

	/**
	 * Gets the type associated with this event.
	 * 
	 * @return returns the handler type
	 */
	public static Type<ValueChangeHandler<?>> getType()
	{
		if (TYPE == null)
		{
			TYPE= new Type<ValueChangeHandler<?>>();
		}
		return TYPE;
	}

	/**
	 * Convenience method to allow subtypes to know when they should fire a value
	 * change event in a null-safe manner.
	 * 
	 * @param <T> value type
	 * @param source the source
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return whether the event should be fired
	 */
	protected static <T> boolean shouldFire(HasValueChangeHandlers<T> source, T oldValue, T newValue)
	{
		return TYPE != null && oldValue != newValue && (oldValue == null || !oldValue.equals(newValue));
	}

}
