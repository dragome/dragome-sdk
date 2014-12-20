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
package java.io;

import java.net.URI;

public class File implements Serializable, Comparable<File>
{
	public File(String uri)
	{
		// TODO Auto-generated constructor stub
	}

	public File(URI uri)
	{
		// TODO Auto-generated constructor stub
	}

	public File(String home, String string)
	{
		// TODO Auto-generated constructor stub
	}

	public int compareTo(File another)
	{
		return 0;
	}

	public Object toURI()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public File[] listFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDirectory()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAbsolutePath()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean exists()
	{
		return false;
	}

	public boolean canRead()
    {
	    return false;
    }
}
