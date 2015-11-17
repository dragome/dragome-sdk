/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.forms.bindings.client.form.validation.component;

import java.util.Map;
import java.util.TreeMap;

import com.dragome.forms.bindings.client.form.validation.Severity;
import com.dragome.forms.bindings.client.form.validation.ValidationResult;
import com.dragome.model.interfaces.UIObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 13, 2009
 * Time: 11:39:45 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ValidationStyles
{
	private static final ValidationStyles DEFAULT_INSTANCE= new ValidationStyles()
	{
		protected void removeStyle(UIObject widget, String styleName)
		{
			widget.removeStyleName(styleName);
		}

		protected void addStyle(UIObject widget, String name)
		{
			widget.addStyleName(name);
		}
	};

	private static final ValidationStyles DEFAULT_DEPENDENT_STYLE_INSTANCE= new ValidationStyles()
	{
		protected void removeStyle(UIObject widget, String styleName)
		{
			widget.removeStyleDependentName(styleName);
		}

		protected void addStyle(UIObject widget, String name)
		{
			widget.addStyleDependentName(name);
		}
	};

	public static final String ERROR_STYLE= "validationError";
	public static final String WARNING_STYLE= "validationWarning";
	public static final String INFO_STYLE= "validationInfo";

	private TreeMap<Severity, String> styleNames= new TreeMap<Severity, String>();

	public ValidationStyles()
	{
		initialiseStyles();
	}

	protected void initialiseStyles()
	{
		registerStyleName(Severity.ERROR, ERROR_STYLE);
		registerStyleName(Severity.WARNING, WARNING_STYLE);
		registerStyleName(Severity.INFO, INFO_STYLE);
	}

	public void registerStyleName(Severity severity, String styleName)
	{
		styleNames.put(severity, styleName);
	}

	public void applyStyle(UIObject widget, ValidationResult result)
	{
		clearStyles(widget);

		if (!result.isEmpty())
		{
			applyStyle(widget, getHighestSeverity(result));
		}
	}

	public void applyStyle(UIObject widget, Severity severity)
	{
		clearStyles(widget);

		addStyle(widget, styleNames.get(severity));
	}

	private Severity getHighestSeverity(ValidationResult result)
	{
		for (Map.Entry<Severity, String> entry : styleNames.entrySet())
		{
			if (result.contains(entry.getKey()))
			{
				return entry.getKey();
			}
		}

		return null;
	}

	public void clearStyles(UIObject widget)
	{
		for (String styleName : styleNames.values())
		{
			removeStyle(widget, styleName);
		}
	}

	protected abstract void removeStyle(UIObject widget, String styleName);

	protected abstract void addStyle(UIObject widget, String name);

	/**
	 * Gets the applicator that applies the style using {@link UIObject#addStyleName(String)}.
	 *
	 * @return an applicator that applies the style using {@link UIObject#addStyleName(String)}.
	 */
	public static ValidationStyles defaultInstance()
	{
		return DEFAULT_INSTANCE;
	}

	/**
	 * Gets the applicator that applies the style using {@link UIObject#addStyleDependentName(String)}.
	 *
	 * @return an applicator that applies the style using {@link UIObject#addStyleDependentName(String)}.
	 */
	public static ValidationStyles defaultDependentStyleNameInstance()
	{
		return DEFAULT_DEPENDENT_STYLE_INSTANCE;
	}
}
