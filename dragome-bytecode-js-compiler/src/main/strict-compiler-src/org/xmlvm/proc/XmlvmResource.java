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

package org.xmlvm.proc;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xmlvm.proc.out.OutputFile;
import org.xmlvm.util.comparators.XmlvmMethodComparator;

/**
 * This class describes a XMLVM resource, that is e.g. produces by
 * InputProcesses.
 * 
 * TODO(Sascha): Some aspects in this class don't properly reflect the CLI input
 * type. E.g., a CLI resource can have multiple types and therefore multiple
 * super types.
 */
public class XmlvmResource
{
	/**
	 * 
	 */
	private static final String ATTRIBUTE_INTERFACE_TABLE_SIZE= "interfaceTableSize";
	/**
	 * 
	 */
	private static final String TAG_CLASS= "class";

	/**
	 * Possible types for an XmlvmResource.
	 */
	public static enum Type
	{
		JVM, CLI, CLI_DFA, DEX, CONST_POOL
	}

	/**
	 * Possible tags for an XmlvmResource.
	 */
	public static enum Tag
	{
		SKELETON_ONLY;
	}

	/**
	 * Wrapper for a <code>&lt;dex:invoke-*&gt;</code> element.
	 */
	public class XmlvmInvokeInstruction
	{
		public Element invokeElement;

		/**
		 * Wrapper for a <code>&lt;dex:invoke-*&gt;</code> element.
		 * 
		 * @param invokeElement
		 *            DOM element representing a
		 *            <code>&lt;dex:invoke-*&gt;</code>.
		 */
		public XmlvmInvokeInstruction(Element invokeElement)
		{
			this.invokeElement= invokeElement;
		}

		public String toString()
		{
			return (new XMLOutputter()).outputString(invokeElement);
		}

		/**
		 * @return
		 */
		public String getClassType()
		{
			return invokeElement.getAttributeValue("class-type");
		}

		/**
		 * @return
		 */
		public String getMethodName()
		{
			return invokeElement.getAttributeValue("method");
		}

		/**
		 * @param vtableIndex
		 */
		public void setVtableIndex(int vtableIndex)
		{
			invokeElement.setAttribute("vtable-index", "" + vtableIndex);
		}

		/**
		 * @param type
		 */
		public void setClassType(String type)
		{
			invokeElement.setAttribute("class-type", type);
		}

		/**
		 * Returns a list of all parameter types (e.g. java.lang.String, ...)
		 * 
		 * @return list of all parameter types
		 */
		public List<String> getParameterTypes()
		{
			Element signature= invokeElement.getChild("parameters", nsDEX);
			@SuppressWarnings("unchecked")
			List<Element> parameterList= signature.getChildren("parameter", nsDEX);

			List<String> parameterTypes= new ArrayList<String>();
			for (Element parameter : parameterList)
			{
				parameterTypes.add(parameter.getAttributeValue("type"));
			}
			return parameterTypes;
		}

	}

	/**
	 * Wrapper for those XMLVM elements that have an associated string constant.
	 * In XMLVM these can only be either <code>&lt;dex:const-string&gt;</code>
	 * or a <code>&lt;vm:field&gt;</code> element with a constant string
	 * initializer.
	 */
	public class XmlvmConstantStringElement
	{
		private Element element;

		/**
		 * Wrapper for a <code>&lt;dex:const-string*&gt;</code> or a
		 * <code>&lt;vm:field&gt;</code> element.
		 * 
		 * @param element
		 *            DOM element representing a
		 *            <code>&lt;dex:const-string&gt;</code> or a
		 *            <code>&lt;vm:field&gt;</code>.
		 */
		public XmlvmConstantStringElement(Element element)
		{
			this.element= element;
		}

		/**
		 * Returns the escaped version of the string that uses the \ooo octal
		 * notation for representing special characters.
		 */
		public String getEscapedStringConstant()
		{
			return element.getAttributeValue("value");
		}

