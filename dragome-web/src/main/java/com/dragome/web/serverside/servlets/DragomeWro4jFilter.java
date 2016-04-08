/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 *  This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.web.serverside.servlets;

import javax.servlet.FilterConfig;

import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.util.ObjectFactory;

//@WebFilter(urlPatterns= "/dragome/*")
public class DragomeWro4jFilter extends ConfigurableWroFilter
{
	protected WroManagerFactory newWroManagerFactory()
	{
		return new DragomeWroManagerFactory();
	}

	protected ObjectFactory<WroConfiguration> newWroConfigurationFactory(FilterConfig filterConfig)
	{
		return new DragomeWro4jConfigurationObjectFactory();
	}
}
