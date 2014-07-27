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
package com.dragome.serverside.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebFilter(urlPatterns= "/*")
public class DragomeIEFilter implements Filter
{
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
	{
		HttpServletRequest httpServletRequest= (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse= (HttpServletResponse) servletResponse;
		httpServletResponse.setHeader("X-UA-Compatible", "IE=EDGE");

		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy()
	{
	}

	public void init(FilterConfig arg0) throws ServletException
	{
	}
}