		/**
		 * Returns the encoded version of the string. The encoded version
		 * represents the string as a comma-separated list of short values.
		 */
		public String getEncodedStringConstant()
		{
			return element.getAttributeValue("encoded-value");
		}

		/**
		 * Returns the length of the string.
		 */
		public int getLength()
		{
			return Integer.parseInt(element.getAttributeValue("length"));
		}

		/**
		 * Set the constant pool ID for this string.
		 * 
		 * @param id
		 *            Constant pool ID.
		 */
		public void setContantPoolID(int id)
		{
			element.setAttribute("id", "" + id);
		}

	}

	public class XmlvmMemberReadWrite
	{
		public Element memberReadWriteElement;

		/**
		 * Wrapper for a <code>&lt;dex:iget-*&gt;</code> or a
		 * <code>&lt;dex:iput-*&gt;</code> element.
		 * 
		 * @param invokeElement
		 *            DOM element representing a <code>&lt;dex:iget-*&gt;</code>
		 *            or a <code>&lt;dex:iput-*&gt;</code>.
		 */
		public XmlvmMemberReadWrite(Element memberReadWriteElement)
		{
			this.memberReadWriteElement= memberReadWriteElement;
		}

		public String toString()
		{
			return (new XMLOutputter()).outputString(memberReadWriteElement);
		}

		/**
		 * @return
		 */
		public String getMemberName()
		{
			return this.memberReadWriteElement.getAttributeValue("member-name");
		}

		/**
		 * @param type
		 */
		public void setClassType(String type)
		{
			this.memberReadWriteElement.setAttribute("class-type", type);
		}

		/**
		 * @return
		 */
		public String getClassType()
		{
			return this.memberReadWriteElement.getAttributeValue("class-type");
		}

	}

	/**
	 * Wrapper for a <code>&lt;vm:method&gt;</code> element.
	 */
	public class XmlvmMethod
	{
		public Element methodElement;

		/**
		 * Wrapper for a <code>&lt;vm:method&gt;</code> element.
		 * 
		 * @param invokeElement
		 *            DOM element representing a <code>&lt;vm:method&gt;</code>.
		 */
		public XmlvmMethod(Element methodElement)
		{
			this.methodElement= methodElement;
		}

		public int hashCode()
		{
			return toString().hashCode();
		}

		public String toString()
		{
			return (new XMLOutputter()).outputString(methodElement);
		}

		public String getName()
		{
			return methodElement.getAttributeValue("name");
		}

		/**
		 * Retrieve all invoke instructions that are handled via a vtable (i.e.,
		 * <code>&lt;dex:invoke-virtual&gt;</code> and
		 * <code>&lt;dex:invoke-interface&gt;</code> instructions).
		 * 
		 * @return All <code>&lt;dex:invoke-virtual&gt;</code> and
		 *         <code>&lt;dex:invoke-interface&gt;</code> instructions of
		 *         this method.
		 */
		public List<XmlvmInvokeInstruction> getVtableInvokeInstructions()
		{
			List<XmlvmInvokeInstruction> invokeInstructions= new ArrayList<XmlvmInvokeInstruction>();
			searchForVtableInvokeInstructions(invokeInstructions, methodElement);
			return invokeInstructions;
		}

		@SuppressWarnings("unchecked")
		private void searchForVtableInvokeInstructions(List<XmlvmInvokeInstruction> invokeInstructions, Element element)
		{
			List<Element> children= element.getChildren("invoke-virtual", nsDEX);
			for (Element instruction : children)
			{
				XmlvmInvokeInstruction invoke= new XmlvmInvokeInstruction(instruction);
				invokeInstructions.add(invoke);
			}
			children= element.getChildren("invoke-virtual-range", nsDEX);
			for (Element instruction : children)
			{
				XmlvmInvokeInstruction invoke= new XmlvmInvokeInstruction(instruction);
				invokeInstructions.add(invoke);
			}
			children= element.getChildren();
			for (Element node : children)
			{
				searchForVtableInvokeInstructions(invokeInstructions, node);
			}
		}

