/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.model.interfaces.list;

import com.dragome.model.interfaces.GwtEvent;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 9, 2009
 * Time: 5:49:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListModelChangedEvent<E> extends GwtEvent<ListModelChangedHandler<E>>
{
	private static final Type<ListModelChangedHandler<?>> TYPE= new Type<ListModelChangedHandler<?>>();
	private ListModel<E> sourceModel;

	public ListModelChangedEvent(ListModel<E> sourceModel)
	{
		this.sourceModel= sourceModel;
	}

	public static Type<ListModelChangedHandler<?>> getType()
	{
		return TYPE;
	}

	public static <E> void fire(ListModel<E> source)
	{
		source.fireEvent(new ListModelChangedEvent<E>(source));
	}

	@SuppressWarnings("unchecked")
	public Type<ListModelChangedHandler<E>> getAssociatedType()
	{
		return (Type) TYPE;
	}

	public void dispatch(ListModelChangedHandler<E> handler)
	{
		handler.onListDataChanged(this);
	}

	public ListModel<E> getSourceModel()
	{
		return sourceModel;
	}
}
