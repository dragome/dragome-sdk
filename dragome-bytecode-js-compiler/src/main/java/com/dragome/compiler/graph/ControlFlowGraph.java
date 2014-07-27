package com.dragome.compiler.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.dragome.compiler.ast.ASTNode;
import com.dragome.compiler.ast.ASTNodeStack;
import com.dragome.compiler.ast.Block;
import com.dragome.compiler.ast.BooleanExpression;
import com.dragome.compiler.ast.InfixExpression;
import com.dragome.compiler.ast.TryStatement;
import com.dragome.compiler.graph.transformation.Transformation;

public class ControlFlowGraph extends Graph
{

	private Map<Integer, Node> nodesByPc= new LinkedHashMap<Integer, Node>();

	private Node sourceNode;

	private List tryStatements;

	public static int problemCounter= 0;

	public ControlFlowGraph(List theTryStatements)
	{
		tryStatements= theTryStatements;
	}

	private TryStatement selectTryStatement(Node node)
	{
		int pc= node.getInitialPc();
		for (int i= 0; i < tryStatements.size(); i++)
		{
			TryStatement tryStmt= (TryStatement) tryStatements.get(i);
			Block block= tryStmt.getTryBlock();
			if (pc >= block.getBeginIndex() && pc <= block.getEndIndex())
				return tryStmt;
		}
		return null;
	}

	public Node createNode(int pc)
	{
		return createNode(pc, Node.class);
	}

	public Node getOrCreateNode(int pc)
	{
		Node node= getNode(pc);
		if (node == null)
		{
			node= createNode(pc, Node.class);
		}
		return node;
	}

	public Node createNode(int pc, Class nodeClass)
	{
		if (pc < 0)
			throw new RuntimeException("Program counter may not be negative");

		Node node= super.createNode(nodeClass);
		node.setInitialPc(pc);
		if (nodesByPc.put(node.getInitialPc(), node) != null)
		{
			throw new RuntimeException("Node already exists: " + node);
		}

		if (pc == 0)
			sourceNode= node;

		return node;
	}

	public Node getNodeAt(int pc)
	{
		int minPcDelta= Integer.MAX_VALUE;
		Node node= null;
		for (Node n : getNodes())
		{
			if (n.getInitialPc() <= pc && pc - n.getInitialPc() < minPcDelta)
			{
				minPcDelta= pc - n.getInitialPc();
				node= n;
				if (minPcDelta == 0)
					return node;
			}
		}

		if (node == null)
		{
			throw new RuntimeException("No node at pc " + pc);
		}

		return node;
	}

	public Node split(Node node, int pc)
	{
		if (node.block.getBeginIndex() >= pc)
			throw new RuntimeException("Block must contain program counter");

		Node nodeB= createNode(pc);

		for (Edge edge : new ArrayList<Edge>(node.getOutEdges()))
		{
			edge.reroot(nodeB);
		}

		addEdge(node, nodeB);

		ASTNode astNode= node.block.getFirstChild();
		while (astNode != null)
		{
			if (astNode.getBeginIndex() >= pc)
			{
				node.setCurrentPc(astNode.getBeginIndex() - 1);
				nodeB.block.appendChildren(astNode, node.block.getLastChild());
				break;
			}
			astNode= astNode.getNextSibling();
		}

		nodeB.stack= node.stack;
		node.stack= new ASTNodeStack();

		return nodeB;
	}

	public Node[] getOrSplitNodeAt(Node currentNode, int pc)
	{
		Node targetNode= getNodeAt(pc);
		if (targetNode.getInitialPc() != pc)
		{

			Node nodeB= split(targetNode, pc);
			if (targetNode == currentNode)
			{
				currentNode= nodeB;
			}
			targetNode= nodeB;
		}
		return new Node[] { currentNode, targetNode };
	}

	private void processTrys()
	{
		for (int i= 0; i < tryStatements.size(); i++)
		{
			TryStatement stmt= (TryStatement) tryStatements.get(i);
			TryHeaderNode header= stmt.header;
			Node tryNode= header.getTryBody();

			if (tryNode == sourceNode)
			{
				//		if (tryNode.getInEdges().size() > 1)
				//		{
				//		    problemCounter++;
				//		    throw new WhileTryProblemException();
				//		}
				//		else
				sourceNode= header;
			}

			for (Edge edge : new ArrayList<Edge>(tryNode.getInEdges()))
			{
				int pc= edge.source.getInitialPc();
				if (pc >= stmt.getBeginIndex() && pc <= stmt.getEndIndex())
					continue;
				if (edge.source == header)
					continue;
				edge.redirect(header);
			}
		}
	}

	private void processTrys2()
	{
		for (Node node : nodesByPc.values())
		{
			TryStatement sourceTry= selectTryStatement(node);
			if (sourceTry == null)
				continue;

			for (Edge edge : node.getOutEdges())
			{
				if (edge.target.getInEdges().size() != 1)
				{
					continue;
				}
				TryStatement targetTry= selectTryStatement(edge.target);
				if (targetTry == null || targetTry != sourceTry)
				{
					edge.reroot(sourceTry.header);
				}
			}
		}
	}

