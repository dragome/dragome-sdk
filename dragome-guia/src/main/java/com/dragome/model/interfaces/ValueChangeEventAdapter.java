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
