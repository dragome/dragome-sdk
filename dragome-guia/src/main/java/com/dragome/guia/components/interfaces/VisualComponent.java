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
package com.dragome.guia.components.interfaces;

import com.dragome.guia.listeners.ClickListener;
import com.dragome.guia.listeners.DoubleClickListener;
import com.dragome.guia.listeners.KeyUpListener;
import com.dragome.model.interfaces.EventProducer;
import com.dragome.model.interfaces.HasVisible;
import com.dragome.model.interfaces.Style;

public interface VisualComponent extends EventProducer, HasVisible
{
	public String getName();
	public VisualComponent setName(String name);

	public Style getStyle();
	public void setStyle(Style style);

	public VisualPanel getParent();
	public void setParent(VisualPanel parent);

	public void addClickListener(ClickListener clickListener);
	public void addDoubleClickListener(DoubleClickListener doubleClickListener);
	public void addKeyListener(KeyUpListener keyUpListener);
	void focus();
}
