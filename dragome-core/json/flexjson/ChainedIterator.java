/**
 * Copyright 2007 Charlie Hubbard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package flexjson;

import java.util.Iterator;
import java.util.Set;

public class ChainedIterator implements Iterator
{

	Iterator[] iterators;
	int current= 0;

	public ChainedIterator(Set... sets)
	{
		iterators= new Iterator[sets.length];
		for (int i= 0; i < sets.length; i++)
		{
			iterators[i]= sets[i].iterator();
		}
	}

	public boolean hasNext()
	{
		if (iterators[current].hasNext())
		{
			return true;
		}
		else
		{
			current++;
			return current < iterators.length && iterators[current].hasNext();
		}
	}

	public Object next()
	{
		return iterators[current].next();
	}

	public void remove()
	{
		iterators[current].remove();
	}
}
