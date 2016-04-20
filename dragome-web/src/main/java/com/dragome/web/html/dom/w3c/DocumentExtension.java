package com.dragome.web.html.dom.w3c;

import org.w3c.dom.CaretPosition;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ObjectArray;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.Touch;
import org.w3c.dom.events.TouchList;
import org.w3c.dom.html.Window;
import org.w3c.dom.stylesheets.StyleSheet;

public interface DocumentExtension extends Document
{
	// Document-1
	public ObjectArray<StyleSheet> getStyleSheets();
	public String getSelectedStyleSheetSet();
	public void setSelectedStyleSheetSet(String selectedStyleSheetSet);
	public String getLastStyleSheetSet();
	public String getPreferredStyleSheetSet();
	public DOMStringList getStyleSheetSets();
	public void enableStyleSheetsForSet(String name);
	// Document-2
	public Element elementFromPoint(float x, float y);
	public CaretPosition caretPositionFromPoint(float x, float y);
	// NodeSelector
	public Element querySelector(String selectors);
	public NodeList querySelectorAll(String selectors);
	// Document-42
	public Touch createTouch(Window view, EventTarget target, int identifier, int pageX, int pageY, int screenX, int screenY);
	public TouchList createTouchList(Touch... touches);
}
