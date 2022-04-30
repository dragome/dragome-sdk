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

import com.dragome.guia.components.interfaces.VisualBorder;
import com.dragome.guia.components.interfaces.VisualBounds;
import com.dragome.guia.components.interfaces.VisualColor;
import com.dragome.guia.components.interfaces.VisualComponent;

public interface Style
{
	public Style invert();
	public abstract VisualBorder getBorder();
	public abstract void setBorder(VisualBorder border);
	public VisualColor getBackgroundColor();
	public VisualColor getForegroundColor();
	public void setBackgroundColor(VisualColor backgroundColor);
	public void setForegroundColor(VisualColor foregroundColor);
	public VisualBounds getBounds();
	public void setBounds(VisualBounds aBounds);
	public void setInverted(boolean isInverted);
	public boolean isInverted();
	public VisualColor getDefaultBackgroundColor();
	public boolean isVisible();
	public void setVisible(boolean isVisible);
	public boolean isEnabled();
	public void setEnabled(boolean isEnabled);
	public String getName();
	public void setName(String name, boolean fireEvent);
	public void setName(String name);
	public abstract void setVisualComponent(VisualComponent visualComponent);
	public abstract VisualComponent getVisualComponent();
	public void addClass(String name);
	public void removeClass(String styleName);
	public boolean hasClass(String styleName);
	boolean isSynchronized();
	void setSynchronized(boolean styleSynchronized);
	void fireStyleChanged();
}
