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

package org.xmlvm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.XmlvmResource.Type;
import org.xmlvm.proc.XmlvmResource.XmlvmInvokeInstruction;
import org.xmlvm.proc.XmlvmResource.XmlvmMemberReadWrite;
import org.xmlvm.proc.XmlvmResource.XmlvmMethod;
import org.xmlvm.proc.lib.LibraryLoader;
import org.xmlvm.util.comparators.ClassNameComparator;
import org.xmlvm.util.comparators.XmlvmMethodComparator;

/**
 * This class helps the VtableOutputProcess in loading/maintaining/retrieving
 * classes in the object hierarchy. The whole object hierarchy is loaded into a
 * graph structure to ease traversal
 */
public class ObjectHierarchyHelper
{

	private static final String TAG= ObjectHierarchyHelper.class.getSimpleName();
	private static final int CHILD= 0;
	private static final int PARENT= 1;

	private GraphNode root= new GraphNode();

	/**
	 * Map containing all nodes of the object hierarchy tree
	 */
	private Map<String, GraphNode> treeIndex= new HashMap<String, GraphNode>();

	/**
	 * Resources loaded through the xmlvm pipeline until the creation of the
	 * hierarchy helper
	 */
	private Map<String, XmlvmResource> preloadedResources= new HashMap<String, XmlvmResource>();

	/**
	 * Graph containing all interfaces as nodes and edges between them in case
	 * they are implemented by the same object
	 */
	private Map<String, ColoredGraphNode> conflictGraph= new HashMap<String, ColoredGraphNode>();

	private Arguments arguments;

	public ObjectHierarchyHelper(Map<String, XmlvmResource> resourcePool, Arguments arguments)
	{
		this.arguments= arguments;

		preloadedResources= resourcePool;

		// Insert all preloaded resources
		for (XmlvmResource resource : resourcePool.values())
		{
			if (resource.getType() != Type.CONST_POOL)
			{
				insertResource(resource);
			}
		}

		// Insert all referenced resources
		for (XmlvmResource resource : resourcePool.values())
		{
			if (resource.getType() == Type.CONST_POOL)
			{
				continue;
			}

			// Insert all resources referenced by vtable instructions into
			// the tree
			for (XmlvmMethod method : resource.getMethods())
			{
				for (XmlvmInvokeInstruction instruction : method.getVtableInvokeInstructions())
				{
					String className= instruction.getClassType();
					if (className.indexOf("[]") != -1)
					{
						className= "java.lang.Object";
					}
					getXmlvmResource(className);
				}
			}

			// Insert all resources referenced by invoke-super,
			// invoke-static and
			// member read/write instructions into the tree
			List<XmlvmInvokeInstruction> invokeInstructions= new ArrayList<XmlvmInvokeInstruction>();
			List<XmlvmMemberReadWrite> memberReadWriteInstructions= new ArrayList<XmlvmMemberReadWrite>();
			resource.collectInstructions(invokeInstructions, memberReadWriteInstructions);
			for (XmlvmInvokeInstruction instr : invokeInstructions)
			{
				String classType= instr.getClassType();
				getXmlvmResource(classType);
			}
			for (XmlvmMemberReadWrite instr : memberReadWriteInstructions)
			{
				String classType= instr.getClassType();
				getXmlvmResource(classType);
			}
		}
		Log.debug(TAG, "Done building object tree");
	}

	/**
	 * Checks if a method is overridden in any parent or child classes
	 * 
	 * @param resource
	 *            The class the method belongs to
	 * @param method
	 *            Method which needs to be checked for overrides
	 * @return True if method is overridden otherwise false
	 */
	public boolean isOverridden(String fullName, XmlvmMethod method)
	{
		GraphNode node= getNode(fullName);
		return isOverridden(node, method, CHILD) || isOverridden(node, method, PARENT);
	}