		/**
		 * Determines if two Java methods can override each other. Two methods
		 * override each other, if their names as well as all their input
		 * parameter types are identical. Note that the return types need not be
		 * identical since Java allows covariant returns.
		 * <code>doesOverrideMethod()</code> does not check for subtype
		 * relationship of the return type. The return type is essentially
		 * ignored and therefore <code>doesOverrideMethod()</code> is
		 * commutative.
		 * 
		 * @param method
		 *            {@link #XmlvmResource} to be checked.
		 * @return true, if <code>method</code> overrides <code>this</code>.
		 */
		@SuppressWarnings("unchecked")
		public boolean doesOverrideMethod(XmlvmMethod method)
		{
			return doesOverrideMethod(method.getName(), method.methodElement.getChild("signature", nsXMLVM).getChildren("parameter", nsXMLVM));
		}

		/**
		 * Determines if two Java methods can override each other. Two methods
		 * override each other, if their names as well as all their input
		 * parameter types are identical. Note that the return types need not be
		 * identical since Java allows covariant returns.
		 * <code>doesOverrideMethod()</code> does not check for subtype
		 * relationship of the return type. The return type is essentially
		 * ignored and therefore <code>doesOverrideMethod()</code> is
		 * commutative.
		 * 
		 * @param method
		 *            {@link #XmlvmInvokeVirtual} to be checked.
		 * @return true, if <code>method</code> overrides <code>this</code>.
		 */
		@SuppressWarnings("unchecked")
		public boolean doesOverrideMethod(XmlvmInvokeInstruction instruction)
		{
			return doesOverrideMethod(instruction.getMethodName(), instruction.invokeElement.getChild("parameters", nsDEX).getChildren("parameter", nsDEX));
		}

		@SuppressWarnings("unchecked")
		private boolean doesOverrideMethod(String methodName, List<Element> parameters)
		{
			if (!this.getName().equals(methodName))
			{
				return false;
			}
			Element mySignature= methodElement.getChild("signature", nsXMLVM);
			List<Element> myParameters= mySignature.getChildren("parameter", nsXMLVM);

			if (myParameters.size() != parameters.size())
			{
				return false;
			}

			for (int i= 0; i < myParameters.size(); i++)
			{
				String myParameterType= myParameters.get(i).getAttributeValue("type");
				String otherParameterType= parameters.get(i).getAttributeValue("type");
				if (!myParameterType.equals(otherParameterType))
				{
					return false;
				}
			}
			return true;
		}

