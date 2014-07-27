package com.dragome.compiler.graph.transformation;

import java.util.Iterator;
import java.util.List;

import com.dragome.compiler.ast.Block;
import com.dragome.compiler.ast.CatchClause;
import com.dragome.compiler.ast.TryStatement;
import com.dragome.compiler.graph.Node;
import com.dragome.compiler.graph.TryHeaderNode;

public class Try extends Transformation
{

	private Node tryBodyNode;

	private List catchNodes;

	private Node finallyNode;

	boolean applies_()
	{
		return header instanceof TryHeaderNode;
	}

	void apply_()
	{
		TryHeaderNode head= (TryHeaderNode) header;
		catchNodes= head.getCatchNodes();

		for (Node catchNode : head.getCatchNodes())
		{
			graph.rerootOutEdges(catchNode, newNode, false);
			graph.removeInEdges(catchNode);
			graph.removeNode(catchNode);
		}

		tryBodyNode= head.getTryBody();
		graph.rerootOutEdges(tryBodyNode, newNode, false);
		graph.removeInEdges(tryBodyNode);
		graph.removeNode(tryBodyNode);

		finallyNode= head.getFinallyNode();
		if (finallyNode != null)
		{
			Block b= finallyNode.block;

			b.removeChild(b.getFirstChild());

			b.removeChild(b.getLastChild());
			graph.rerootOutEdges(finallyNode, newNode, false);
			graph.removeInEdges(finallyNode);
			graph.removeNode(finallyNode);
		}

	}

	void rollOut_(Block block)
	{
		TryHeaderNode head= (TryHeaderNode) header;

		TryStatement stmt= head.getTryStatement();
		block.appendChild(stmt);

		graph.rollOut(tryBodyNode, stmt.getTryBlock());

		if (finallyNode != null)
		{
			stmt.setFinallyBlock(new Block());
			graph.rollOut(finallyNode, stmt.getFinallyBlock());
		}

		CatchClause cc= (CatchClause) stmt.getCatchStatements().getFirstChild();
		Iterator iter= catchNodes.iterator();
		while (iter.hasNext())
		{
			Node catchNode= (Node) iter.next();
			graph.rollOut(catchNode, cc);
			cc= (CatchClause) cc.getNextSibling();
		}
	}

	public String toString()
	{
		return super.toString() + "(" + header + ")";
	}

}
