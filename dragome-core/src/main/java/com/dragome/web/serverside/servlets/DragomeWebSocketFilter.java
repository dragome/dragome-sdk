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
package com.dragome.web.serverside.servlets;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.atmosphere.cpr.AtmosphereServlet;

import com.dragome.web.serverside.debugging.websocket.DragomeWebSocketHandler;

//@WebServlet(initParams= { @WebInitParam(name= "org.atmosphere.cpr.packages", value= "com.dragome.serverside.debugging.websocket"),//
//        @WebInitParam(name= "org.atmosphere.websocket.binaryWrite", value= "false"), // 
//        @WebInitParam(name= "org.atmosphere.websocket.maxTextMessageSize", value= "100000"), // 
//        @WebInitParam(name= "org.atmosphere.websocket.maxBinaryMessageSize", value= "100000") //
//}, loadOnStartup= 0, //
//urlPatterns= DragomeWebSocketHandler.DRAGOME_WEBSOCKET + "/*")
public class DragomeWebSocketFilter extends AtmosphereServlet
{
}
