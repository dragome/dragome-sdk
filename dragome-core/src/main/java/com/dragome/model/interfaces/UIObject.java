/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dragome.model.interfaces;

import org.w3c.dom.Element;

/**
 * The superclass for all user-interface objects. It simply wraps a DOM element,
 * and cannot receive events. Most interesting user-interface classes derive
 * from {@link com.google.gwt.user.client.ui.Widget}.
 * 
 * <h3>Styling With CSS</h3>
 * <p>
 * All <code>UIObject</code> objects can be styled using CSS. Style names that
 * are specified programmatically in Java source are implicitly associated with
 * CSS style rules. In terms of HTML and CSS, a GWT style name is the element's
 * CSS "class". By convention, GWT style names are of the form
 * <code>[project]-[widget]</code>.
 * </p>
 * 
 * <p>
 * For example, the {@link Button} widget has the style name
 * <code>gwt-Button</code>, meaning that within the <code>Button</code>
 * constructor, the following call occurs:
 * 
 * <pre class="code">
 * setStyleName("gwt-Button");</pre>
 * 
 * A corresponding CSS style rule can then be written as follows:
 * 
 * <pre class="code">
 * // Example of how you might choose to style a Button widget 
 * .gwt-Button {
 *   background-color: yellow;
 *   color: black;
 *   font-size: 24pt;
 * }</pre>
 * 
 * Note the dot prefix in the CSS style rule. This syntax is called a <a
 * href="http://www.w3.org/TR/REC-CSS2/selector.html#class-html">CSS class
 * selector</a>.
 * </p>
 * 
 * <h3>Style Name Specifics</h3>
 * <p>
 * Every <code>UIObject</code> has a <i>primary style name</i> that identifies
 * the key CSS style rule that should always be applied to it. Use
 * {@link #setStylePrimaryName(String)} to specify an object's primary style
 * name. In most cases, the primary style name is set in a widget's constructor
 * and never changes again during execution. In the case that no primary style
 * name is specified, it defaults to the first style name that is added.
 * </p>
 * 
 * <p>
 * More complex styling behavior can be achieved by manipulating an object's
 * <i>secondary style names</i>. Secondary style names can be added and removed
 * using {@link #addStyleName(String)}, {@link #removeStyleName(String)}, or
 * {@link #setStyleName(String, boolean)}. The purpose of secondary style names
 * is to associate a variety of CSS style rules over time as an object
 * progresses through different visual states.
 * </p>
 * 
 * <p>
 * There is an important special formulation of secondary style names called
 * <i>dependent style names</i>. A dependent style name is a secondary style
 * name prefixed with the primary style name of the widget itself. See
 * {@link #addStyleName(String)} for details.
 * </p>
 * 
 * <h3>Use in UiBinder Templates</h3>
 * <p>
 * Setter methods that follow JavaBean property conventions are exposed as
 * attributes in {@link com.google.gwt.uibinder.client.UiBinder UiBinder}
 * templates. For example, because UiObject implements {@link #setWidth(String)}
 * you can set the width of any widget like so:
 * 
 * <pre>
 * &lt;g:Label width='15em'>Hello there&lt;/g:Label></pre>
 * 
 * Generally speaking, values are parsed as if they were Java literals, so
 * methods like {@link #setVisible(boolean)} are also available:
 * 
 * <pre>
 * &lt;g:Label width='15em' visible='false'>Hello there&lt;/g:Label></pre>
 * Enum properties work this way too. Imagine a Bagel widget with a handy Type
 * enum and a setType(Type) method:
 * 
 * <pre>
 * enum Type { poppy, sesame, raisin, jalapeno }
 * 
 * &lt;my:Bagel type='poppy' /></pre>
 * 
 * There is also special case handling for two common method signatures,
 * <code>(int, int)</code> and <code>(double, {@link 
 * com.google.gwt.dom.client.Style.Unit Unit})</code>
 * 
 * <pre>
 * &lt;g:Label pixelSize='100, 100'>Hello there&lt;/g:Label></pre>
 * 
 * Finally, a few UiObject methods get special handling. The debug id (see
 * {@link #ensureDebugId}) of any UiObject can be set via the
 * <code>debugId</code> attribute, and addtional style names and dependent style
 * names can be set with the <code>addStyleNames</code> and
 * <code>addStyleDependentNames</code> attributes.<pre>
 * &lt;g:Label debugId='helloLabel' 
 *     addStyleNames='pretty rounded big'>Hello there&lt;/g:Label></pre>
 * 
 * Style names can be space or comma separated.
 */
public abstract class UIObject
{

	public static void setVisible(Element target, boolean visible)
	{
		// TODO Auto-generated method stub

	}

	public static boolean isVisible(Element element)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setVisible(boolean visible)
	{
		// TODO Auto-generated method stub

	}

	public void removeStyleName(String styleName)
	{
		// TODO Auto-generated method stub

	}

	public void addStyleName(String name)
	{
		// TODO Auto-generated method stub

	}

	public void removeStyleDependentName(String styleName)
	{
		// TODO Auto-generated method stub

	}

	public void addStyleDependentName(String name)
	{
		// TODO Auto-generated method stub

	}
}
