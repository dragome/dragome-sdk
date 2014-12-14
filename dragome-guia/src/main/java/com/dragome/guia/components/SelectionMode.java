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
package com.dragome.model;

public class SelectionMode
{
	public static SelectionMode MULTIPLE_INTERVAL_SELECTION= new SelectionMode(0);
	public static SelectionMode SINGLE_SELECTION= new SelectionMode(1);
	private int mode;

	public SelectionMode()
	{
	}

	public SelectionMode(int aMode)
	{
		this.mode= aMode;
	}

	public boolean equals(Object obj)
	{
		return ((SelectionMode) obj).getMode() == getMode();
	}

	public int getMode()
	{
		return mode;
	}
	public void setMode(int mode)
	{
		this.mode= mode;
	}
}
