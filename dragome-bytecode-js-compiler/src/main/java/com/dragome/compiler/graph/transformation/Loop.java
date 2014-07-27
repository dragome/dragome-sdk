package com.dragome.compiler.graph.transformation;

import java.util.Iterator;
import java.util.Set;

import com.dragome.compiler.ast.Block;
import com.dragome.compiler.ast.BooleanLiteral;
import com.dragome.compiler.ast.WhileStatement;
import com.dragome.compiler.graph.Edge;

public class Loop extends Transformation
{

	private Set selfEdges;

	public Loop()
	{
		selfEdges= null;
	}

	public boolean applies_()
	{
		return header.hasSelfEdges();
	}

	public void apply_()
	{
		selfEdges= graph.removeSelfEdges(header);
	}

	void rollOut_(Block block)
	{
		WhileStatement loopStmt= new WhileStatement();
		Block loopBody= new Block();
		loopStmt.setBlock(loopBody);
		loopStmt.setExpression(new BooleanLiteral(true));

		block.appendChild(loopStmt);

		Iterator iter= selfEdges.iterator();
		while (iter.hasNext())
		{
			Edge edge= (Edge) iter.next();
			if (!edge.isGlobal())
				continue;
			loopStmt.isLabeled();
			produceJump(edge, loopStmt);
		}

		graph.rollOut(header, loopBody);
	}

	public String toString()
	{
		return super.toString() + "(" + header + ")";
	}
}
