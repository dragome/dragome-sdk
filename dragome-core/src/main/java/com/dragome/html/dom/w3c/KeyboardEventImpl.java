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
package com.dragome.html.dom.w3c;

import org.w3c.dom.events.KeyboardEvent;
import org.w3c.dom.views.AbstractView;

public class KeyboardEventImpl extends EventImpl implements KeyboardEvent
{
	private int keyCode;

	public KeyboardEventImpl(String eventType, int keyCode)
	{
		super(eventType);
		this.keyCode= keyCode;
	}

	public int getDetail()
	{
		return 0;
	}

	public AbstractView getView()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void initUIEvent(String arg0, boolean arg1, boolean arg2, AbstractView arg3, int arg4)
	{
		// TODO Auto-generated method stub

	}

	public String getKeyIdentifier()
	{
		return keyCode + "";
	}

	public int getKeyLocation()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getCtrlKey()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getShiftKey()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getAltKey()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getMetaKey()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getModifierState(String keyIdentifierArg)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void initKeyboardEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, String keyIdentifierArg, int keyLocationArg, String modifiersList)
	{
		// TODO Auto-generated method stub

	}

	public void initKeyboardEventNS(String namespaceURI, String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, String keyIdentifierArg, int keyLocationArg, String modifiersList)
	{
		// TODO Auto-generated method stub

	}
}
