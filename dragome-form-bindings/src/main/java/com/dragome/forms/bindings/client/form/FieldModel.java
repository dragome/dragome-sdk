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

package com.dragome.forms.bindings.client.form;

/**
 * Basic type for fields that hold a single value.  This type exists to allow the API to cleanly
 * separate (in a code completion context) between the various field types.  I.e. the api can
 * use the type {@link FieldModelBase} if it applies to both {@link FieldModel}s and {@link FormattedFieldModel}s
 * or it can reference each type explicitly.
 */
public interface FieldModel<T> extends FieldModelBase<T>
{
}
