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

import java.util.List;
import java.util.Stack;

/**
 * Internal class used by Flexjson to represent a path to a field within a serialized stream.
 */
public class Path
{
	Stack<String> path= new Stack<String>();

	public Path()
	{
	}

	public Path(String... fields)
	{
		for (String field : fields)
		{
			path.add(field);
		}
	}

	public Path enqueue(String field)
	{
		path.add(field);
		return this;
	}

	public String pop()
	{
		return path.pop();
	}

	public List<String> getPath()
	{
		return path;
	}

	public int length()
	{
		return path.size();
	}

	public String toString()
	{
		StringBuilder builder= new StringBuilder("[ ");
		boolean afterFirst= false;
		for (String current : path)
		{
			if (afterFirst)
			{
				builder.append(".");
			}
			builder.append(current);
			afterFirst= true;
		}
		builder.append(" ]");
		return builder.toString();
	}

	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Path path1= (Path) o;

		if (!path.equals(path1.path))
			return false;

		return true;
	}

	public int hashCode()
	{
		return path.hashCode();
	}

	public static Path parse(String path)
	{
		return path != null ? new Path(path.split("\\.")) : new Path();
	}
}
