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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DragomeResourcesServlet extends GetPostServlet
{
	private static Logger LOGGER= Logger.getLogger(DragomeResourcesServlet.class.getName());

	Map<String, String> typesTable;

	protected void doService(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException, IOException
	{
		BufferedInputStream bis= null;
		BufferedOutputStream bos= null;
		ServletOutputStream outputStream= null;

		try
		{
			String resourcePath= getResourcePath(aRequest);

			URL resource= DragomeResourcesServlet.class.getResource(resourcePath);
			//			System.out.println("resource:" + resourcePath);
			if (resource != null)
			{
				URLConnection urlConnection= resource.openConnection();
				int length= urlConnection.getContentLength();
				long lastModified= urlConnection.getLastModified();

				long ifModifiedSince= aRequest.getDateHeader("If-Modified-Since");
				if (ifModifiedSince >= (lastModified / 1000 * 1000))
				{
					aResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				}
				else
				{
					String theExtension= resourcePath.substring(resourcePath.lastIndexOf(".") + 1);
					aResponse.setContentType(getMimeType(theExtension));
					aResponse.setContentLength(length);

					bis= new BufferedInputStream(urlConnection.getInputStream());
					bos= new BufferedOutputStream(outputStream= aResponse.getOutputStream());

					aResponse.setDateHeader("Last-Modified", lastModified / 1000 * 1000);
					//		aResponse.setDateHeader("Expires", System.currentTimeMillis() + 5184000 * 1000);
					//		aResponse.setHeader("Cache-Control", "max-age=0");

					copyStreams(bis, bos, 4096);
				}
			}
			else
				aResponse.sendError(404);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Cannot load resource", e);
			aResponse.sendError(404);
		}
		finally
		{
			if (bis != null)
				bis.close();

			if (bos != null)
				bos.close();

			if (outputStream != null)
			{
				outputStream.flush();
				outputStream.close();
			}
		}
	}
	private String getResourcePath(HttpServletRequest aRequest)
	{
		String servletPath= aRequest.getServletPath() + aRequest.getPathInfo();
		servletPath= servletPath.substring("/dragome-resources".length());
		return servletPath;
	}
	private String getMimeType(String theExtension)
	{
		return (String) typesTable.get(theExtension.trim().toLowerCase());
	}

	public static void copyStreams(final InputStream input, final OutputStream output, final int bufferSize) throws IOException
	{
		int n= 0;
		final byte[] buffer= new byte[bufferSize];
		while (-1 != (n= input.read(buffer)))
			output.write(buffer, 0, n);
	}

	public void init() throws ServletException
	{
		String realPath= getServletContext().getRealPath("/");
		System.setProperty("cache-dir", realPath);

		typesTable= new HashMap<String, String>();

		typesTable.put("js", "text/javascript");
		typesTable.put("css", "text/css");
		typesTable.put("gif", "image/gif");
		typesTable.put("jpg", "image/jpeg");
		typesTable.put("jpeg", "image/jpeg");
		typesTable.put("jpe", "image/jpeg");
		typesTable.put("png", "image/x-png");
	}

	protected long getLastModified(HttpServletRequest aRequest)
	{
		String resourcePath= getResourcePath(aRequest);
		URL resource= DragomeResourcesServlet.class.getResource(resourcePath);
		if (resource != null)
		{
			try
			{
				URLConnection urlConnection= resource.openConnection();
				long lastModified= urlConnection.getLastModified();
				return lastModified;
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
			return super.getLastModified(aRequest);
	}
}
