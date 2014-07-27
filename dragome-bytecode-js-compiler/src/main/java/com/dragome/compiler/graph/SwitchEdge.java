package com.dragome.compiler.graph;

import java.util.ArrayList;
import java.util.List;

import com.dragome.compiler.ast.NumberLiteral;

public class SwitchEdge extends Edge
{
	public List<NumberLiteral> expressions= new ArrayList<NumberLiteral>();

	SwitchEdge(Graph graph, Node theSource, Node theTarget)
	{
		super(graph, theSource, theTarget);
	}
}
