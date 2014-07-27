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
/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.proc.out.templates;

class TemplateFile
{

	final String source;
	final String dest;
	final String path;
	final Mode mode;

	TemplateFile(String source)
	{
		this(source, "");
	}

	TemplateFile(String source, Mode mode)
	{
		this(source, "", mode);
	}

	TemplateFile(String source, String path)
	{
		this(source, source, path);
	}

	TemplateFile(String source, String path, Mode mode)
	{
		this(source, source, path, mode);
	}

	TemplateFile(String source, String dest, String path)
	{
		this(source, dest, path, Mode.OVERWRITE);
	}

	TemplateFile(String source, String dest, String path, Mode mode)
	{
		this.source= source;
		this.dest= dest;
		this.path= path;
		this.mode= mode;
	}

	enum Mode
	{

		OVERWRITE, KEEP, BACKUP, ABORT, IGNORE, NEWFILE, DELETE;
	}
}
