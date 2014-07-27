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
package com.dragome.model.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Editor<T>
{
	/**
	 * Tells the Editor framework to ignore an Editor accessor.
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value= { ElementType.FIELD, ElementType.METHOD })
	public @interface Ignore
	{
	}

	/**
	 * Maps a composite Editor's component Editors into the data-model.
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value= { ElementType.FIELD, ElementType.METHOD })
	public @interface Path
	{
		String value();
	}
}
