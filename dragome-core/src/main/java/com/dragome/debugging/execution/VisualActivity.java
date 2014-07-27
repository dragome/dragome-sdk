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
package com.dragome.debugging.execution;

public interface VisualActivity
{
	public void onCreate();
	public void onStart();
	public void onRestart();
	public void onResume();
	public void onPause();
	public void onStop();
	public void onDestroy();
}
