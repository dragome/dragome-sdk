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

package com.dragome.forms.bindings.client.form.validation;

import com.dragome.model.interfaces.GwtEvent;
import com.dragome.model.interfaces.IndexedValidationResult;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 10:01:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class IndexedValidationEvent extends GwtEvent<IndexedValidationHandler>
{
	private static final Type<IndexedValidationHandler> TYPE= new Type<IndexedValidationHandler>();

	public static Type<IndexedValidationHandler> getType()
	{
		return TYPE;
	}

	public static void fire(HasIndexedValidationHandlers source, IndexedValidationResult validationResult)
	{
		source.fireEvent(new IndexedValidationEvent(validationResult));
	}

	private IndexedValidationResult result;

	public IndexedValidationEvent(IndexedValidationResult result)
	{
		this.result= result;
	}

	public IndexedValidationResult getValidationResult()
	{
		return result;
	}

	public Type<IndexedValidationHandler> getAssociatedType()
	{
		return TYPE;
	}

	public void dispatch(IndexedValidationHandler handler)
	{
		handler.onValidate(this);
	}

}