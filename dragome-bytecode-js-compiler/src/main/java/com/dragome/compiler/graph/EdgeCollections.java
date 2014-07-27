package com.dragome.compiler.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class EdgeCollections
{

	public static Set<Node> getSources(Collection<Edge> edges)
	{
		Set<Node> sources= new LinkedHashSet<Node>();
		for (Edge edge : edges)
		{
			sources.add(edge.source);
		}
		return sources;
	}

}
