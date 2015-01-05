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
package com.dragome.html.dom;

public class DragomeJsException extends RuntimeException
{
	private String message;
	private Exception originalException;

	public DragomeJsException()
	{
	}

	public Exception getOriginalException()
	{
		return originalException;
	}

	public void setOriginalException(Exception originalException)
	{
		this.originalException= originalException;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message= message;
	}

	public DragomeJsException(Exception cause, String message)
	{
		originalException= cause;
		this.message= message;
	}

}
