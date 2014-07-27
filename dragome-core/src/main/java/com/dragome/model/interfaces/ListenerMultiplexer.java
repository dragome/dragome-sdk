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
// ---------------------------------------------------------------------------
//         Copyright (c) 2010 Oracle Corporation. All rights reserved.      
// ---------------------------------------------------------------------------
//                                                                          
//  FILENAME                                                                
//         ListenerMultiplexer.java
//                                                                          
//  Class
//         net.ar.unfeca.test.ListenerMultiplexer
//                                                                          
//  DESCRIPTION                                              
//                                                                           
//  SOURCE CONTROL                                                          
//         Version : $Revision$                                     
//         Fecha   : $Date$                           
//                                                                          
//  HISTORY                                                                 
//         Sep 7, 2010      fpetrola         Creation
//                                                                          
// ---------------------------------------------------------------------------
package com.dragome.model.interfaces;

import java.util.EventListener;

public interface ListenerMultiplexer<ListenerType extends EventListener>
{
	void add(ListenerType aListener);
	void remove(ListenerType aListener);
}
