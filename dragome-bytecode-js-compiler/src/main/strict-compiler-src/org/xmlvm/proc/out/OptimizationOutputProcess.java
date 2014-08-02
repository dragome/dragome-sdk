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

package org.xmlvm.proc.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.XmlvmResource.XmlvmConstantStringElement;

/**
 * Optimizes the DEXMLVM output.
 */
public class OptimizationOutputProcess extends XmlvmProcessImpl
{

	/**
	 * Helper class representing one entry of the string constant pool. Used by
	 * method <code>generateStringConstantPool</code>.
	 */
	class ConstantPoolEntry
	{
		public int id;
		public int length;
		public String encodedString;
		public String escapedString;

		public ConstantPoolEntry(int id, int len, String escapedConstant, String encodedConstant)
		{
			this.id= id;
			this.length= len;
			this.escapedString= escapedConstant;
			this.encodedString= encodedConstant;
		}

		public boolean equals(Object other)
		{
			if (!(other instanceof ConstantPoolEntry))
			{
				return false;
			}
			ConstantPoolEntry o= (ConstantPoolEntry) other;
			return other instanceof ConstantPoolEntry && this.id == o.id && this.encodedString.equals(o.encodedString);
		}

	}

	/**
	 * Initializes the {@link OptimizationOutputProcess}.
	 */
	public OptimizationOutputProcess(Arguments arguments)
	{
		super(arguments);
		addSupportedInput(DEXmlvmOutputProcess.class);
	}

	public boolean processPhase1(BundlePhase1 bundle)
	{
		return true;
	}

	public boolean processPhase2(BundlePhase2 bundle)
	{
		generateStringConstantPool(bundle);
		return true;
	}

	/**
	 * Generates the string constant pool for this application. This method
	 * iterates over all <code>XmlvmResources</code> and builds up a list of
	 * unique string constants. Those string constants are added as another
	 * <code>XmlvmResource</code> (of type <code>CONST_POOL</code>) to the
	 * bundle. The XML markup of the constant pool looks as follows:
	 * 
	 * <pre>
	 * &lt;vm:constant-pool&gt;
	 *     &lt;entry id="..." length="..." encoded-constant="..." escaped-constant="..."/&gt;
	 *     ...
	 * &lt/constant-pool&gt;
	 * </pre>
	 */
	private void generateStringConstantPool(BundlePhase2 bundle)
	{
		List<ConstantPoolEntry> constantPool= new ArrayList<ConstantPoolEntry>();
		Map<String, Integer> constantPoolIDs= new HashMap<String, Integer>();
		for (XmlvmResource resource : bundle.getResources())
		{
			List<XmlvmConstantStringElement> instructions= new ArrayList<XmlvmConstantStringElement>();
			resource.collectInstructions(instructions);
			for (XmlvmConstantStringElement inst : instructions)
			{
				String escapedConstant= inst.getEscapedStringConstant();
				String encodedConstant= inst.getEncodedStringConstant();
				int len= inst.getLength();
				int id= -1;
				if (constantPoolIDs.containsKey(encodedConstant))
				{
					id= constantPoolIDs.get(encodedConstant);
				}
				else
				{
					id= constantPool.size();
					ConstantPoolEntry entry= new ConstantPoolEntry(id, len, escapedConstant, encodedConstant);
					constantPool.add(entry);
					constantPoolIDs.put(encodedConstant, id);
				}
				inst.setContantPoolID(id);
			}
		}

		Element xmlRoot= new Element("constant-pool", XmlvmResource.nsXMLVM);
		for (ConstantPoolEntry entry : constantPool)
		{
			Element xmlEntry= new Element("entry", XmlvmResource.nsXMLVM);
			xmlEntry.setAttribute("id", "" + entry.id);
			xmlEntry.setAttribute("length", "" + entry.length);
			xmlEntry.setAttribute("escaped-constant", entry.escapedString);
			xmlEntry.setAttribute("encoded-constant", entry.encodedString);
			xmlRoot.addContent(xmlEntry);
		}

		XmlvmResource resource= new XmlvmResource(XmlvmResource.Type.CONST_POOL, new Document(xmlRoot));
		bundle.addAdditionalResource(resource);
	}
}
