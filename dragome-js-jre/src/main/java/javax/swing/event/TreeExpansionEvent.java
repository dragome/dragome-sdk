package javax.swing.event;

import java.util.EventObject;

import javax.swing.tree.TreePath;

public class TreeExpansionEvent extends EventObject
{

	/**
	 * Constructs a TreeExpansionEvent object.
	 */
	public TreeExpansionEvent(Object source, TreePath path)
	{
		super(source);
	}

}
