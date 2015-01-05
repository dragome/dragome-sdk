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
package com.dragome.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class TimeCollector
{
	private Stack<Long> start;
	private Stack<String> keys;
	protected Map<String, List<Long>> elapsedTimes= new HashMap<String, List<Long>>();

	public TimeCollector()
	{
		start= new Stack<Long>();
		keys= new Stack<String>();
	}

	public void reset()
	{
		elapsedTimes.clear();
		start.clear();
		keys.clear();
	}

	public void registerStart(String key)
	{
		long nanoTime= getCurrentTime();
		start.push(nanoTime);
		if (!elapsedTimes.containsKey(key))
			elapsedTimes.put(key, new ArrayList<Long>());

		keys.push(key);
	}

	private long getCurrentTime()
	{
		return System.currentTimeMillis();
	}

	public void registerEnd()
	{
		long nanoTime= getCurrentTime();
		if (!keys.isEmpty())
		{
			String key= (String) keys.pop();
			long elapsedTime= nanoTime - ((Long) start.pop()).longValue();
			elapsedTimes.get(key).add(elapsedTime);
		}
	}

	public void printReport(String prefijo)
	{
		for (Entry<String, List<Long>> entry : elapsedTimes.entrySet())
		{
			Double total= 0.0;
			for (Long time : entry.getValue())
			{
				total+= time;
			}

			//total/= 1000000.0;
			double counter= entry.getValue().size();
			double average= total / counter;

			//	    System.out.println(String.format("%9s -> %50s ==> %.4f ms X %d= %.4fs = %.4fm", prefijo, entry.getKey(), average, (long) counter, total / 1000.0, total / 1000.0 / 60.0));
			System.out.println(entry.getKey() + "=>" + average + "ms X " + counter + " = " + total + "ms");
		}
	}
}
