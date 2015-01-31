package com.dragome.render.serverside.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class SwingUtils
{

	public static void addChangeListener(final JTextComponent text, final ChangeListener changeListener)
	{
		Objects.requireNonNull(text);
		Objects.requireNonNull(changeListener);
		final DocumentListener dl= new DocumentListener()
		{
			private int lastChange= 0, lastNotifiedChange= 0;
	
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				changedUpdate(e);
			}
	
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				changedUpdate(e);
			}
	
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				lastChange++;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if (lastNotifiedChange != lastChange)
						{
							lastNotifiedChange= lastChange;
							changeListener.stateChanged(new ChangeEvent(text));
						}
					}
				});
			}
		};
	
		PropertyChangeListener propertyChangeListener= new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				Document d1= (Document) e.getOldValue();
				Document d2= (Document) e.getNewValue();
				if (d1 != null)
					d1.removeDocumentListener(dl);
				if (d2 != null)
					d2.addDocumentListener(dl);
			}
		};
	
		text.addPropertyChangeListener("document", propertyChangeListener);
		Document d= text.getDocument();
		if (d != null)
			d.addDocumentListener(dl);
	}

}
