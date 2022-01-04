package com.dragome.web.helpers;

import java.util.Stack;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XPathGenerator
{
	public static String getFullXPath(Node n)
	{
		// abort early
		if (null == n)
			return null;

		// declarations
		Node parent= null;
		Stack<Node> hierarchy= new Stack<Node>();
		StringBuffer buffer= new StringBuffer();

		// push element on stack
		hierarchy.push(n);

		switch (n.getNodeType())
		{
			case Node.ATTRIBUTE_NODE:
				parent= ((Attr) n).getOwnerElement();
				break;
			case Node.ELEMENT_NODE:
				parent= n.getParentNode();
				break;
			case Node.DOCUMENT_NODE:
				parent= n.getParentNode();
				break;
			case Node.TEXT_NODE:
				parent= n.getParentNode();
				break;
			default:
				return null;
//				throw new IllegalStateException("Unexpected Node type" + n.getNodeType());
		}

		while (null != parent && parent.getNodeType() != Node.DOCUMENT_NODE)
		{
			// push on stack
			hierarchy.push(parent);

			// get parent of parent
			parent= parent.getParentNode();
		}

		// construct xpath
		Object obj= null;
		while (!hierarchy.isEmpty() && null != (obj= hierarchy.pop()))
		{
			Node node= (Node) obj;
			boolean handled= false;

			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element e= (Element) node;

				// is this the root element?
				if (buffer.length() == 0)
				{
					// root element - simply append element name
					buffer.append(node.getNodeName());
				}
				else
				{
					// child element - append slash and element name
					buffer.append("/");
					buffer.append(node.getNodeName());

					if (false && node.hasAttributes())
					{
						// see if the element has a name or id attribute
						if (e.hasAttribute("id"))
						{
							// id attribute found - use that
							buffer.append("[@id='" + e.getAttribute("id") + "']");
							handled= true;
						}
						else if (e.hasAttribute("name"))
						{
							// name attribute found - use that
							buffer.append("[@name='" + e.getAttribute("name") + "']");
							handled= true;
						}
					}

					if (!handled)
					{
						// no known attribute we could use - get sibling index
						int prev_siblings= 1;
						Node prev_sibling= node.getPreviousSibling();
						while (null != prev_sibling)
						{
							if (prev_sibling.getNodeType() == node.getNodeType())
							{
								if (prev_sibling.getNodeName().equalsIgnoreCase(node.getNodeName()))
								{
									prev_siblings++;
								}
							}
							prev_sibling= prev_sibling.getPreviousSibling();
						}
						buffer.append("[" + prev_siblings + "]");
					}
				}
			}
			else if (node.getNodeType() == Node.ATTRIBUTE_NODE)
			{
				buffer.append("/@");
				buffer.append(node.getNodeName());
			}
			else if (node.getNodeType() == Node.TEXT_NODE)
			{
				buffer.append("/text()");
			}
		}
		// return buffer
		return buffer.toString();
	}
}
