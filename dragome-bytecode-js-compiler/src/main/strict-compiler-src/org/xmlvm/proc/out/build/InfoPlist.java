/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.proc.out.build;

import java.util.StringTokenizer;

public class InfoPlist
{

	private String text;

	public InfoPlist(String template)
	{
		text= template;
	}

	public void setIdentifier(String id)
	{
		text= text.replace("PROPERTY_BUNDLEIDENTIFIER", id);
	}

	public void setVersion(String version)
	{
		text= text.replace("PROPERTY_BUNDLEVERSION", version);
	}

	public void setDisplayName(String name)
	{
		text= text.replace("PROPERTY_BUNDLEDISPLAYNAME", name);
	}

	public void setStatusBarHidden(String statusbarhidden)
	{
		text= text.replace("PROPERTY_STATUSBARHIDDEN", toBoolean(statusbarhidden));
	}

	public void setPrerenderIcon(String prerendericon)
	{
		text= text.replace("PROPERTY_PRERENDEREDICON", toBoolean(prerendericon));
	}

	public void setFileSharingEnabled(String filesharingenabled)
	{
		text= text.replace("PROPERTY_FILESHARINGENABLED", toBoolean(filesharingenabled));
	}

	public void setApplicationExits(String applicationexits)
	{
		text= text.replace("PROPERTY_APPLICATIONEXITS", toBoolean(applicationexits));
	}

	public void setDefaultOrientation(String defaultorientation)
	{
		text= text.replace("PROPERTY_INTERFACE_ORIENTATION", defaultorientation);
	}

	public void setSupportedOrientations(String supportedorientations)
	{
		text= text.replace("PROPERTY_SUPPORTED_INTERFACE_ORIENTATIONS", getPropertyAsArray("UISupportedInterfaceOrientations", "string", supportedorientations));
	}

	public void setFonts(String fonts)
	{
		text= text.replace("PROPERTY_FONTS", getPropertyAsArray("UIAppFonts", "string", fonts));
	}

	public void setInjectedInfoPlist(String xml)
	{
		text= text.replace("PROPERTY_INJECTED_INFO_PLIST", xml == null ? "" : xml);
	}

	public void setApplication(String application)
	{
		text= text.replace("XMLVM_APP", application);
	}

	public void setMainNib(String mainNib)
	{
		text= text.replace("PROPERTY_MAINNIB", mainNib == null ? "" : mainNib);
	}

	public void setMainStoryboard(String mainStoryboard)
	{
		if (mainStoryboard != null && mainStoryboard.length() > 0)
		{
			mainStoryboard= "<key>UIMainStoryboardFile</key>\n<string>" + mainStoryboard + "</string>";
		}
		else
		{
			mainStoryboard= "";
		}
		text= text.replace("PROPERTY_MAINSTORYBOARD", mainStoryboard);
	}

	/**
	 * Convert a list of entries to an Info.plist array
	 * 
	 * @param keyname
	 *            The name of the plist entry
	 * @param type
	 *            The type of the plist entry
	 * @param entries
	 *            The array items, each one separated by colon ":"
	 * @return The plist array
	 */
	private static String getPropertyAsArray(String keyname, String type, String entries)
	{
		if (entries == null)
		{
			return "";
		}
		StringBuilder result= new StringBuilder();
		StringTokenizer tk= new StringTokenizer(entries, ":");
		while (tk.hasMoreTokens())
		{
			String token= tk.nextToken();
			if (token.length() != 0)
			{
				result.append("\t\t<").append(type).append(">");
				result.append(token).append("</").append(type).append(">\n");
			}
		}
		String array= result.toString();
		return array.length() == 0 ? "" : "\t<key>" + keyname + "</key>\n\t<array>\n" + array + "\t</array>";
	}

	public String toString()
	{
		return text;
	}

	private static String toBoolean(String value)
	{
		return value.toLowerCase().equals("true") ? "true" : "false";
	}
}