	/**
	 * Checks if a method is overriding any parent class method
	 * 
	 * @param resource
	 *            The class the method belongs to
	 * @param method
	 *            Method which needs to be checked for overriding
	 * @return True if method is overriding a parent class method otherwise false
	 */
	public boolean isOverridding(String fullName, XmlvmMethod method)
	{
		GraphNode node= getNode(fullName);
		Set<GraphNode> toCheck= node.getParents();

		for (GraphNode current : toCheck)
		{
			// Check if method is overridden
			XmlvmResource childClass= current.getResource();
			if (!childClass.isInterface())
			{
				for (XmlvmMethod each : childClass.getMethods())
				{
					if (each.doesOverrideMethod(method))
					{
						return true;
					}
				}

				// Recursively call for all children
				if (isOverridding(childClass.getFullName(), method))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if a method is overridden
	 * 
	 * @param node
	 *            The class the method belongs to
	 * @param method
	 *            Method which needs to be checked for overrides
	 * @param parentOrChild
	 *            Should be either CHILD or PARENT (0 or 1) to determine in
	 *            which direction the graph should be searched
	 * @return
	 */
	private boolean isOverridden(GraphNode node, XmlvmMethod method, int parentOrChild)
	{
		Set<GraphNode> toCheck;
		if (parentOrChild == CHILD)
		{
			toCheck= node.getChildren();
		}
		else
		{
			toCheck= node.getParents();
		}

		for (GraphNode current : toCheck)
		{
			// Check if method is overridden
			XmlvmResource childClass= current.getResource();
			for (XmlvmMethod each : childClass.getMethods())
			{
				if (each.doesOverrideMethod(method))
				{
					return true;
				}
			}
			// Recursively call for all children
			if (isOverridden(current, method, parentOrChild))
			{
				return true;
			}
		}
		return false;

	}

	/**
	 * Get a XmlvmResource from the object hierarchy. The XmlvmResource will be
	 * loaded through the library loader if it wasn't already loaded
	 * 
	 * @param fullName
	 *            The full name of the resource (e.g. java.lang.String)
	 * @return XmlvmResource
	 */
	public XmlvmResource getXmlvmResource(String fullName)
	{
		GraphNode graphNode= getNode(fullName);
		return graphNode == null ? null : graphNode.getResource();
	}

	/**
	 * Get the GraphNode of an XmlvmResource. Load the resource if it isn't
	 * already in the graph
	 * 
	 * @param fullName
	 *            XmlvmResource of the class you want to insert
	 * @param root
	 *            Root node of the object hierarchy tree
	 */
	private GraphNode getNode(String fullName)
	{
		if (!treeIndex.containsKey(fullName))
		{
			XmlvmResource resource= loadResource(fullName);
			if (resource != null)
			{
				insertResource(resource);
			}
			else
			{
				Log.error("Couldn't create node for " + fullName);
			}
		}
		return treeIndex.get(fullName);
	}

	/**
	 * Insert a class and all its super classes and interfaces into the object
	 * hierarchy tree
	 * 
	 * @param resource
	 *            Resource to insert into the graph
	 * @return GraphNode in which the resource was inserted into the graph
	 */
	private GraphNode insertResource(XmlvmResource resource)
	{
		String fullName= resource.getFullName();
		if (!treeIndex.containsKey(fullName))
		{
			Log.debug(TAG, "Inserting " + resource.getFullName() + " into object tree");
			GraphNode newNode= null;
			if (resource.isInterface())
			{
				newNode= insertInterface(resource);
			}
			else
			{
				newNode= insertClass(resource);
				addInterfaces(newNode, resource);
			}
			treeIndex.put(resource.getFullName(), newNode);
			return newNode;
		}
		else
		{
			return treeIndex.get(fullName);
		}
	}

	/**
	 * Insert a class (not an interfacce!) into the object hierarchy
	 * 
	 * @param resource
	 *            Resource to insert into the graph
	 * @return GraphNode in which the resource was inserted into the graph
	 */
	private GraphNode insertClass(XmlvmResource resource)
	{
		String superType= resource.getSuperTypeName();

		// If we don't have a super type (=> java.lang.Object) we are the root
		if (superType == null || superType.equals(""))
		{
			root.setResource(resource);
			return root;
		}
		else
		{
			// Find the base class in the tree and add yourself as a child
			GraphNode superNode= getNode(superType);
			GraphNode newNode= new GraphNode(resource);
			superNode.add(newNode);
			return newNode;
		}
	}

	/**
	 * Insert an interface into the object hierarchy. Even though all interfaces
	 * extend java.lang.Object in the end there is no link between
	 * java.lang.Object and the interface added.
	 * 
	 * @param resource
	 *            Resource to insert into the graph
	 * @return GraphNode in which the resource was inserted into the graph
	 */
	private GraphNode insertInterface(XmlvmResource resource)
	{
		GraphNode newNode= new GraphNode(resource);
		addInterfaces(newNode, resource);
		return newNode;
	}

	/**
	 * Insert interfaces into the object hierarchy which are extended by another
	 * interface or class
	 *
	 * @param newNode GraphNode in which to insert the resource into the graph
	 * @param resource Resource to insert into the graph
	 */
	private void addInterfaces(GraphNode newNode, XmlvmResource resource)
	{
		String interfaces= resource.getInterfaces();
		if (interfaces != null && !interfaces.equals(""))
		{
			for (String interfaceName : interfaces.split("\\,"))
			{
				// Try to find the interface in the existing tree
				GraphNode interfaceNode= getNode(interfaceName);
				interfaceNode.add(newNode);
			}
		}
	}

	/**
	 * Check if the resource was already loaded through the pipeline before the
	 * ObjectHierarchyHelper was created or load the class through the
	 * LibraryLoader otherwise
	 * 
	 * @param fullName
	 *            Full name of the class (e.g. java.lang.String)
	 * @return XmlvmResource of the class
	 */
	private XmlvmResource loadResource(String fullName)
	{
		if (preloadedResources.containsKey(fullName))
		{
			return preloadedResources.get(fullName);
		}
		else
		{
			Log.debug(TAG, "Loading JDK class: " + fullName);
			LibraryLoader loader= new LibraryLoader(arguments);
			XmlvmResource resource= loader.load(fullName);
			return resource;
		}
	}

	/**
	 * Load all children of a class recursively
	 * 
	 * @param fullName
	 *            Full name of the class (e.g. java.lang.String)
	 * @return Set containing all children of the class as XmlvmResource
	 */
	public Set<XmlvmResource> getChildrenRecursive(String fullName)
	{
		Set<GraphNode> children= getChildrenRecursive(getNode(fullName));
		Set<XmlvmResource> ret= new HashSet<XmlvmResource>();
		for (GraphNode node : children)
		{
			ret.add(node.getResource());
		}
		return ret;
	}

	/**
	 * Load all children of a class recursively
	 * 
	 * @param node
	 *            GraphNode of the class
	 * @return Set containing the GraphNodes for all children of the class
	 */
	private Set<GraphNode> getChildrenRecursive(GraphNode node)
	{
		Set<GraphNode> children= new HashSet<GraphNode>();
		for (GraphNode parent : node.getChildren())
		{
			children.add(parent);
			children.addAll(getChildrenRecursive(parent));
		}
		return children;
	}

	/**
	 * Load all parents of a class recursively
	 * 
	 * @param fullName
	 *            Full name of the class (e.g. java.lang.String)
	 * @return Set containing all parents of the class as XmlvmResource
	 */
	public Set<XmlvmResource> getParentsRecursive(String fullName)
	{
		Set<GraphNode> parents= getParentsRecursive(getNode(fullName));
		Set<XmlvmResource> ret= new HashSet<XmlvmResource>();
		for (GraphNode node : parents)
		{
			ret.add(node.getResource());
		}
		return ret;
	}

	/**
	 * Load all parents of a class recursively
	 * 
	 * @param node
	 *            GraphNode of the class
	 * @return Set containing the GraphNodes for all parents of the class
	 */
	private Set<GraphNode> getParentsRecursive(GraphNode node)
	{
		Set<GraphNode> parents= new HashSet<GraphNode>();
		for (GraphNode parent : node.getParents())
		{
			parents.add(parent);
			parents.addAll(getParentsRecursive(parent));
		}
		return parents;
	}

	/**
	 * Load all interfaces of a class recursively
	 * 
	 * @param fullName
	 *            Full name of the class (e.g. java.lang.String)
	 * @return Set containing all interfaces of the class as XmlvmResource
	 */
	public Set<XmlvmResource> getInterfacesRecursive(String fullName)
	{
		Set<GraphNode> interfaces= getParentsRecursive(getNode(fullName));
		Set<XmlvmResource> ret= new HashSet<XmlvmResource>();
		for (GraphNode node : interfaces)
		{
			if (node.getResource().isInterface())
			{
				ret.add(node.getResource());
			}
		}
		return ret;
	}

	/**
	 * If an abstract class implements an interface and the method is not
	 * implemented or declared in the class or parent classes redeclare it. This
	 * makes sure a vtable entry gets created in cases like the following:
	 * 
	 * interface X { x(): } abstract class A implements X {} class B extends A
	 * {}
	 * 
	 * interface X { x(): } interface A extends X {} class C implements A {}
	 * 
	 * For this call we need an index #define XMLVM_ITABLE_IDX_B_a__;
	 * 
	 * Java doesn't force redeclaration of the interface method x() in class A
	 * because it's abstract but to support this call
	 * 
	 * A a = new B(); a.x();
	 * 
	 * we need to create an vtable index of this method in A. To achieve that we
	 * explicitly redeclare the interface method in A.
	 */
	public void redeclareInterfaceMethodsInAbstractClasses()
	{
		for (GraphNode node : treeIndex.values())
		{
			if (node.getResource().isAbstract())
			{

				Set<XmlvmResource> interfaces= getInterfacesRecursive(node.getResource().getFullName());
				Set<XmlvmMethod> interfaceMethods= new HashSet<XmlvmMethod>();
				for (XmlvmResource ifaceResource : interfaces)
				{
					interfaceMethods.addAll(ifaceResource.getMethods());
				}

				Set<GraphNode> parents= getParentsRecursive(node);
				Set<XmlvmMethod> classMethods= new HashSet<XmlvmMethod>();
				classMethods.addAll(node.getResource().getMethods());
				for (GraphNode parent : parents)
				{
					if (!parent.getResource().isInterface())
					{
						classMethods.addAll(parent.getResource().getMethods());
					}
				}

				List<XmlvmMethod> methods= new ArrayList<XmlvmMethod>(interfaceMethods);
				Collections.sort(methods, new XmlvmMethodComparator());

				outer: for (XmlvmMethod ifaceMethod : methods)
				{
					for (XmlvmMethod classMethod : classMethods)
					{
						if (ifaceMethod.doesOverrideMethod(classMethod))
						{
							continue outer;
						}
					}
					classMethods.add(ifaceMethod);
					node.getResource().addMethod(ifaceMethod).setSynthetic(true);
				}
			}
		}
	}

	/**
	 * Calculates valid interface indices for all interface methods through
	 * selector coloring. This method is described here:
	 * 
	 * R. Dixon, T. McKee, M. Vaughan, and P. Schweizer. 1989. A fast method
	 * dispatcher for compiled languages with multiple inheritance. SIGPLAN Not.
	 * 24, 10 (September 1989), 211-214. DOI=10.1145/74878.74900
	 * http://doi.acm.org/10.1145/74878.74900
	 */
	public void calculateInterfaceIndices()
	{
		buildConflictGraph();
		Log.debug(TAG, "Built interface conflict graph containing " + conflictGraph.size() + " interfaces");
		int lastStartingNumber= colorConflictGraph();
		Log.debug(TAG, "Colored graph with highest starting index " + lastStartingNumber);
		verifyConflictGraph();
		Log.debug(TAG, "Verified correctness of coloring for " + conflictGraph.size() + " interfaces");
		Log.debug(TAG, "Done calculating interface indices");
	}

	/**
	 * Build a conflict graph where every node represents an interface and every
	 * edge between two nodes represents that two classes/interfaces implement
	 * this interface and therefore there is a conflict
	 */
	private void buildConflictGraph()
	{
		for (GraphNode node : treeIndex.values())
		{
			String fullName= node.getResource().getFullName();
			if (node.getResource().isInterface())
			{
				ColoredGraphNode newNode= new ColoredGraphNode(node.getResource());
				conflictGraph.put(fullName, newNode);
				Log.debug(TAG, "Added " + fullName + " to conflict graph");
			}
		}
		for (GraphNode node : treeIndex.values())
		{
			processNodeForConflictGraph(node);
		}
	}

	/**
	 * Load all interfaces of a node and create edges between them
	 * 
	 * @param node
	 *            Node to process
	 */
	private void processNodeForConflictGraph(GraphNode node)
	{
		// Add the node to the conflict graph if we don't already have it
		List<ColoredGraphNode> conflictingInterfaces= new ArrayList<ColoredGraphNode>();
		for (XmlvmResource interfaceResource : getInterfacesRecursive(node.getResource().getFullName()))
		{
			String fullName= interfaceResource.getFullName();
			conflictingInterfaces.add(conflictGraph.get(fullName));
		}

		for (ColoredGraphNode connectFromNode : conflictingInterfaces)
		{
			for (ColoredGraphNode connectToNode : conflictingInterfaces)
			{
				if (!connectFromNode.getResource().getFullName().equals(connectToNode.getResource().getFullName()))
				{
					connectToNode.add(connectFromNode);
				}
			}
		}
	}

	/**
	 * Compare ColoredGraphNodes by their resource's full class name, sorting
	 * core library packages first.
	 */
	private static class ColoredGraphNodeComparator implements Comparator<ColoredGraphNode>
	{
		private static Comparator<String> comparator= new ClassNameComparator();

		public int compare(ColoredGraphNode n1, ColoredGraphNode n2)
		{
			return comparator.compare(n1.getResource().getFullName(), n2.getResource().getFullName());
		}
	}

	/**
	 * Color the graph (assign indices to all interface methods) starting with
	 * interfaces in core/priority packages, and then sorting by class name
	 */
	private int colorConflictGraph()
	{
		int maxColor= 0;
		Collection<ColoredGraphNode> values= conflictGraph.values();
		List<ColoredGraphNode> orderedNodes= new ArrayList<ColoredGraphNode>();
		for (ColoredGraphNode node : values)
		{
			orderedNodes.add(node);
		}

		Collections.sort(orderedNodes, new ColoredGraphNodeComparator());

		for (ColoredGraphNode node : orderedNodes)
		{
			int nodeMaxColor= colorNode(node);
			maxColor= Math.max(nodeMaxColor, maxColor);
		}
		return maxColor;
	}

	/**
	 * Color a specific node in the class. To pick the color for a method use
	 * the lowest available index in the neighborhood of the node
	 * 
	 * @param node
	 *            Node to be colored
	 */
	private int colorNode(ColoredGraphNode node)
	{
		List<Integer> usedColors= new ArrayList<Integer>();
		for (ColoredGraphNode neighbor : node.getNeighbors())
		{
			usedColors.addAll(neighbor.getColors());
		}

		int current= 0;
		List<XmlvmMethod> methods= node.getResource().getMethodsSorted();
		for (XmlvmMethod method : methods)
		{
			if (method.isStatic())
			{
				node.getColors().add(-1);
			}
			else
			{
				while (usedColors.contains(current))
				{
					current++;
				}
				node.getColors().add(current);
				current++;
			}
		}
		return current;
	}

	/**
	 * Verify that the coloring algorithm produced a valid coloring by checking
	 * color intersections between nodes and that every method was assigned a
	 * color
	 */
	public void verifyConflictGraph()
	{
		for (ColoredGraphNode node : conflictGraph.values())
		{
			if (node.getResource().getMethods().size() != node.getColors().size())
			{
				throw new RuntimeException("Not every method in interface " + node.getResource().getFullName() + " is assigned a color!");
			}

			List<Integer> usedColors= new ArrayList<Integer>();
			for (ColoredGraphNode neighbor : node.getNeighbors())
			{
				usedColors.addAll(neighbor.getColors());
			}

			for (int currentColor : node.getColors())
			{
				if (usedColors.contains(currentColor))
				{
					throw new RuntimeException("Color conflict with neighbor in interface " + node.getResource().getFullName() + "!");
				}
			}
		}
	}

	/**
	 * Compare GraphNodes by their resource's full name, sorting core library
	 * packages first.
	 */
	private static class GraphNodeComparator implements Comparator<GraphNode>
	{
		private static Comparator<String> comparator= new ClassNameComparator();

		public int compare(GraphNode n1, GraphNode n2)
		{
			return comparator.compare(n1.getResource().getFullName(), n2.getResource().getFullName());
		}
	}

	/**
	 * Set interface table information. Specifically, set the interface table
	 * size on XmlvmResources and the interface table index on XmlvmMethods
	 */
	public void computeInterfaceTableInformation()
	{
		int itableSizeTotal= 0, itableCount= 0;

		List<GraphNode> nodes= new ArrayList<GraphNode>(treeIndex.values());
		Collections.sort(nodes, new GraphNodeComparator());
		for (GraphNode node : nodes)
		{
			if (!node.getResource().isInterface())
			{
				Set<XmlvmResource> interfaces= getInterfacesRecursive(node.getResource().getFullName());
				int maxIndex= -1;
				for (XmlvmResource iface : interfaces)
				{
					List<XmlvmMethod> ifaceMethods= iface.getMethodsSorted();
					for (XmlvmMethod m : ifaceMethods)
					{
						maxIndex= Math.max(maxIndex, getInterfaceIndex(iface, m, ifaceMethods));
					}
				}
				maxIndex++;
				node.getResource().setInterfaceTableSize(Integer.valueOf(maxIndex));

				itableCount++;
				itableSizeTotal+= maxIndex;
			}
		}

		Log.debug(TAG, "The average itable size is " + ((double) itableSizeTotal) / itableCount);

		List<ColoredGraphNode> orderedNodes= new ArrayList<ColoredGraphNode>(conflictGraph.values());
		Collections.sort(orderedNodes, new ColoredGraphNodeComparator());
		for (ColoredGraphNode node : orderedNodes)
		{
			// Add all symbols for own methods
			computeMethodItableIndex(node);
		}
	}

	/**
	 * Determine the interface table index, if any, for the node's resource's methods
	 * @param node Node for which we want to retrieve the interface indices
	 */
	private void computeMethodItableIndex(ColoredGraphNode node)
	{
		int index= 0;
		List<XmlvmMethod> methods= node.getResource().getMethodsSorted();
		for (XmlvmMethod method : methods)
		{
			if (method.isStatic())
			{
				continue;
			}
			int color= node.getColors().get(index);
			method.setInterfaceTableIndex(Integer.valueOf(color));
			index++;
		}
	}

	/**
	 * Get the interface index for a method
	 * 
	 * @param iface
	 *            Interface containing the method
	 * @param m
	 *            XmlvmMethod for which we want the index
	 * @param ifaceMethods
	 *            Sorted methods of iface
	 * @return Interface index of the method
	 */
	public int getInterfaceIndex(XmlvmResource iface, XmlvmMethod m, List<XmlvmMethod> ifaceMethods)
	{
		ColoredGraphNode node= conflictGraph.get(iface.getFullName());
		int interfaceIndex= -1;
		for (int i= 0; i < ifaceMethods.size(); i++)
		{
			if (ifaceMethods.get(i).doesOverrideMethod(m))
			{
				interfaceIndex= node.getColors().get(i);
				break;
			}
		}

		String parameterList= "";
		for (String parameter : m.getParameterTypes())
		{
			parameterList+= parameter + " ";
		}
		Log.debug(TAG, "Interface index for " + iface.getFullName() + " " + m.getName() + "(" + parameterList + ") = " + interfaceIndex);
		return interfaceIndex;
	}

	/**
	 * Escape a Java resource name to what it will be named in C
	 * @param resourceName the resource name
	 * @return an escaped name of the resource
	 */
	public static String escapeName(String resourceName)
	{
		return resourceName.replace('.', '_').replace('$', '_');
	}

	/**
	 * Returns a name-mangled signature of the given parameterTypes.
	 * 
	 * @return
	 */
	public static String getParameterString(List<String> parameterTypes)
	{
		StringBuilder parameterString= new StringBuilder(parameterTypes.size() * 16);//average of tutorial
		if (parameterTypes.size() > 0)
		{
			for (String parameter : parameterTypes)
			{
				parameterString.append("_");
				int i= parameter.indexOf('[');
				// dim == number of dimensions
				int dim= (i == -1) ? 0 : (parameter.length() - i) / 2;
				parameter= parameter.replaceAll("\\[\\]", "");
				parameter= escapeName(parameter);
				parameterString.append(parameter);
				if (dim > 0)
				{
					parameterString.append("_").append(dim).append("ARRAY");
				}
			}
		}
		return parameterString.toString();
	}

	/**
	 * Class representing a vertex containing a XmlvmResource in a directed
	 * Graph
	 */
	private class GraphNode
	{

		private XmlvmResource resource;
		private Set<GraphNode> children= new HashSet<GraphNode>();
		private Set<GraphNode> parents= new HashSet<GraphNode>();

		public GraphNode()
		{
		}

		public GraphNode(XmlvmResource resource)
		{
			this.resource= resource;
		}

		public XmlvmResource getResource()
		{
			return this.resource;
		}

		public void setResource(XmlvmResource resource)
		{
			this.resource= resource;
		}

		public Set<GraphNode> getChildren()
		{
			return this.children;
		}

		public Set<GraphNode> getParents()
		{
			return this.parents;
		}

		public void add(GraphNode newNode)
		{
			children.add(newNode);
			newNode.parents.add(this);
		}

	}

	/**
	 * Class representing a vertex containing a XmlvmResource in an undirected
	 * Graph being able to store multiple colors (Integer) per vertex
	 */
	private class ColoredGraphNode
	{

		private XmlvmResource resource;
		private Set<ColoredGraphNode> neighbors= new HashSet<ColoredGraphNode>();
		private List<Integer> colors= new ArrayList<Integer>();

		public ColoredGraphNode(XmlvmResource resource)
		{
			this.resource= resource;
		}

		public List<Integer> getColors()
		{
			return colors;
		}

		public XmlvmResource getResource()
		{
			return this.resource;
		}

		public Set<ColoredGraphNode> getNeighbors()
		{
			return this.neighbors;
		}

		public void add(ColoredGraphNode newNode)
		{
			neighbors.add(newNode);
			newNode.neighbors.add(this);
		}

	}

}