		/**
		 * Returns true if this method is static.
		 */
		public boolean isStatic()
		{
			String flag= methodElement.getAttributeValue("isStatic");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this method is private.
		 */
		public boolean isPrivate()
		{
			String flag= methodElement.getAttributeValue("isPrivate");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this method is protected.
		 */
		public boolean isProtected()
		{
			String flag= methodElement.getAttributeValue("isProtected");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this method is public.
		 */
		public boolean isPublic()
		{
			String flag= methodElement.getAttributeValue("isPublic");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this method is abstract.
		 */
		public boolean isAbstract()
		{
			String flag= methodElement.getAttributeValue("isAbstract");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this method is native.
		 */
		public boolean isNative()
		{
			String flag= methodElement.getAttributeValue("isNative");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this method is a constructor.
		 */
		public boolean isConstructor()
		{
			return methodElement.getAttributeValue("name").equals("<init>");
		}

		/**
		 * Returns true if this method is synthetic.
		 */
		public boolean isSynthetic()
		{
			String flag= methodElement.getAttributeValue("isSynthetic");
			return flag != null && flag.equals("true");
		}

		/**
		 * Sets the @isSynthetic flag of the method.
		 */
		public void setSynthetic(boolean flag)
		{
			if (flag)
			{
				methodElement.setAttribute("isSynthetic", "true");
			}
			else
			{
				methodElement.removeAttribute("isSynthetic");
			}
		}

		/**
		 * Returns whether this method is overriding another method the
		 * containing class inherited.
		 * 
		 * This is used by the C# backend which needs to emit the special
		 * override keyword in the method signature in this case (see
		 * http://msdn.microsoft.com/en-us/library/ebca9ah3.aspx).
		 * 
		 * It is not filled by other backends!
		 */
		public boolean isOverriding()
		{
			String flag= methodElement.getAttributeValue("isOverriding");
			return flag != null && flag.equals("true");
		}

		/**
		 * Set if this method is overriding another method the containing class
		 * inherited.
		 * 
		 * This is used by the C# backend which needs to emit the special
		 * override keyword in the method signature in this case (see
		 * http://msdn.microsoft.com/en-us/library/ebca9ah3.aspx)
		 * 
		 * It is not filled by other backends!
		 */
		public void setOverriding(boolean overriding)
		{
			methodElement.setAttribute("isOverriding", Boolean.toString(overriding));
		}

		/**
		 * Set a vtable index for this method (XML attribute
		 * <code>vtableIndex</code>).
		 */
		public void setVtableIndex(int idx)
		{
			methodElement.setAttribute("vtableIndex", "" + idx);
		}

		/**
		 * Returns a list of all parameter types (e.g. java.lang.String, ...)
		 * 
		 * @return list of all parameter types
		 */
		public List<String> getParameterTypes()
		{
			Element signature= methodElement.getChild("signature", nsXMLVM);
			@SuppressWarnings("unchecked")
			List<Element> parameterList= signature.getChildren("parameter", nsXMLVM);

			List<String> parameterTypes= new ArrayList<String>();
			for (Element parameter : parameterList)
			{
				parameterTypes.add(parameter.getAttributeValue("type"));
			}
			return parameterTypes;
		}

		/**
		 * @return the index in the interface table for this method, or null for
		 *         none
		 */
		public Integer getInterfaceTableIndex()
		{
			Integer interfaceTableIndex= null;
			String strVal= methodElement.getAttributeValue("itableIndex");
			if (strVal != null)
			{
				try
				{
					interfaceTableIndex= Integer.parseInt(strVal);
				}
				catch (NumberFormatException e)
				{
					// do nothing
				}
			}
			return interfaceTableIndex;
		}

		/**
		 * @param interfaceTableIndex
		 *            the index of in the interface table for this method, or
		 *            null for none
		 */
		public void setInterfaceTableIndex(Integer interfaceTableIndex)
		{
			methodElement.setAttribute("itableIndex", interfaceTableIndex == null ? "" : interfaceTableIndex.toString());
		}
	}

	/**
	 * Add a copy of the given method to the XmlvmResource
	 * 
	 * @param method
	 *            method to add
	 * @return Copied method
	 */
	public XmlvmMethod addMethod(XmlvmMethod method)
	{
		Element clazz= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		Element clone= (Element) method.methodElement.clone();
		clazz.addContent(clone);
		return new XmlvmMethod(clone);
	}

	/**
	 * Wrapper for a <code>&lt;vm:field&gt;</code> element.
	 */
	public class XmlvmField
	{
		public Element fieldElement;

		/**
		 * Wrapper for a <code>&lt;vm:field&gt;</code> element.
		 * 
		 * @param fieldElement
		 *            DOM element representing a <code>&lt;vm:field&gt;</code>.
		 */
		public XmlvmField(Element fieldElement)
		{
			this.fieldElement= fieldElement;
		}

		/**
		 * Returns the name of the field.
		 */
		public String getName()
		{
			return fieldElement.getAttributeValue("name");
		}

		/**
		 * Returns the type of the field.
		 */
		public String getType()
		{
			return fieldElement.getAttributeValue("type");
		}

		/**
		 * Returns true if this field is private.
		 */
		public boolean isPrivate()
		{
			String flag= fieldElement.getAttributeValue("isPrivate");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this field is protected.
		 */
		public boolean isProtected()
		{
			String flag= fieldElement.getAttributeValue("isProtected");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if this field is public.
		 */
		public boolean isPublic()
		{
			String flag= fieldElement.getAttributeValue("isPublic");
			return flag != null && flag.equals("true");
		}

		/**
		 * Returns true if the field the <code>instruction</code> is accessing
		 * has the same name as this field.
		 */
		public boolean matchesName(XmlvmMemberReadWrite instruction)
		{
			return getName().equals(instruction.getMemberName());
		}

		/**
		 * Returns true if the given <code>field</code> has the same name and
		 * type as this field.
		 */
		public boolean matchesDeclaration(XmlvmField field)
		{
			return getName().equals(field.getName()) && getType().equals(field.getType());
		}
	}

	/**
	 * @param search
	 */
	@SuppressWarnings("unchecked")
	public void removeMethod(XmlvmMethod search)
	{
		List<Element> classes= xmlvmDocument.getRootElement().getChildren(TAG_CLASS, nsXMLVM);
		for (Element clazz : classes)
		{
			if (clazz.removeContent(search.methodElement))
			{
				return;
			}
		}
	}

	/**
	 * vm:itable contains mappings between interface indices of implemented
	 * interfaces and the class vtable or directly methods of the class
	 */
	public class XmlvmItable
	{
		private Element itableElement;

		public XmlvmItable(Element itableElement)
		{
			this.itableElement= itableElement;
		}

		/**
		 * Create a new mapping between a global interface method index and the
		 * vtable of a class
		 * 
		 * @param itableIndex
		 *            global interface method index
		 * @param vtableIndexClass
		 *            Corresponding vtable index of an implementing method in a
		 *            class.
		 */
		public void addVtableMapping(String ifaceName, XmlvmMethod ifaceMethod, int vtableIndex)
		{
			Element map= new Element("vtable-map", nsXMLVM);
			map.setAttribute("ifaceName", ifaceName);
			map.setAttribute("ifaceMethodName", ifaceMethod.getName());
			Element signature= (Element) ifaceMethod.methodElement.getChild("signature", nsXMLVM).clone();
			map.addContent(signature);
			map.setAttribute("vtableIndex", "" + vtableIndex);
			itableElement.addContent(map);
		}

		/**
		 * Create a new mapping between a global interface method index and a
		 * direct invoke
		 * 
		 * @param itableIndex
		 *            global interface method index
		 * @param classType
		 *            class containing the method to be mapped
		 * @param methodName
		 *            name of the method to be mapped
		 */
		public void addDirectMapping(String ifaceName, XmlvmMethod ifaceMethod, String classType)
		{
			Element map= new Element("direct-map", nsXMLVM);
			map.setAttribute("ifaceName", ifaceName);
			map.setAttribute("ifaceMethodName", ifaceMethod.getName());
			Element signature= (Element) ifaceMethod.methodElement.getChild("signature", nsXMLVM).clone();
			map.addContent(signature);
			map.setAttribute("className", classType);
			itableElement.addContent(map);
		}
	}

	public static Namespace nsXMLVM= Namespace.getNamespace("vm", "http://xmlvm.org");
	public static Namespace nsDEX= Namespace.getNamespace("dex", "http://xmlvm.org/dex");
	public static Namespace nsJVM= Namespace.getNamespace("jvm", "http://xmlvm.org/jvm");

	private final Type type;
	private final Document xmlvmDocument;
	private final Set<String> referencedTypes;
	private final String name;
	private final String superTypeName;
	private final Map<Tag, String> tags= new HashMap<Tag, String>();

	public XmlvmResource(Type type, Document xmlvmDocument)
	{
		this.type= type;
		this.xmlvmDocument= xmlvmDocument;

		if (type != Type.CONST_POOL)
		{
			this.referencedTypes= extractReferencedTypes(xmlvmDocument);

			Element classElement= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
			this.name= classElement.getAttributeValue("name");
			this.superTypeName= classElement.getAttributeValue("extends");
		}
		else
		{
			this.referencedTypes= null;
			this.name= "ConstantPool";
			this.superTypeName= null;
		}
	}

	private static Set<String> extractReferencedTypes(Document xmlvmDocument)
	{
		Set<String> result= new HashSet<String>();
		Element referencesElement= xmlvmDocument.getRootElement().getChild("references", nsXMLVM);
		if (referencesElement == null)
		{
			return result;
		}

		@SuppressWarnings("unchecked")
		List<Element> references= referencesElement.getChildren("reference", nsXMLVM);
		for (Element reference : references)
		{
			result.add(reference.getAttributeValue("name"));
		}
		return result;
	}

	public String toString()
	{
		return getFullName();
	}

	/**
	 * Returns the XMLVM document of this resource.
	 */
	public Document getXmlvmDocument()
	{
		return xmlvmDocument;
	}

	/**
	 * Returns the type of this XMLVM resource.
	 */
	public Type getType()
	{
		return type;
	}

	/**
	 * Returns the name of this XMLVM resource.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the full name, which is the normal name prefixed by the package.
	 */
	public String getFullName()
	{
		String fullResourceName= getPackageName();
		if (!fullResourceName.isEmpty())
		{
			fullResourceName+= ".";
		}
		fullResourceName+= getName();
		return fullResourceName;
	}

	/**
	 * Checks if this class is abstract
	 * 
	 * @return true if it's an abstract class otherwise false
	 */
	public boolean isAbstract()
	{
		Element clazz= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		String flag= clazz.getAttributeValue("isAbstract");
		return flag != null && flag.equals("true");
	}

	/**
	 * Returns the names of all types that are referenced in this resource.
	 */
	public Set<String> getReferencedTypes()
	{
		return referencedTypes;
	}

	/**
	 * Returns the name of the super class type.
	 */
	public String getSuperTypeName()
	{
		return superTypeName;
	}

	/**
	 * Returns the name of the package, this resource is in.
	 * <p>
	 * E.g. "java.lang"
	 */
	public String getPackageName()
	{
		if (type == Type.CONST_POOL)
		{
			return "org.xmlvm";
		}
		Element clazz= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		return clazz.getAttributeValue("package");
	}

	/**
	 * Returns a comma-separated list of interfaces this resources implements.
	 */
	public String getInterfaces()
	{
		Element clazz= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		return clazz.getAttributeValue("interfaces");
	}

	/**
	 * Returns all methods defined in this resource.
	 */
	public List<XmlvmMethod> getMethods()
	{
		List<XmlvmMethod> result= new ArrayList<XmlvmMethod>();
		List<Element> methods= getMethodElements();
		for (Element method : methods)
		{
			result.add(new XmlvmMethod(method));
		}
		return result;
	}

	/**
	 * Returns a sorted list of all methods defined in this resource.
	 */
	public List<XmlvmMethod> getMethodsSorted()
	{
		List<XmlvmMethod> result= getMethods();
		Collections.sort(result, new XmlvmMethodComparator());
		return result;
	}

	/**
	 * Returns all fields defined in this resource.
	 */
	@SuppressWarnings("unchecked")
	public List<XmlvmField> getFields()
	{
		List<XmlvmField> result= new ArrayList<XmlvmField>();
		List<Element> classes= xmlvmDocument.getRootElement().getChildren(TAG_CLASS, nsXMLVM);
		for (Element clazz : classes)
		{
			List<Element> fields= clazz.getChildren("field", nsXMLVM);
			for (Element field : fields)
			{
				result.add(new XmlvmField(field));
			}
		}
		return result;
	}

	/**
	 * Returns whether this resource represents an interface.
	 */
	public boolean isInterface()
	{
		return Boolean.parseBoolean(xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM).getAttributeValue("isInterface"));
	}

	@SuppressWarnings("unchecked")
	// JDOM's non-generic API.
	private List<Element> getMethodElements()
	{
		List<Element> result= new ArrayList<Element>();
		List<Element> classes= xmlvmDocument.getRootElement().getChildren(TAG_CLASS, nsXMLVM);
		for (Element clazz : classes)
		{
			result.addAll(clazz.getChildren("method", nsXMLVM));
		}
		return result;
	}

	/**
	 * Set the XML attribute <code>vtableSize</code> that designates the number
	 * of vtable entries for this class.
	 * 
	 * @param vtableSize
	 *            Size of the vtable.
	 */
	@SuppressWarnings("unchecked")
	// JDOM's non-generic API.
	public void setVtableSize(int vtableSize)
	{
		List<Element> classes= xmlvmDocument.getRootElement().getChildren(TAG_CLASS, nsXMLVM);
		if (classes.size() != 1)
		{
			System.err.println("XmlvmResource.setVtableSize(): cannot deal with multiple classes");
			System.exit(-1);
		}
		classes.get(0).setAttribute("vtableSize", "" + vtableSize);
	}

	/**
	 * Creates an itable for this class.The created itable is essentially a
	 * mapping of methods defined in an interface to methods defined in the
	 * class. The XML markup of the vtable is as follows:
	 * 
	 * <pre>
	 * &lt;itable&gt;
	 *     &lt;vtable-map itableIndex="..." vtableIndex="..."/&gt;
	 *     &lt;vtable-map itableIndex="..." vtableIndex="..."/&gt;
	 *     ...
	 *     &lt;direct-map itableIndex="..." class-type="..." name="..."/&gt;
	 *     &lt;direct-map itableIndex="..." class-type="..." name="..."/&gt;
	 *     ...
	 * &lt/itable&gt;
	 * </pre>
	 * 
	 * @return A newly created itable.
	 */
	public XmlvmItable createItable()
	{
		Element itableElement= new Element("itable", nsXMLVM);
		xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM).addContent(itableElement);
		return new XmlvmItable(itableElement);

	}

	/**
	 * Collects a list of all instructions whose type may need to be adjusted
	 * for a specific set of classes that comprise the application being
	 * cross-compiled (see
	 * {@link org.xmlvm.plugins.c.COutputProcess#adjustTypes()}). Instructions
	 * that need to be considered are invoke-static, invoke-super, iput, iget,
	 * sput, and sget.
	 * 
	 * @param invokeInstructions
	 *            Will be filled with all invoke-static and invoke-super
	 *            instructions upon return.
	 * @param readWriteInstructions
	 *            Will be filled with all iput, iget, sput, and sget
	 *            instructions upon return.
	 */
	@SuppressWarnings("unchecked")
	public void collectInstructions(List<XmlvmInvokeInstruction> invokeInstructions, List<XmlvmMemberReadWrite> readWriteInstructions)
	{
		Element root= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		Iterator<Object> iter= root.getDescendants();
		while (iter.hasNext())
		{
			Object o= iter.next();
			if (o instanceof Element)
			{
				Element elem= (Element) o;
				String name= elem.getName();
				if (name.startsWith("invoke-static") || name.startsWith("invoke-super"))
				{
					invokeInstructions.add(new XmlvmInvokeInstruction(elem));
				}
				if (name.startsWith("iput") || name.startsWith("iget") || name.startsWith("sput") || name.startsWith("sget"))
				{
					readWriteInstructions.add(new XmlvmMemberReadWrite(elem));
				}
			}
		}
	}

	/**
	 * Collects a list of all dex:const-string instructions as well as vm:fields
	 * that have a string initializer.
	 * 
	 * @param constStringInstructions
	 *            Will be filled with all const-string instructions upon return.
	 */
	@SuppressWarnings("unchecked")
	public void collectInstructions(List<XmlvmConstantStringElement> constStringInstructions)
	{
		Element root= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		Iterator<Object> iter= root.getDescendants();
		while (iter.hasNext())
		{
			Object o= iter.next();
			if (o instanceof Element)
			{
				Element elem= (Element) o;
				if (elem.getName().equals("const-string"))
				{
					constStringInstructions.add(new XmlvmConstantStringElement(elem));
				}
				if (elem.getName().equals("field") && elem.getAttributeValue("type").equals("java.lang.String") && elem.getAttributeValue("value") != null)
				{
					constStringInstructions.add(new XmlvmConstantStringElement(elem));
				}
			}
		}
	}

	public static XmlvmResource fromFile(OutputFile file)
	{
		Document doc= null;
		SAXBuilder builder= new SAXBuilder();
		try
		{
			doc= builder.build(new ByteArrayInputStream(file.getDataAsBytes()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return new XmlvmResource(org.xmlvm.proc.XmlvmResource.Type.DEX, doc);
	}

	/**
	 * Creates a &lt;vm:inmplementsInterface&gt; in the vm:class which is used
	 * by xmlvm2c.xsl to initialize the implementedInterface array
	 * 
	 * @param fullName
	 */
	public void createImplementsInterface(String fullName)
	{
		Element implementsInterfaceElement= new Element("implementsInterface", nsXMLVM);
		implementsInterfaceElement.setAttribute("name", fullName);
		xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM).addContent(implementsInterfaceElement);
	}

	/**
	 * Adds a public static &lt;vm:field&gt; element, corresponding to the given
	 * field of the given interface, implemented by the class represented by
	 * this resource.
	 * 
	 * This method has no effect on resources which do not represent a
	 * class/interface (e.g. constant pool).
	 * 
	 * This method is to be used by backends which do not support interface
	 * fields (as of now only C#). 
	 * 
	 * @param definingInterface
	 *            the topmost interface where the given field is defined.
	 * @param field
	 *            field which will be added to the class element of the
	 *            xmlvmDocument
	 */
	public void addInterfaceField(String definingInterface, XmlvmField field)
	{
		Element classElement= xmlvmDocument.getRootElement().getChild("class", nsXMLVM);
		if (classElement == null)
		{
			return;
		}
		Element interfaceFieldElement= new Element("field", nsXMLVM);
		interfaceFieldElement.setAttribute("name", field.getName());
		interfaceFieldElement.setAttribute("type", field.getType());
		interfaceFieldElement.setAttribute("isPublic", "true");
		interfaceFieldElement.setAttribute("isStatic", "true");
		interfaceFieldElement.setAttribute("definingInterface", definingInterface);
		classElement.addContent(interfaceFieldElement);
	}

	/**
	 * Sets a tag to a given value.
	 * 
	 * @param tag
	 *            The tag to set.
	 * @param tagValue
	 *            The value of the tag.
	 */
	public void setTag(Tag tag, String tagValue)
	{
		tags.put(tag, tagValue);
	}

	/**
	 * Returns whether this resource contains the given tag.
	 * 
	 * @param tag
	 *            The tag to check.
	 */
	public boolean hasTag(Tag tag)
	{
		return tags.containsKey(tag);
	}

	/**
	 * Returns the value of the tag with the given tag.
	 * 
	 * @param tag
	 *            The tag to get.
	 */
	public String getTagValue(Tag tag)
	{
		return tags.get(tag);
	}

	/**
	 * @return the size of the interface table
	 */
	public Integer getInterfaceTableSize()
	{
		Element clazz= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		String interfaceTableSize= clazz.getAttributeValue(ATTRIBUTE_INTERFACE_TABLE_SIZE);
		return interfaceTableSize == null ? null : Integer.parseInt(interfaceTableSize);
	}

	/**
	 * @param interfaceTableSize
	 *            the size of the interface table to set
	 */
	public void setInterfaceTableSize(Integer interfaceTableSize)
	{
		Element clazz= xmlvmDocument.getRootElement().getChild(TAG_CLASS, nsXMLVM);
		clazz.setAttribute(ATTRIBUTE_INTERFACE_TABLE_SIZE, interfaceTableSize == null ? null : String.valueOf(interfaceTableSize));
	}
}
