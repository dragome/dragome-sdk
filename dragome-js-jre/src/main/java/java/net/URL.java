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
package java.net;

import java.io.InputStream;

/**
 * Class URL represents a Uniform Resource Locator, a pointer to a "resource" on the World Wide Web.
 *
 * 
 */
public class URL
{

	private String representation;

	/**
	 * Creates a URL object from the String representation.
	 */
	public URL(String spec)
	{
		if (!spec.matches("[a-z]+://.*"))
		{
			// TODO: Should throw a MalformedURLException.
			throw new RuntimeException("Protocol part is missing: " + spec);
		}
		representation= spec;
	}

	/**
	 * Creates a URL by parsing the given spec within a specified context.
	 */
	public URL(URL context, String spec)
	{
		if (spec.matches("[a-z]+://.*"))
		{
			// The spec is already absolute.
			representation= spec;
		}
		else
		{
			String base= context.toString();

			// Context URL is of the form <scheme>://<authority><path>?<query>#<fragment>
			// Strip into scheme, authority&path, query and fragment.
			String[] parts= base.split("://|\\?|#");
			String authorityAndPath= parts[1];
			// Strip last path component.
			int index= authorityAndPath.lastIndexOf('/');
			if (index != -1)
			{
				authorityAndPath= authorityAndPath.substring(0, index);
			}
			representation= parts[0] + "://" + authorityAndPath + '/' + spec;
		}
	}

	/**
	 * Constructs a string representation of this URL.
	 */
	public String toString()
	{
		return representation;
	}

	public URI toURI()
	{
		try
		{
			return new URI(toString());
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}

	public URLConnection openConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream openStream() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getFile()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
