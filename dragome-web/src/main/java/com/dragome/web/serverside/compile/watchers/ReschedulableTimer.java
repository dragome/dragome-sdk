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
package com.dragome.web.serverside.compile.watchers;

import java.util.Timer;
import java.util.TimerTask;

public class ReschedulableTimer extends Timer
{
	private Runnable task;
	private TimerTask timerTask;

	public void schedule(Runnable runnable, long delay)
	{
		task= runnable;
		timerTask= new TimerTask()
		{
			public void run()
			{
				task.run();
			}
		};

		schedule(timerTask, delay);
	}

	public void reschedule(long delay)
	{
		if (isScheduling())
		{
			timerTask.cancel();
			timerTask= new TimerTask()
			{
				public void run()
				{
					task.run();
				}
			};
			schedule(timerTask, delay);
		}
	}

	public boolean isScheduling()
	{
		return timerTask != null;
	}
}
