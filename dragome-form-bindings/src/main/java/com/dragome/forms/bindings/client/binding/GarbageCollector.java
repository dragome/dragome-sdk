package com.dragome.forms.bindings.client.binding;

import java.util.ArrayList;

import com.dragome.forms.bindings.client.util.Utils;
import com.dragome.model.interfaces.HandlerRegistration;

/**
 * This class can be used to collect instances of Disposable and HandlerRegistration so the
 * can all be disposed of at once.  If you're using any of the binders (e.g. WidgetBinder, MetadataBinder etc)
 * you won't  need to use this as the perform garbage collection for you.  If you're creating listeners
 * to other long lived objects in your business layers then it can be handy. E.g.
 * <pre>
 * GarbageCollector gc = new GarbageCollector();
 *
 * // collect all our disposables so we can remove them in one fell swoop.
 * gc.add(
 *   aCommandThatLivesForever.always().sendResultTo(someDestination),
 *   anotherCommandThatLivesForever.always().sendErrorTo(someErrorThingy)
 * );
 *
 * // then later when you're done you just call dispose an all the
 * // event handlers will be cleared.
 * gc.dispose()
 * </pre>
 */
public class GarbageCollector implements Disposable
{
	private ArrayList<Disposable> disposables= new ArrayList<Disposable>();

	/**
	 * Adds the specified disposable to this collector and returns it.
	 * @param disposable the disposable to add.
	 * @return the disposable after adding it to the collector.
	 */
	public <T extends Disposable> T capture(T disposable)
	{
		add(disposable);
		return disposable;
	}

	public void add(Disposable disposable)
	{
		disposables.add(disposable);
	}

	public void add(Disposable disposable, Disposable... others)
	{
		disposables.addAll(Utils.asList(disposable, others));
	}

	public void add(final HandlerRegistration registration)
	{
		add(new Disposable()
		{
			public void dispose()
			{
				registration.removeHandler();
			}
		});
	}

	public void dispose()
	{
		for (Disposable disposable : disposables)
		{
			disposable.dispose();
		}

		disposables.clear();
	}
}
