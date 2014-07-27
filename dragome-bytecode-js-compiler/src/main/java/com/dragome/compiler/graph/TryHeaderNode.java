package com.dragome.compiler.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dragome.compiler.ast.TryStatement;

public class TryHeaderNode extends Node
{

	public TryStatement tryStmt;

	public TryHeaderNode(Graph theGraph)
	{
		super(theGraph);
	}

	public void setTryBody(Node theTryBody)
	{
		Edge edge= graph.addEdge(this, theTryBody);
		edge.type= EdgeType.TRYBODY;
	}

	public void setFinallyNode(Node theFinallyNode)
	{
		Edge edge= graph.addEdge(this, theFinallyNode);
		edge.type= EdgeType.FINALLY;

	}

	public void addCatchNode(Node node)
	{
		Edge edge= graph.addEdge(this, node);
		edge.type= EdgeType.CATCH;

	}

	public Node getSuccessor()
	{
		Iterator iter= outEdges.iterator();
		while (iter.hasNext())
		{
			Edge edge= (Edge) iter.next();
			if (edge.type == null)
				return edge.target;
		}
		return null;
	}

	public Node getTryBody()
	{
		Iterator iter= outEdges.iterator();
		while (iter.hasNext())
		{
			Edge edge= (Edge) iter.next();
			if (edge.type == EdgeType.TRYBODY)
				return edge.target;
		}
		throw new RuntimeException();
	}

	public Node getFinallyNode()
	{
		Iterator iter= outEdges.iterator();
		while (iter.hasNext())
		{
			Edge edge= (Edge) iter.next();
			if (edge.type == EdgeType.FINALLY)
				return edge.target;
		}
		return null;
	}

	public List<Node> getCatchNodes()
	{
		List<Node> catchNodes= new ArrayList<Node>();
		for (Edge edge : outEdges)
		{
			if (edge.type == EdgeType.CATCH)
				catchNodes.add(edge.target);
		}

		return catchNodes;
	}

	public TryStatement getTryStatement()
	{
		return tryStmt;
	}

}