	private void processTrys1()
	{
		for (int i= 0; i < tryStatements.size(); i++)
		{
			TryStatement stmt= (TryStatement) tryStatements.get(i);
			TryHeaderNode header= stmt.header;
			Node finallyNode= header.getFinallyNode();
			if (finallyNode == null)
				continue;

			Iterator iter= finallyNode.jsrCallers.iterator();
			while (iter.hasNext())
			{
				Node node= (Node) iter.next();

				if (node.getInitialPc() > finallyNode.getInitialPc())
				{
					removeInEdges(node);
					addEdge(header, node);
					node.setDomParent(header);
				}
			}
		}
	}

	public Node getSource()
	{
		return sourceNode;
	}

	public Node getNode(int pc)
	{
		return nodesByPc.get(pc);
	}

	private void markGlobalTargets(Node predecessor)
	{
		for (Edge edge : predecessor.getOutEdges())
		{
			Node target= edge.target;
			if (target.getDomParent() == predecessor)
				continue;

			if (predecessor.isBranch())
			{
				Node node= createNode(Node.class);
				edge.redirect(node);
				node.setDomParent(predecessor);
				edge= addEdge(node, target);
			}
		}
	}

	void visitToMark(Node node)
	{

		for (Node child : new ArrayList<Node>(node.getDomChildren()))
		{
			visitToMark(child);
		}

		markGlobalTargets(node);
	}

	void visit(Node node)
	{

		for (Node child : new ArrayList<Node>(node.getDomChildren()))
		{
			visit(child);
		}

		do
		{
			Transformation t= Transformation.select(this, node);
			if (t == null)
				break;
			node= t.apply();
			dump("After transformation");
		}
		while (true);

		if (node.getDomChildren().size() > 0)
		{
			throw new RuntimeException("Could not reduce graph at " + node);
		}
	}

	public void replaceNode(Node node, Node newNode)
	{

		super.replaceNode(node, newNode);

		if (newNode != null)
		{
			nodesByPc.put(node.getInitialPc(), newNode);
		}
		else
		{
			nodesByPc.remove(node.getInitialPc());
		}

		if (node == sourceNode)
		{
			if (newNode == null)
			{
				throw new RuntimeException("Cannot remove source node " + sourceNode);
			}
			sourceNode= newNode;
		}
	}

	public Block reduce()
	{
		processTrys();
		processTrys2();

		dump("Before Shortcuts");
		processShortcuts();

		DominatorTree builder= new DominatorTree(this);
		builder.build();

		processTrys1();

		visitToMark(getSource());
		dump("Begin reduce");

		visit(getSource());

		if (size() != 1)
		{
			//throw new RuntimeException("Could not reduce graph");
		}

		Block block= new Block();

		rollOut(getSource(), block);

		return block;
	}

	boolean performAndOrShortcut(Node a, Node b)
	{
		if (b.getInEdges().size() != 1)
			return false;
		if (b.block.getChildCount() > 0)
		{

			return false;
		}

		ConditionalEdge aToC;
		ConditionalEdge bToC;
		boolean isOR= true;

		while (true)
		{
			aToC= a.getConditionalEdge(isOR);
			bToC= b.getConditionalEdge(isOR);
			if (bToC.target == aToC.target)
				break;
			if (!isOR)
				return false;
			isOR= false;
		}

		if (aToC.target.getInEdges().size() != 2)
			return false;

		ConditionalEdge bToD= b.getConditionalEdge(!isOR);
		ConditionalEdge aToB= a.getConditionalEdge(!isOR);

		aToB.redirect(bToD.target);
		removeEdge(bToC);
		removeEdge(bToD);
		removeNode(b);

		InfixExpression infix= new InfixExpression(isOR ? InfixExpression.Operator.CONDITIONAL_OR : InfixExpression.Operator.CONDITIONAL_AND);

		infix.setOperands(aToC.getBooleanExpression().getExpression(), bToC.getBooleanExpression().getExpression());

		BooleanExpression be= new BooleanExpression(infix);
		aToC.setBooleanExpression(be);
		aToB.setBooleanExpression(be);

		logger.debug("Created shortcut and removed " + b);

		return true;
	}

	public void processShortcuts()
	{
		Collection<Node> branches= new LinkedHashSet<Node>();
		for (Node node : getNodes())
		{
			if (node.isBranch())
				branches.add(node);
		}

		L: while (true)
		{
			Iterator<Node> iter= branches.iterator();
			while (iter.hasNext())
			{
				Node branch= iter.next();
				for (Node node : branch.succs())
				{
					if (node.isBranch() && performAndOrShortcut(branch, node))
					{
						branches.remove(node);
						continue L;
					}
				}
			}
			break L;
		}

	}
}
