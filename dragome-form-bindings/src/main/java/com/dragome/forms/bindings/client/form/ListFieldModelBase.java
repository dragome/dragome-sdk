/*
 * Copyright 2010 Andrew Pietsch
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

import com.dragome.model.interfaces.list.MutableListModel;

/**
 * ListFieldModelBase provides a base type for {@link ListFieldModel} and {@link FormattedListFieldModel}.  This allows
 * plugins that aren't interested in formatted fields to treat both the same way while other plugins
 * can safely distinguish between the two since neither is an instance of the other.
 */
public interface ListFieldModelBase<T> extends Field<T>, MutableListModel<T>
{
}