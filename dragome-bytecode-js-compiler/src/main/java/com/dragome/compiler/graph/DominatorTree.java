package com.dragome.compiler.graph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.dragome.compiler.utils.Log;

public class DominatorTree
{

	private ControlFlowGraph graph;

	public DominatorTree(ControlFlowGraph theGraph)
	{
		graph= theGraph;
	}

	/**
	 * Sets the pre-order index of a node.
	 */
	private void visit(Node node, Collection<Node> visited)
	{
		// Establish preorder index.
		node.setPreOrderIndex(visited.size());
		visited.add(node);

		for (Node succ : node.succs())
		{
			if (!visited.contains(succ))
			{
				visit(succ, visited);
			}
		}

	}

	/**
	 * Builds the dominator tree and store it in the respective nodes. It will
	 * remove all unreachable nodes on the way!
	 */
	public void build()
	{

		// Construct list of nodes in pre-order order.
		ArrayList<Node> preOrder= new ArrayList<Node>();

		visit(graph.getSource(), preOrder);

		// Remove unreachable nodes.
		for (Node node : new ArrayList<Node>(graph.getNodes()))
		{
			if (!preOrder.contains(node))
			{
				Log.getLogger().warn("Unreachable code detected and removed");
				// Logger.getLogger().info("Removed " + node);
				graph.removeInEdges(node);
				graph.removeOutEdges(node);
				graph.removeNode(node);
			}
			else if (node.getPreOrderIndex() == -1)
			{
				throw new RuntimeException("Pre-order not set for " + node);
			}
		}

		int size= graph.size(); // The number of vertices in the cfg

		Map snkPreds= new LinkedHashMap(); // The predacessor vertices from the sink

		// Determine the predacessors of the cfg's sink node

		int rootIndex= graph.getSource().getPreOrderIndex();

		if (rootIndex < 0 || rootIndex >= size)
			throw new RuntimeException("Root index out of range");

		BitSet[] domMatrix= new BitSet[size];

		for (int i= 0; i < size; i++)
		{
			BitSet domVector= new BitSet(size);

			if (i == rootIndex)
			{

				domVector.set(rootIndex);
			}
			else
			{

				domVector.set(0, size);
			}

			domMatrix[i]= domVector;
		}

		boolean changed;

		do
		{
			changed= false;

			Iterator nodes= preOrder.iterator();

			while (nodes.hasNext())
			{
				Node node= (Node) nodes.next();

				int i= node.getPreOrderIndex();

				if (i < 0 || i >= size)
					throw new RuntimeException("Unreachable node " + node);

				if (i == rootIndex)
				{
					continue;
				}

				BitSet oldSet= domMatrix[i];

				BitSet domVector= new BitSet(size);
				domVector.or(oldSet);

				Collection preds= node.preds();

				Iterator e= preds.iterator();

				// predacessors.
				while (e.hasNext())
				{
					Node pred= (Node) e.next();

					int j= pred.getPreOrderIndex();
					if (j == -1)
						throw new RuntimeException("Unreachable node " + pred);

					domVector.and(domMatrix[j]);
				}

				// Don't forget to account for the sink node if node is a

				preds= (Collection) snkPreds.get(node);

				if (preds != null)
				{
					e= preds.iterator();

					while (e.hasNext())
					{
						Node pred= (Node) e.next();

						int j= pred.getPreOrderIndex();

						domVector.and(domMatrix[j]);
					}
				}

				domVector.set(i);

				if (!domVector.equals(oldSet))
				{
					changed= true;
					domMatrix[i]= domVector;
				}
			}
		}
		while (changed);

		for (Node node : graph.getNodes())
		{
			node.setDomParent(null);
			node.getDomChildren().clear();
		}

		// A node's immediate dominator is its closest dominator. So, we

		for (Node node : graph.getNodes())
		{
			int i= node.getPreOrderIndex();

			if (i < 0 || i >= size)
				throw new RuntimeException("Unreachable node " + node);

			if (i == rootIndex)
			{
				continue;
			}

			BitSet domVector= domMatrix[i];

			BitSet idom= new BitSet(size);
			idom.or(domVector);
			idom.clear(i);

			for (int j= 0; j < size; j++)
			{
				if (i != j && domVector.get(j))
				{

					BitSet b= new BitSet(size);

					b.or(domMatrix[j]);
					b.flip(0, size);
					b.set(j);
					idom.and(b);
				}
			}

			Node parent= null;

			for (int j= 0; j < size; j++)
			{
				if (idom.get(j))
				{
					Node p= preOrder.get(j);
					if (parent != null)
						throw new RuntimeException(node + " has more than one immediate dominator: " + parent + " and " + p);
					parent= p;
				}
			}

			if (parent == null)
				throw new RuntimeException(node + " has 0 immediate dominators");

			node.setDomParent(parent);
		}
	}
}
