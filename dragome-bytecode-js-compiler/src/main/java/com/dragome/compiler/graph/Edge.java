/*
 * Copyright 2005 by Wolfgang Kuehn
 * Created on 12.11.2005
 */
package com.dragome.compiler.graph;

public class Edge
{

	private Graph graph;

	public Node source;

	public Node target;

	public EdgeType type;

	private Node orgSource;

	Edge(Graph theGraph, Node theSource, Node theTarget)
	{
		graph= theGraph;
		source= theSource;
		target= theTarget;
	}

	public boolean equals(Object other)
	{
		if (other == null || !(other instanceof Edge))
			return false;
		Edge otherEdge= (Edge) other;
		return source.getId().equals(otherEdge.source.getId()) && target.getId().equals(otherEdge.target.getId());
	}

	public void reroot(Node newSource)
	{
		source.outEdges.remove(this);
		newSource.outEdges.add(this);
		source= newSource;
	}

	public void redirect(Node newTarget)
	{
		target.inEdges.remove(this);
		newTarget.inEdges.add(this);
		target= newTarget;
	}

	public boolean isBackEdge()
	{
		return target.isDomAncestor(source);
	}

	public boolean isGlobal()
	{
		return orgSource != null;
	}

	public String toString()
	{
		String s= getClass().getName();

		s= s.substring(s.lastIndexOf('.') + 1);
		return s + " " + source.getId() + " -> " + target.getId();
	}

	public Node getOrgSource()
	{
		if (orgSource == null)
		{
			return source;
		}
		return orgSource;
	}

	public void setOrgSource(Node theOrgSource)
	{
		orgSource= theOrgSource;
	}

}
