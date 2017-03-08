/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

// Copyright 2011 The j2js Authors. All Rights Reserved.
//
// This file is part of j2js.
//
// j2js is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// j2js is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with j2js. If not, see <http://www.gnu.org/licenses/>.

package com.dragome.compiler.parser;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ByteSequence;

import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.Project;
import com.dragome.compiler.ast.ASTNode;
import com.dragome.compiler.ast.ASTNodeStack;
import com.dragome.compiler.ast.ArrayAccess;
import com.dragome.compiler.ast.ArrayCreation;
import com.dragome.compiler.ast.ArrayInitializer;
import com.dragome.compiler.ast.Assignment;
import com.dragome.compiler.ast.Block;
import com.dragome.compiler.ast.BooleanExpression;
import com.dragome.compiler.ast.CastExpression;
import com.dragome.compiler.ast.CatchClause;
import com.dragome.compiler.ast.ClassInstanceCreation;
import com.dragome.compiler.ast.ClassLiteral;
import com.dragome.compiler.ast.ConditionalBranch;
import com.dragome.compiler.ast.ExceptionHandler;
import com.dragome.compiler.ast.ExceptionHandlers;
import com.dragome.compiler.ast.Expression;
import com.dragome.compiler.ast.FieldAccess;
import com.dragome.compiler.ast.FieldRead;
import com.dragome.compiler.ast.FieldWrite;
import com.dragome.compiler.ast.InfixExpression;
import com.dragome.compiler.ast.InstanceofExpression;
import com.dragome.compiler.ast.Jump;
import com.dragome.compiler.ast.JumpSubRoutine;
import com.dragome.compiler.ast.MethodBinding;
import com.dragome.compiler.ast.MethodDeclaration;
import com.dragome.compiler.ast.MethodInvocation;
import com.dragome.compiler.ast.NoOperation;
import com.dragome.compiler.ast.NullLiteral;
import com.dragome.compiler.ast.NumberLiteral;
import com.dragome.compiler.ast.PrefixExpression;
import com.dragome.compiler.ast.PrimitiveCast;
import com.dragome.compiler.ast.ReturnStatement;
import com.dragome.compiler.ast.StringLiteral;
import com.dragome.compiler.ast.SynchronizedBlock;
import com.dragome.compiler.ast.ThisExpression;
import com.dragome.compiler.ast.ThrowStatement;
import com.dragome.compiler.ast.TryStatement;
import com.dragome.compiler.ast.VariableBinding;
import com.dragome.compiler.ast.VariableDeclaration;
import com.dragome.compiler.exceptions.UnhandledCompilerProblemException;
import com.dragome.compiler.graph.ControlFlowGraph;
import com.dragome.compiler.graph.Node;
import com.dragome.compiler.graph.SwitchEdge;
import com.dragome.compiler.graph.TryHeaderNode;
import com.dragome.compiler.parser.Form.Value;
import com.dragome.compiler.type.Signature;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.utils.Log;
import com.dragome.compiler.utils.Utils;

public class Pass1
{

	private ConstantPool constantPool;

	private ByteSequence bytes;

	private static ASTNode currentNode;

	private ASTNodeStack stack;

	private Code code;

	private MethodDeclaration methodDecl;

	private Method method;

	private List<TryStatement> tryStatements= new ArrayList<TryStatement>();

	private ControlFlowGraph graph= new ControlFlowGraph(tryStatements);

	private int depth;

	private static Log logger= Log.getLogger();

	private boolean wide= false;

	public static ASTNode getCurrentNode()
	{
		return currentNode;
	}

	public Pass1(JavaClass jc)
	{
		constantPool= jc.getConstantPool();
		loopFound= false;
	}

	private CatchClause createCatchClause(TryStatement tryStmt, ExceptionHandler handle)
	{
		CatchClause cStmt= new CatchClause(handle.getHandlerPC());
		VariableDeclaration decl= new VariableDeclaration(VariableDeclaration.LOCAL_PARAMETER);
		decl.setName("_EX_");
		decl.setType(handle.getCatchType(constantPool));
		cStmt.setException(decl);
		tryStmt.addCatchStatement(cStmt);
		return cStmt;
	}

	private void makeTryFrames()
	{
		for (int i= 0; i < tryStatements.size(); i++)
		{
			TryStatement tryStmt= tryStatements.get(i);
			makeTryFrame(tryStmt);
		}
	}

	private void makeTryFrame(TryStatement stmt)
	{
		TryHeaderNode header= stmt.header;

		Node tryNode= graph.getOrCreateNode(stmt.getBeginIndex());
		tryNode.stack= new ASTNodeStack();
		header.setTryBody(tryNode);

		CatchClause clause= (CatchClause) stmt.getCatchStatements().getFirstChild();
		while (clause != null)
		{
			// Push implicit exception.
			Node catchNode= graph.createNode(clause.getBeginIndex());
			// catchNode.type = NodeType.CATCH;
			catchNode.stack= new ASTNodeStack(new VariableBinding(clause.getException()));
			header.addCatchNode(catchNode);

			clause= (CatchClause) clause.getNextSibling();
		}
	}

	private void compileCodeException()
	{
		ExceptionHandlers handlers= new ExceptionHandlers(code);

		Iterator<ExceptionHandler> handleIterator= handlers.iterator();

		ExceptionHandler handle= handleIterator.hasNext() ? handleIterator.next() : null;
		while (handle != null)
		{
			boolean hasFinally= false;
			int start= handle.getStartPC();
			int end= handle.getEndPC();

			TryStatement tryStmt= new TryStatement();
			tryStmt.header= (TryHeaderNode) graph.createNode(TryHeaderNode.class);
			tryStmt.header.tryStmt= tryStmt;

			Block tryBlock= new Block(start, end);
			tryStmt.setTryBlock(tryBlock);

			// tryStmt.setBeginIndex(start);

			tryStatements.add(tryStmt);

			CatchClause cStmt= null;

			// Collect all non-default handlers. The range of each handler is
			// from the 'store'-instruction to the beginning of the next
			// handler.
			while (handle != null && !handle.isDefault() && handle.getStartPC() == start && handle.getEndPC() == end)
			{
				if (cStmt != null)
				{
					cStmt.setEndIndex(handle.getHandlerPC() - 1);
				}
				cStmt= createCatchClause(tryStmt, handle);
				handle= handleIterator.hasNext() ? handleIterator.next() : null;
			}

			int foo= -1;
			if (handle != null && handle.isDefault() && handle.getStartPC() == start)
			{
				// Collect first default handler.
				hasFinally= true;
				if (cStmt != null)
				{
					cStmt.setEndIndex(handle.getHandlerPC() - 1);
					tryStmt.setEndIndex(handle.getHandlerPC() - 1);
					// Warning: We only set a lower bound for the end index. The
					// correct index is set later
					// when the finally statement is analysed.
				}
				cStmt= createCatchClause(tryStmt, handle);
				foo= handle.getHandlerPC();
				handle= handleIterator.hasNext() ? handleIterator.next() : null;
			}

			// Last catch stmt has no endIndex, yet!

			while (handle != null && handle.isDefault() && (handle.getHandlerPC() == foo))
			{
				// Skip all remaining default handlers.
				throw new RuntimeException("remaining default handlers");
				// handle = handleIterator.hasNext()?handleIterator.next():null;
			}

			Block catches= tryStmt.getCatchStatements();
			if (catches.getChildCount() == 0)
			{
				throw new ParseException("A try clause must have at least one (possibly default) catch clause", tryStmt);
			}
			cStmt= (CatchClause) catches.getChildAt(0);
			tryBlock.setEndIndex(cStmt.getBeginIndex() - 1);
			cStmt= (CatchClause) catches.getLastChild();
			if (cStmt.getEndIndex() == Integer.MIN_VALUE)
			{
				cStmt.setEndIndex(cStmt.getBeginIndex() + 1);
			}
			tryStmt.setEndIndex(cStmt.getEndIndex());

			if (hasFinally)
			{
				// Can't say yet where finally block is located.
			}
		}
	}

	private void dumpCode()
	{
		InstructionList il= new InstructionList(code.getCode());
		InstructionHandle[] handles= il.getInstructionHandles();

		for (InstructionHandle handle : handles)
		{
			System.out.println(handle.toString(true));
			InstructionTargeter[] targeters= handle.getTargeters();
			if (targeters != null)
			{
				for (InstructionTargeter targeter : handle.getTargeters())
				{
					System.out.println("    Targeter: " + targeter.toString() + " " + targeter.getClass());
				}
			}
		}

		for (CodeException ce : code.getExceptionTable())
		{
			String exceptionType;
			if (ce.getCatchType() > 0)
			{
				Constant constant= constantPool.getConstant(ce.getCatchType());
				exceptionType= Pass1.constantToString(constant, constantPool);
			}
			else
			{
				exceptionType= "Default";
			}
			System.out.println(ce.toString() + " " + exceptionType);
		}
	}

	public void parse(Method theMethod, MethodDeclaration theMethodDecl) throws IOException
	{
		method= theMethod;
		methodDecl= theMethodDecl;

		code= method.getCode();

		if (logger.isDebugEnabled())
		{
			dumpCode();
		}

		Block.TAG= 0;

		compileCodeException();

		bytes= new ByteSequence(code.getCode());

		graph.createNode(0);

		makeTryFrames();

		parseStatement();

		try
		{
			Optimizer optimizer= new Optimizer(methodDecl, tempDecls);
			optimizer.optimize();
		}
		catch (Error e)
		{
			DragomeJsCompiler.errorCount++;
			if (logger.isDebugEnabled())
			{
				logger.debug("In Expression Optimizer:\n" + e + "\n" + Utils.stackTraceToString(e));
			}
			else
			{
				logger.error("In Expression Optimizer:\n " + e);
			}
		}

		Block block;
		if (DragomeJsCompiler.compiler.reductionLevel == 0)
		{
			block= graph.reduceDumb();
		}
		else
		{
			block= graph.reduce();
		}

		methodDecl.setBody(block);

		if (loopFound)
			throwBugException();
	}

	private boolean isProcedure(ASTNode stmt)
	{
		if (stmt instanceof MethodInvocation)
		{
			MethodInvocation mi= (MethodInvocation) stmt;
			return mi.getTypeBinding().equals(Type.VOID);
		}
		return false;
	}

	Node cNode;

	Node lastCurrentNode;

	private boolean whileTryProblemDetected= false;

	private Jump lastJump;

	public static boolean loopFound= false;

	private void setCurrentNode(Node theNode)
	{
		if (cNode == theNode)
			return;
		cNode= theNode;
		if (cNode != null && cNode != lastCurrentNode)
		{
			logger.debug("Switched to " + cNode);
			lastCurrentNode= cNode;
		}
	}

	private void joinNodes(Node node)
	{
		Collection<Node> nodes= node.preds();
		Iterator iter= nodes.iterator();
		while (iter.hasNext())
		{
			Node n= (Node) iter.next();
			if (n.stack.size() == 0)
				iter.remove();
		}
		if (nodes.size() > 0)
		{
			mergeStacks(nodes, node.stack);
		}
	}

	private void selectActiveNode(int pc)
	{

		List<Node> activeNodes= new ArrayList<Node>();
		for (Node node : graph.getNodes())
		{
			if (node.getCurrentPc() == pc)
			{
				activeNodes.add(node);
			}
		}

		if (activeNodes.size() == 0)
		{

			Node node= graph.createNode(pc);
			setCurrentNode(node);
			return;
		}

		if (activeNodes.size() == 1)
		{

			Node node= activeNodes.get(0);
			setCurrentNode(node);
			return;
		}

		Node node= graph.getNode(pc);
		if (node == null)
			throw new RuntimeException("No node found at " + pc);

		setCurrentNode(node);

		activeNodes.remove(node);
		for (Node n : activeNodes)
		{
			graph.addEdge(n, node);
		}
	}

	private void expressionsToVariables(Node node, boolean clone)
	{
		if (node.stack.size() == 0)
			return;

		logger.debug("expressionsToVariables ...");

		for (int i= 0; i < node.stack.size(); i++)
		{
			Expression expr= (Expression) node.stack.get(i);

			if (expr instanceof VariableBinding && (((VariableBinding) expr).isTemporary()))
			{

				continue;
			}

			VariableBinding vb= methodDecl.createAnonymousVariableBinding(expr.getTypeBinding(), true);
			logger.debug("\t" + expr + ' ' + vb.getName());
			Assignment a= new Assignment(Assignment.Operator.ASSIGN);
			a.setLeftHandSide(vb);
			a.setRightHandSide(expr);

			node.block.appendChild(a);
			node.stack.set(i, clone ? (VariableBinding) vb.clone() : vb);
		}

		logger.debug("... expressionsToVariables");
	}

	private Object stacksIdentical(Collection sources, int index)
	{
		Expression expr= null;
		Iterator iter= sources.iterator();
		while (iter.hasNext())
		{
			Node node= (Node) iter.next();
			Expression e= (Expression) node.stack.get(index);
			if (expr == null)
			{
				expr= e;
			}
			else if (e != expr)
			{
				return expr.getTypeBinding();
			}
		}
		return expr;
	}

	private void mergeStacks(Collection sources, ASTNodeStack target)
	{
		logger.debug("Merging ...");

		Iterator iter= sources.iterator();
		while (iter.hasNext())
		{
			Node pred= (Node) iter.next();
			dump(pred.stack, "Stack for " + pred);
		}

		int stackSize= -1;

		iter= sources.iterator();
		while (iter.hasNext())
		{
			Node pred= (Node) iter.next();

			if (stackSize == -1)
			{
				stackSize= pred.stack.size();
			}
			else if (stackSize != pred.stack.size())
			{
				dump(sources);
				throw new RuntimeException("Stack size mismatch");
			}
		}

		for (int index= 0; index < stackSize; index++)
		{
			Object obj= stacksIdentical(sources, index);
			if (obj instanceof Expression)
			{
				target.add((Expression) ((Expression) obj).clone());
				logger.debug("\tIdentical: " + obj);
			}
			else
			{

				VariableBinding vb= methodDecl.createAnonymousVariableBinding((Type) obj, true);

				target.add(vb);

				iter= sources.iterator();
				while (iter.hasNext())
				{
					Node node= (Node) iter.next();
					Expression expr= (Expression) node.stack.get(index);

					Assignment a= new Assignment(Assignment.Operator.ASSIGN);
					a.setLeftHandSide((VariableBinding) vb.clone());
					if (expr instanceof VariableBinding)
						expr= (VariableBinding) expr.clone();
					a.setRightHandSide(expr);

					node.block.appendChild(a);
				}
				logger.debug("\t" + vb.getName());
			}
		}
		logger.debug("... Merging stacks");
	}

	public void parseStatement() throws IOException
	{
		depth= 0;

		while (bytes.available() > 0)
		{

			int pc= bytes.getIndex();

			if (cNode != null)
			{
				cNode.setCurrentPc(pc);
			}

			selectActiveNode(pc);

			if (cNode.getInitialPc() == pc)
			{
				joinNodes(cNode);
			}

			stack= cNode.stack;

			ASTNode stmt= parseInstruction();

			if (stmt instanceof NoOperation)
				continue;

			depth+= stmt.getStackDelta();

			if (stmt instanceof VariableBinding)
			{
				depth= depth;
			}

			logger.debug(" -> " + stmt + " @ " + methodDecl.getLineNumberCursor().getLineNumber(stmt) + ", depth:" + depth + ", delta:" + stmt.getStackDelta());

			if (stmt instanceof JumpSubRoutine)
			{
				JumpSubRoutine jsr= (JumpSubRoutine) stmt;
				cNode.block.setEndIndex(jsr.getEndIndex());

				Node finallyNode= graph.getNode(jsr.getTargetIndex());

				if (finallyNode == null)
				{

					finallyNode= graph.createNode(jsr.getTargetIndex());

					finallyNode.stack= new ASTNodeStack(new Expression());

				}
				finallyNode.jsrCallers.add(cNode);
				if (cNode.preds().size() == 1 && finallyNode.preds().size() == 0 && cNode.getPred() instanceof TryHeaderNode)
				{

					TryHeaderNode tryHeaderNode= (TryHeaderNode) cNode.getPred();

					tryHeaderNode.setFinallyNode(finallyNode);
				}

			}
			else if (stmt instanceof ConditionalBranch)
			{
				ConditionalBranch cond= (ConditionalBranch) stmt;

				if (bytes.getIndex() == cond.getTargetIndex())
				{

				}
				else
				{
					Node elseNode= graph.getOrCreateNode(bytes.getIndex());

					Node ifNode;
					if (cond.getTargetIndex() <= pc)
					{
						Node[] nodes= graph.getOrSplitNodeAt(cNode, cond.getTargetIndex());
						cNode= nodes[0];
						ifNode= nodes[1];
					}
					else
					{
						ifNode= graph.getOrCreateNode(cond.getTargetIndex());
					}

					BooleanExpression be= new BooleanExpression(cond.getExpression());

					graph.addIfElseEdge(cNode, ifNode, elseNode, be);
					expressionsToVariables(cNode, false);
					cNode= null;

					if (lastJump != null && tryStatements.size() > 0 && (cond.getBeginIndex() - 1 == lastJump.getEndIndex()))
						whileTryProblemDetected= true;

					for (TryStatement tryStatement : tryStatements)
					{
						boolean nextToTryBegining= tryStatement.getBeginIndex() - 1 == cond.getEndIndex();
						boolean sameThanTryBegining= tryStatement.getBeginIndex() == cond.getBeginIndex();
						//			sameThanTryBegining= false; //TODO identificar si es un while!
						if (nextToTryBegining || sameThanTryBegining)
							whileTryProblemDetected= true;
					}

					if (!whileTryProblemDetected)
						for (CodeException codeException : code.getExceptionTable())
						{
							boolean nextToTryBegining= codeException.getEndPC() == cond.getTargetIndex();
							if (nextToTryBegining)
								whileTryProblemDetected= true;
						}

					if (whileTryProblemDetected)
						throwBugException();
				}
			}
			else if (stmt instanceof Jump)
			{
				int targetPc= ((Jump) stmt).getTargetIndex();
				lastJump= (Jump) stmt;
				Node targetNode;

				if (!whileTryProblemDetected)
					for (CodeException codeException : code.getExceptionTable())
					{
						boolean nextToTryBegining= codeException.getStartPC() - 1 == lastJump.getEndIndex();
						boolean nextToTryHandlerBegining= codeException.getCatchType() == 0 && codeException.getEndPC() - 1 == lastJump.getEndIndex();
						if (nextToTryBegining || nextToTryHandlerBegining)
							throwBugException();
					}

				if (targetPc <= pc)
				{
					if (whileTryProblemDetected)
						throwBugException();

					loopFound= true;

					Node[] nodes= graph.getOrSplitNodeAt(cNode, targetPc);
					cNode= nodes[0];
					targetNode= nodes[1];
				}
				else
				{
					targetNode= graph.getOrCreateNode(targetPc);
				}
				graph.addEdge(cNode, targetNode);
				cNode= null;
			}
			else if (stmt instanceof SynchronizedBlock || isProcedure(stmt))
			{
				cNode.block.appendChild(stmt);
			}
			else if (stmt instanceof Assignment)
			{
				expressionsToVariables(cNode, true);
				cNode.block.appendChild(stmt);
			}
			else if (stmt instanceof ThrowStatement || stmt instanceof ReturnStatement)
			{
				cNode.block.appendChild(stmt);
				cNode.close();
				cNode= null;
			}
			else
			{
				stack.push(stmt);
			}
		}

	}

	private void throwBugException()
	{
		throw new UnhandledCompilerProblemException();
	}

	void dump(Collection nodes)
	{
		if (!logger.isDebugEnabled())
			return;

		Iterator iter= nodes.iterator();
		while (iter.hasNext())
		{
			Node node= (Node) iter.next();
			dump(node.stack, node.toString());
		}
	}

	static void dump(List list, String msg)
	{
		if (!logger.isDebugEnabled())
			return;

		StringBuffer sb= new StringBuffer();
		sb.append("Begin dumping " + msg + "...\n");
		for (int i= 0; i < list.size(); i++)
		{
			ASTNode node= (ASTNode) list.get(i);
			sb.append("    " + i + ": " + node + "\n");
		}
		sb.append("... end of dump");

		logger.debug(sb.toString());
	}

	private VariableBinding createVariableBinding(int slot, Type type, boolean isWrite)
	{
		return methodDecl.createVariableBinding(VariableDeclaration.getLocalVariableName(method, slot, bytes.getIndex()), type, isWrite);
	}

	private InfixExpression createInfixRightLeft(InfixExpression.Operator op, Expression right, Expression left, Type type)
	{
		InfixExpression binOp= new InfixExpression(op);
		binOp.setTypeBinding(type);
		binOp.setOperands(left, right);
		return binOp;
	}

	private PrefixExpression createPrefix(PrefixExpression.Operator op, ASTNode operand, Type type)
	{
		PrefixExpression pe= new PrefixExpression();
		pe.setOperator(op);
		pe.setTypeBinding(type);
		pe.setOperand(operand);
		return pe;
	}

	private Form selectForm(InstructionType instructionType)
	{
		if (instructionType.getFormCount() == 1)
		{
			return instructionType.getForm(0);
		}
		FormLoop: for (int i= 0; i < instructionType.getFormCount(); i++)
		{
			Form form= instructionType.getForm(i);
			for (int j= 0; j < form.getIns().length; j++)
			{
				Form.Value in= form.getIns()[form.getIns().length - 1 - j];
				if (stack.peek(j).getCategory() != in.getCategory())
					continue FormLoop;
			}
			return form;
		}
		throw new RuntimeException("Could not determine correct form for " + instructionType);
	}

	private List<VariableDeclaration> tempDecls= new ArrayList<VariableDeclaration>();

	private Expression[] duplicate(Expression e)
	{
		if (e instanceof NumberLiteral || e instanceof ThisExpression || e instanceof StringLiteral)
		{

			return new Expression[] { e, (Expression) e.clone() };
		}

		if (e instanceof VariableBinding && ((VariableBinding) e).isTemporary())
		{
			VariableBinding vb1= (VariableBinding) e;
			VariableBinding vb2= (VariableBinding) vb1.clone();
			return new VariableBinding[] { vb1, vb2 };
		}

		Assignment a= new Assignment(Assignment.Operator.ASSIGN);
		a.setRange(bytes.getIndex(), bytes.getIndex());
		VariableBinding vb1= methodDecl.createAnonymousVariableBinding(e.getTypeBinding(), true);
		VariableBinding vb2= (VariableBinding) vb1.clone();
		VariableBinding vb3= (VariableBinding) vb1.clone();
		tempDecls.add(vb1.getVariableDeclaration());
		vb1.getVariableDeclaration().setParentNode(methodDecl);
		a.setLeftHandSide(vb1);
		a.setRightHandSide(e);
		cNode.block.appendChild(a);
		return new VariableBinding[] { vb2, vb3 };
	}

	private SwitchEdge getOrCreateCaseGroup(Node header, Map<Integer, SwitchEdge> switchEdges, int startPc)
	{
		SwitchEdge switchEdge= switchEdges.get(startPc);
		if (switchEdge == null)
		{
			Node caseGroupNode= graph.createNode(startPc);
			switchEdge= (SwitchEdge) graph.addEdge(header, caseGroupNode, SwitchEdge.class);
			switchEdges.put(startPc, switchEdge);
		}

		return switchEdge;
	}

	private int readUnsigned() throws IOException
	{
		int index;
		if (wide)
		{
			index= bytes.readUnsignedShort();
			wide= false;
		}
		else
		{
			index= bytes.readUnsignedByte();
		}
		return index;
	}

	private int readSigned() throws IOException
	{
		int index;
		if (wide)
		{
			index= bytes.readShort();
			wide= false;
		}
		else
		{
			index= bytes.readByte();
		}
		return index;
	}

	private void dup1()
	{
		Expression[] value1= duplicate(stack.pop());
		stack.push(value1[0]);
		stack.push(value1[1]);
	}

	private void dup2()
	{
		Expression[] value1= duplicate(stack.pop());
		Expression[] value2= duplicate(stack.pop());
		stack.push(value2[0]);
		stack.push(value1[0]);
		stack.push(value2[1]);
		stack.push(value1[1]);
	}

	private ASTNode parseInstruction() throws IOException
	{
		int currentIndex= bytes.getIndex();
		short opcode= (short) bytes.readUnsignedByte();

		InstructionType instructionType= Const.instructionTypes[opcode];

		Form form= selectForm(instructionType);

		int opStackDelta= form.getOpStackDelta();

		ASTNode instruction= null;

		logger.debug(currentIndex + " " + instructionType.getName() + "[" + opcode + "] ");

		switch (opcode)
		{

			case Const.TABLESWITCH:

			case Const.LOOKUPSWITCH:
			{

				Node switchNode= graph.createNode(currentIndex);
				switchNode.isSwitchHeader= true;
				graph.addEdge(cNode, switchNode);
				cNode= null;

				int defaultOffset;
				int npairs;
				int offset;
				int remainder= bytes.getIndex() % 4;
				int noPadBytes= (remainder == 0) ? 0 : 4 - remainder;

				for (int i= 0; i < noPadBytes; i++)
				{
					byte b;

					if ((b= bytes.readByte()) != 0)
					{
						logger.warn("Padding byte != 0 in " + instructionType.getName() + ":" + b);
					}
				}

				defaultOffset= bytes.readInt();

				int low= 0;
				if (opcode == Const.LOOKUPSWITCH)
				{
					npairs= bytes.readInt();
					offset= bytes.getIndex() - 8 - noPadBytes - 1;
				}
				else
				{
					low= bytes.readInt();
					int high= bytes.readInt();
					npairs= high - low + 1;
					offset= bytes.getIndex() - 12 - noPadBytes - 1;
				}

				defaultOffset+= offset;

				switchNode.switchExpression= stack.pop();

				TreeMap<Integer, SwitchEdge> caseGroups= new TreeMap<Integer, SwitchEdge>();

				for (int i= 0; i < npairs; i++)
				{
					int key;
					if (opcode == Const.LOOKUPSWITCH)
					{
						key= bytes.readInt();
					}
					else
					{
						key= low + i;
					}

					SwitchEdge switchEdge= getOrCreateCaseGroup(switchNode, caseGroups, offset + bytes.readInt());
					switchEdge.expressions.add(NumberLiteral.create(new Integer(key)));
				}

				try
				{
					Node defaultNode= graph.createNode(defaultOffset);
					graph.addEdge(switchNode, defaultNode);
				}
				catch (Exception e)
				{
					setClassNotReversible(methodDecl);
				}

				instruction= new NoOperation();
				break;
			}

			case Const.CHECKCAST:
			{

				CastExpression cast= new CastExpression();
				int index= bytes.readUnsignedShort();
				ConstantClass c= (ConstantClass) constantPool.getConstant(index);
				ObjectType type= new ObjectType(c.getBytes(constantPool).replace('/', '.'));
				cast.setTypeBinding(type);
				cast.setExpression(stack.pop());
				instruction= cast;
				break;
			}

			case Const.INSTANCEOF:
			{

				int index= bytes.readUnsignedShort();
				InstanceofExpression ex= new InstanceofExpression();
				Expression objectref= stack.pop();
				ex.setLeftOperand(objectref);
				ConstantClass c= (ConstantClass) constantPool.getConstant(index);
				ObjectType type= new ObjectType(c.getBytes(constantPool).replace('/', '.'));
				ex.setRightOperand(type);
				ex.widen(objectref);
				instruction= ex;
				break;
			}

			case Const.ACONST_NULL:

				instruction= new NullLiteral();
				break;

			case Const.JSR:
			{

				instruction= new JumpSubRoutine(currentIndex + bytes.readShort());
				opStackDelta= 0;
				break;
			}

			case Const.JSR_W:
			{

				instruction= new JumpSubRoutine(currentIndex + bytes.readInt());
				break;
			}

			case Const.IFEQ:

				instruction= createConditional(currentIndex, InfixExpression.Operator.EQUALS, NumberLiteral.create(0));
				break;
			case Const.IFNE:

				instruction= createConditional(currentIndex, InfixExpression.Operator.NOT_EQUALS, NumberLiteral.create(0));
				break;
			case Const.IFGE:

				instruction= createConditional(currentIndex, InfixExpression.Operator.GREATER_EQUALS, NumberLiteral.create(0));
				break;
			case Const.IFGT:

				instruction= createConditional(currentIndex, InfixExpression.Operator.GREATER, NumberLiteral.create(0));
				break;
			case Const.IFLE:

				instruction= createConditional(currentIndex, InfixExpression.Operator.LESS_EQUALS, NumberLiteral.create(0));
				break;
			case Const.IFLT:

				instruction= createConditional(currentIndex, InfixExpression.Operator.LESS, NumberLiteral.create(0));
				break;
			case Const.IFNONNULL:

				instruction= createConditional(currentIndex, InfixExpression.Operator.NOT_EQUALS, new NullLiteral());
				break;
			case Const.IFNULL:

				instruction= createConditional(currentIndex, InfixExpression.Operator.EQUALS, new NullLiteral());
				break;

			case Const.IF_ACMPEQ:

			case Const.IF_ICMPEQ:

				instruction= createConditional(currentIndex, InfixExpression.Operator.EQUALS);
				break;
			case Const.IF_ACMPNE:

			case Const.IF_ICMPNE:

				instruction= createConditional(currentIndex, InfixExpression.Operator.NOT_EQUALS);
				break;
			case Const.IF_ICMPGE:

				instruction= createConditional(currentIndex, InfixExpression.Operator.GREATER_EQUALS);
				break;
			case Const.IF_ICMPGT:

				instruction= createConditional(currentIndex, InfixExpression.Operator.GREATER);
				break;
			case Const.IF_ICMPLE:

				instruction= createConditional(currentIndex, InfixExpression.Operator.LESS_EQUALS);
				break;
			case Const.IF_ICMPLT:

				instruction= createConditional(currentIndex, InfixExpression.Operator.LESS);
				break;

			case Const.LCMP:

			case Const.FCMPL:

			case Const.FCMPG:

			case Const.DCMPL:

			case Const.DCMPG:
			{

				MethodBinding binding= MethodBinding.lookup("javascript.Utils", "cmp", "(DDI)I");
				MethodInvocation mi= new MethodInvocation(methodDecl, binding);

				Expression value2= stack.pop();
				mi.addArgument(stack.pop());
				mi.addArgument(value2);

				int gORl= 0;
				if (instructionType.getName().endsWith("g"))
					gORl= 1;
				else if (instructionType.getName().endsWith("l"))
					gORl= -1;
				mi.addArgument(NumberLiteral.create(gORl));

				instruction= mi;

				break;
			}

			case Const.GOTO:
			{

				instruction= new Jump(currentIndex + bytes.readShort());
				break;
			}

			case Const.GOTO_W:
			{

				instruction= new Jump(currentIndex + bytes.readInt());
				break;
			}

			case Const.NEW:
			{

				ConstantClass c= (ConstantClass) constantPool.getConstant(bytes.readUnsignedShort());
				ObjectType type= new ObjectType(c.getBytes(constantPool).replace('/', '.'));

				instruction= new ClassInstanceCreation(type);
			}
				break;

			case Const.NEWARRAY:
			{

				String componentSignature= BasicType.getType(bytes.readByte()).getSignature();

				List<ASTNode> dimensions= new ArrayList<ASTNode>();
				dimensions.add(stack.pop());
				ArrayCreation ac= new ArrayCreation(methodDecl, new ObjectType("[" + componentSignature), dimensions);
				instruction= ac;
				break;
			}

			case Const.ANEWARRAY:
			{

				ConstantClass c= (ConstantClass) constantPool.getConstant(bytes.readUnsignedShort());
				String componentSignature= c.getBytes(constantPool).replace('/', '.');
				Type arrayType;
				if (componentSignature.startsWith("["))
				{
					arrayType= new ObjectType("[" + componentSignature);
				}
				else
				{
					arrayType= new ObjectType("[L" + componentSignature + ";");
				}

				List<ASTNode> dimensions= new ArrayList<ASTNode>();
				dimensions.add(stack.pop());
				ArrayCreation ac= new ArrayCreation(methodDecl, arrayType, dimensions);
				instruction= ac;
				break;
			}

			case Const.MULTIANEWARRAY:
			{

				ConstantClass c= (ConstantClass) constantPool.getConstant(bytes.readUnsignedShort());
				ObjectType arrayType= new ObjectType(c.getBytes(constantPool).replace('/', '.'));

				int dimCount= bytes.readUnsignedByte();
				opStackDelta= 1 - dimCount;
				List<ASTNode> dimensions= new ArrayList<ASTNode>();
				for (int i= 0; i < dimCount; i++)
				{

					dimensions.add(0, stack.pop());
				}
				ArrayCreation ac= new ArrayCreation(methodDecl, arrayType, dimensions);
				instruction= ac;
				break;
			}

			case Const.PUTSTATIC:

			case Const.PUTFIELD:
			{

				Assignment a= new Assignment(Assignment.Operator.ASSIGN);
				Expression rhs= stack.pop();

				int index= bytes.readUnsignedShort();
				ConstantFieldref fieldRef= (ConstantFieldref) constantPool.getConstant(index, Constants.CONSTANT_Fieldref);

				FieldAccess fa= new FieldWrite();
				fa.setName(getFieldName(fieldRef));
				fa.setFieldType(getFieldType(fieldRef));
				fa.setType(new ObjectType(fieldRef.getClass(constantPool)));
				fa.initialize(methodDecl);

				if (opcode == Const.PUTFIELD)
				{
					fa.setExpression(stack.pop());
				}

				a.setLeftHandSide(fa);
				a.setRightHandSide(rhs);

				instruction= a;
				break;
			}

			case Const.GETFIELD:
			{

				int index= bytes.readUnsignedShort();
				ConstantFieldref fieldRef= (ConstantFieldref) constantPool.getConstant(index, Constants.CONSTANT_Fieldref);

				Expression ex= stack.pop();
				FieldAccess fa= new FieldRead();
				fa.setType(new ObjectType(fieldRef.getClass(constantPool)));
				fa.setName(getFieldName(fieldRef));
				fa.setFieldType(getFieldType(fieldRef));
				fa.setExpression(ex);
				fa.initialize(methodDecl);
				instruction= fa;
				break;
			}

			case Const.GETSTATIC:
			{

				int index= bytes.readUnsignedShort();
				ConstantFieldref fieldRef= (ConstantFieldref) constantPool.getConstant(index, Constants.CONSTANT_Fieldref);

				FieldAccess fa= new FieldRead();
				fa.setType(new ObjectType(fieldRef.getClass(constantPool)));
				fa.setFieldType(getFieldType(fieldRef));
				fa.setName(getFieldName(fieldRef));
				fa.initialize(methodDecl);

				instruction= fa;
				break;
			}

			case Const.DUP:
			{

				dup1();
				instruction= stack.pop();
				break;
			}

			case Const.DUP2:

				if (form.getIndex() == 0)
				{
					dup2();
					instruction= stack.pop();
				}
				else
				{
					dup1();
					instruction= stack.pop();
				}
				break;

			case Const.DUP_X1:
			{

				dup1();
				stack.rotate(2);
				instruction= stack.pop();
				break;
			}

			case Const.DUP_X2:
			{

				if (form.getIndex() == 0)
				{
					dup1();
					stack.rotate(3);
				}
				else
				{
					dup1();
					stack.rotate(2);
				}
				instruction= stack.pop();
				break;
			}

			case Const.DUP2_X1:

				if (form.getIndex() == 0)
				{
					dup2();
					stack.rotate(4);
					stack.rotate(4);
				}
				else
				{
					dup1();
					stack.rotate(2);
				}
				instruction= stack.pop();
				break;

			case Const.DUP2_X2:

				if (form.getIndex() == 0)
				{
					dup2();
					stack.rotate(5);
					stack.rotate(5);
				}
				else if (form.getIndex() == 1)
				{
					dup1();
					stack.rotate(3);
				}
				else if (form.getIndex() == 2)
				{
					dup2();
					stack.rotate(4);
					stack.rotate(4);
				}
				else
				{
					dup1();
					stack.rotate(2);
				}

				instruction= stack.pop();
				break;

			case Const.SWAP:
			{

				stack.rotate(1);
				instruction= new NoOperation();
				break;
			}

			case Const.I2S:

			case Const.I2F:

			case Const.L2I:

			case Const.F2I:

			case Const.F2L:

			case Const.L2F:

			case Const.L2D:

			case Const.D2I:

			case Const.D2L:

			case Const.D2F:

			case Const.I2B:

			case Const.I2C:

				instruction= new PrimitiveCast(opcode, stack.pop(), form.getResultType());
				break;

			case Const.I2L:

				stack.peek().setTypeBinding(Type.LONG);
				instruction= new NoOperation();
				break;
			case Const.I2D:

			case Const.F2D:

				stack.peek().setTypeBinding(Type.DOUBLE);
				instruction= new NoOperation();
				break;

			case Const.INEG:

			case Const.LNEG:

			case Const.FNEG:

			case Const.DNEG:

				instruction= createPrefix(PrefixExpression.MINUS, stack.pop(), form.getResultType());
				break;

			case Const.ISHR:

			case Const.LSHR:

				instruction= createInfixRightLeft(InfixExpression.Operator.RIGHT_SHIFT_SIGNED, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.ISHL:

			case Const.LSHL:

				instruction= createInfixRightLeft(InfixExpression.Operator.LEFT_SHIFT, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IUSHR:

			case Const.LUSHR:

				instruction= createInfixRightLeft(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IADD:

			case Const.LADD:

			case Const.FADD:

			case Const.DADD:

				instruction= createInfixRightLeft(InfixExpression.Operator.PLUS, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.ISUB:

			case Const.LSUB:

			case Const.FSUB:

			case Const.DSUB:

				instruction= createInfixRightLeft(InfixExpression.Operator.MINUS, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IMUL:

			case Const.LMUL:

			case Const.FMUL:

			case Const.DMUL:

				instruction= createInfixRightLeft(InfixExpression.Operator.TIMES, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IDIV:

			case Const.LDIV:

			case Const.FDIV:

			case Const.DDIV:

				instruction= createInfixRightLeft(InfixExpression.Operator.DIVIDE, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IREM:

			case Const.LREM:

			case Const.FREM:

			case Const.DREM:

				instruction= createInfixRightLeft(InfixExpression.Operator.REMAINDER, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IXOR:

			case Const.LXOR:

				instruction= createInfixRightLeft(InfixExpression.Operator.XOR, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IAND:

			case Const.LAND:

				instruction= createInfixRightLeft(InfixExpression.Operator.AND, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IOR:

			case Const.LOR:

				instruction= createInfixRightLeft(InfixExpression.Operator.OR, stack.pop(), stack.pop(), form.getResultType());
				break;

			case Const.IINC:
			{

				boolean isWide= wide;
				int index= readUnsigned();

				wide= isWide;
				int constByte= readSigned();

				VariableBinding reference= createVariableBinding(index, Type.INT, true);
				reference.setField(false);

				Assignment assign= new Assignment(Assignment.Operator.PLUS_ASSIGN);
				assign.setLeftHandSide(reference);
				assign.setRightHandSide(NumberLiteral.create(new Integer(constByte)));
				instruction= assign;
				break;
			}

			case Const.ARRAYLENGTH:
			{

				Expression arrayRef= stack.pop();
				FieldAccess access= new FieldRead();
				access.setFieldType(Type.INT);
				access.setExpression(arrayRef);
				access.setName("length");

				instruction= access;
				break;
			}

			case Const.WIDE:

				wide= true;
				return new NoOperation();

			case Const.ILOAD_0:

			case Const.ILOAD_1:

			case Const.ILOAD_2:

			case Const.ILOAD_3:
			{

				VariableBinding reference= createVariableBinding(opcode - Const.ILOAD_0, Type.INT, false);
				reference.setField(false);
				instruction= reference;
				break;
			}

			case Const.LLOAD_0:

			case Const.LLOAD_1:

			case Const.LLOAD_2:

			case Const.LLOAD_3:
			{

				VariableBinding reference= createVariableBinding(opcode - Const.LLOAD_0, Type.LONG, false);
				reference.setField(false);
				instruction= reference;
				break;
			}

			case Const.FLOAD_0:

			case Const.FLOAD_1:

			case Const.FLOAD_2:

			case Const.FLOAD_3:
			{

				VariableBinding reference= createVariableBinding(opcode - Const.FLOAD_0, Type.FLOAT, false);
				reference.setField(false);
				instruction= reference;
				break;
			}

			case Const.DLOAD_0:

			case Const.DLOAD_1:

			case Const.DLOAD_2:

			case Const.DLOAD_3:
			{

				VariableBinding reference= createVariableBinding(opcode - Const.DLOAD_0, Type.DOUBLE, false);
				reference.setField(false);
				instruction= reference;
				break;
			}

			case Const.ALOAD_0:

			case Const.ALOAD_1:

			case Const.ALOAD_2:

			case Const.ALOAD_3:
			{

				if (opcode == Const.ALOAD_0 && !Modifier.isStatic(methodDecl.getAccess()))
				{
					ThisExpression reference= new ThisExpression();
					instruction= reference;
				}
				else
				{
					VariableBinding reference= createVariableBinding(opcode - Const.ALOAD_0, Type.OBJECT, false);
					reference.setField(true);
					instruction= reference;
				}
				break;
			}

			case Const.ILOAD:

			case Const.LLOAD:

			case Const.FLOAD:

			case Const.DLOAD:
			{

				VariableBinding reference= createVariableBinding(readUnsigned(), form.getResultType(), false);
				reference.setField(false);
				instruction= reference;
				break;
			}

			case Const.ALOAD:
			{

				VariableBinding reference= createVariableBinding(readUnsigned(), Type.OBJECT, false);
				reference.setField(true);
				instruction= reference;
				break;
			}

			case Const.BALOAD:

			case Const.CALOAD:

			case Const.SALOAD:

			case Const.IALOAD:

			case Const.LALOAD:

			case Const.FALOAD:

			case Const.DALOAD:

			case Const.AALOAD:
			{

				Expression index= stack.pop();
				Expression arrayRef= stack.pop();
				ArrayAccess aa;
				aa= new ArrayAccess();
				aa.setTypeBinding(form.getResultType());
				aa.setArray(arrayRef);
				aa.setIndex(index);

				instruction= aa;
				break;
			}

			case Const.BASTORE:

			case Const.CASTORE:

			case Const.SASTORE:

			case Const.IASTORE:

			case Const.LASTORE:

			case Const.FASTORE:

			case Const.DASTORE:

			case Const.AASTORE:
			{

				Expression value= stack.pop();
				Expression index= stack.pop();
				Expression arrayRef= stack.pop();
				if (arrayRef instanceof ArrayCreation)
				{
					ArrayCreation ac= (ArrayCreation) arrayRef;
					if (ac.getInitializer() == null)
					{
						ac.setInitializer(new ArrayInitializer());
					}
					ac.getInitializer().getExpressions().add(value);
					instruction= new NoOperation();
					break;
				}
				Assignment a= new Assignment(Assignment.Operator.ASSIGN);

				ArrayAccess aa;
				aa= new ArrayAccess();
				aa.setArray(arrayRef);
				aa.setIndex(index);

				a.setLeftHandSide(aa);
				a.setRightHandSide(value);
				instruction= a;
				break;
			}

			case Const.DSTORE:

			case Const.DSTORE_0:

			case Const.DSTORE_1:

			case Const.DSTORE_2:

			case Const.DSTORE_3:
			{

				int index;
				if (opcode == Const.DSTORE)
				{
					index= readUnsigned();
				}
				else
				{
					index= opcode - Const.DSTORE_0;
				}
				Assignment a= new Assignment(Assignment.Operator.ASSIGN);
				VariableBinding reference= createVariableBinding(index, Type.DOUBLE, true);
				reference.setField(false);
				a.setLeftHandSide(reference);
				a.setRightHandSide(stack.pop());
				instruction= a;
				break;
			}

			case Const.FSTORE:

			case Const.FSTORE_0:

			case Const.FSTORE_1:

			case Const.FSTORE_2:

			case Const.FSTORE_3:
			{

				int index;
				if (opcode == Const.FSTORE)
				{
					index= readUnsigned();
				}
				else
				{
					index= opcode - Const.FSTORE_0;
				}
				Assignment a= new Assignment(Assignment.Operator.ASSIGN);
				VariableBinding reference= createVariableBinding(index, Type.FLOAT, true);
				reference.setField(false);
				a.setLeftHandSide(reference);
				a.setRightHandSide(stack.pop());
				instruction= a;
				break;
			}

			case Const.ISTORE:

			case Const.ISTORE_0:

			case Const.ISTORE_1:

			case Const.ISTORE_2:

			case Const.ISTORE_3:
			{

				int index;
				if (opcode == Const.ISTORE)
				{
					index= readUnsigned();
				}
				else
				{
					index= opcode - Const.ISTORE_0;
				}
				Assignment a= new Assignment(Assignment.Operator.ASSIGN);
				VariableBinding reference= createVariableBinding(index, Type.INT, true);
				reference.setField(false);
				a.setLeftHandSide(reference);
				a.setRightHandSide(stack.pop());
				instruction= a;
				break;
			}

			case Const.LSTORE:

			case Const.LSTORE_0:

			case Const.LSTORE_1:

			case Const.LSTORE_2:

			case Const.LSTORE_3:
			{

				int index;
				if (opcode == Const.LSTORE)
				{
					index= readUnsigned();
				}
				else
				{
					index= opcode - Const.LSTORE_0;
				}
				Assignment a= new Assignment(Assignment.Operator.ASSIGN);
				VariableBinding reference= createVariableBinding(index, Type.LONG, true);
				reference.setField(false);
				a.setLeftHandSide(reference);
				a.setRightHandSide(stack.pop());
				instruction= a;
				break;
			}

			case Const.ASTORE:

			case Const.ASTORE_0:

			case Const.ASTORE_1:

			case Const.ASTORE_2:

			case Const.ASTORE_3:
			{

				Assignment a= new Assignment(Assignment.Operator.ASSIGN);
				int index;
				if (opcode == Const.ASTORE)
				{
					index= readUnsigned();
				}
				else
				{
					index= (opcode - Const.ASTORE_0);
				}
				VariableBinding reference= createVariableBinding(index, Type.OBJECT, true);
				a.setLeftHandSide(reference);

				if (stack.size() > 0)
				{
					a.setRightHandSide(stack.pop());
				}
				instruction= a;
				break;
			}

			case Const.ATHROW:
			{

				ThrowStatement throwStmt= new ThrowStatement();
				throwStmt.setExpression(stack.pop());
				instruction= throwStmt;
				break;
			}

			case Const.ICONST_M1:

			case Const.ICONST_0:

			case Const.ICONST_1:

			case Const.ICONST_2:

			case Const.ICONST_3:

			case Const.ICONST_4:

			case Const.ICONST_5:

				instruction= NumberLiteral.create(new Integer(-1 + opcode - Const.ICONST_M1));
				break;

			case Const.LCONST_0:

			case Const.LCONST_1:

				instruction= NumberLiteral.create(new Long(opcode - Const.LCONST_0));
				break;

			case Const.FCONST_0:

			case Const.FCONST_1:

			case Const.FCONST_2:

				instruction= NumberLiteral.create(new Float(opcode - Const.FCONST_0));
				break;

			case Const.DCONST_0:

			case Const.DCONST_1:

				instruction= NumberLiteral.create(new Double(opcode - Const.DCONST_0));
				break;

			case Const.BIPUSH:
			{

				NumberLiteral literal= NumberLiteral.create(new Byte(bytes.readByte()));
				instruction= literal;
				break;
			}

			case Const.SIPUSH:
			{

				NumberLiteral il= NumberLiteral.create(new Short(bytes.readShort()));
				instruction= il;
				break;
			}

			case Const.LDC:

			case Const.LDC_W:

			case Const.LDC2_W:
			{

				int index;
				if (opcode == Const.LDC)
				{
					index= bytes.readUnsignedByte();
				}
				else
				{
					index= bytes.readUnsignedShort();
				}
				Constant constant= constantPool.getConstant(index);

				if (opcode == Const.LDC2_W && (constant.getTag() != Constants.CONSTANT_Double && constant.getTag() != Constants.CONSTANT_Long))
					throw new RuntimeException("LDC2_W must load long or double");

				if (constant.getTag() == Constants.CONSTANT_Integer)
				{
					instruction= NumberLiteral.create(new Integer(((ConstantInteger) constant).getBytes()));
				}
				else if (constant.getTag() == Constants.CONSTANT_Float)
				{
					instruction= NumberLiteral.create(new Float(((ConstantFloat) constant).getBytes()));
				}
				else if (constant.getTag() == Constants.CONSTANT_Long)
				{
					instruction= NumberLiteral.create(new Long(((ConstantLong) constant).getBytes()));
				}
				else if (constant.getTag() == Constants.CONSTANT_Double)
				{
					instruction= NumberLiteral.create(new Double(((ConstantDouble) constant).getBytes()));
				}
				else if (constant.getTag() == Constants.CONSTANT_Utf8)
				{
					instruction= new StringLiteral(((ConstantUtf8) constant).getBytes());
				}
				else if (constant.getTag() == Constants.CONSTANT_String)
				{
					int k= ((ConstantString) constant).getStringIndex();
					constant= constantPool.getConstant(k, Constants.CONSTANT_Utf8);
					instruction= new StringLiteral(((ConstantUtf8) constant).getBytes());
				}
				else if (constant.getTag() == Constants.CONSTANT_Class)
				{
					Signature signature= Project.getSingleton().getSignature(((ConstantClass) constant).getBytes(constantPool));
					instruction= new ClassLiteral(signature);
				}
				else
				{
					throw new RuntimeException("Cannot handle constant tag: " + constant.getTag());
				}
				break;
			}

			case Const.RET:
			{

				int index= readUnsigned();
				ReturnStatement r= new ReturnStatement(currentIndex, currentIndex);
				r.setExpression(createVariableBinding(index, Type.INT, false));
				instruction= r;
				break;
			}

			case Const.RETURN:

			case Const.IRETURN:

			case Const.FRETURN:

			case Const.LRETURN:

			case Const.DRETURN:

			case Const.ARETURN:
			{

				ReturnStatement r= new ReturnStatement(currentIndex, currentIndex);
				if (opcode != Const.RETURN)
				{
					r.setExpression(stack.pop());
				}
				instruction= r;
				break;
			}

			case Const.POP:

			case Const.POP2:
			{
				if (opcode == Const.POP2 && form.getIndex() == 1)
				{
					ASTNode a= stack.pop();
					//			cNode.block.appendChild(a);
					a= stack.pop();
					//			cNode.block.appendChild(a);
					//		    throw new UnsupportedOperationException("InstructionType " + instructionType.getName() + " not supported");
				}
				else
				{
					ASTNode a= stack.pop();
					if (!(a instanceof VariableBinding))
					{

						// be an ASTNode,
						// because it has no location.
						cNode.block.appendChild(a);
					}
				}
				instruction= new NoOperation();
				break;
			}

			case Const.NOP:
				// Format: nop
				// Operand stack: ... -> ...
				return new NoOperation();

			case Const.XXXUNUSEDXXX:
				// Format: xxxunusedxxx
				// Operand stack: ... -> ...
				logger.info("Byte code contains unused operation (Ignored)");
				return new NoOperation();

			case Const.INVOKEINTERFACE:
				// Format: invokeinterface, index(short), count(byte), 0(byte)
				// Operand stack: ..., objectref(), arg1(), ...(), argN() -> ...
			case Const.INVOKESPECIAL:
				// Format: invokespecial
				// Operand stack: ..., objectref(), arg1(), ...(), argN() -> ...
			case Const.INVOKEVIRTUAL:
				// Format: invokevirtual
				// Operand stack: ..., objectref(), arg1(), ...(), argN() -> ...
			case Const.INVOKESTATIC:
			{
				// Format: invokestatic, index(short)
				// Operand stack: ..., arg1(), ...(), argN() -> ...
				int index= bytes.readUnsignedShort();
				MethodBinding methodBinding= MethodBinding.lookup(index, constantPool);
				MethodInvocation invocation= new MethodInvocation(methodDecl, methodBinding);

				// Processor.getLogger().finer(method.getName() + "->" +
				// invocation.binding);

				int nArgs= methodBinding.getParameterTypes().length;
				int kk= stack.size() - nArgs;
				for (int i= 0; i < nArgs; i++)
				{
					Expression arg= (Expression) stack.get(kk);
					stack.remove(kk);
					invocation.addArgument(arg);
				}

				opStackDelta= -nArgs;

				if (opcode == Const.INVOKEVIRTUAL || opcode == Const.INVOKESPECIAL || opcode == Const.INVOKEINTERFACE)
				{
					opStackDelta--;
					invocation.setExpression(stack.pop());
				}
				else
				{ // INVOKESTATIC
					// Name name = new Name(method.getClassName());
					// invocation.setExpression(name);
				}

				if (methodBinding.getReturnType() != Type.VOID)
				{
					opStackDelta++;
				}

				if (opcode == Const.INVOKEINTERFACE)
				{
					bytes.readUnsignedByte(); // historical, redundant number of
					// arguments.
					bytes.readUnsignedByte(); // Last byte is a reserved space.
				}
				else if (opcode == Const.INVOKESPECIAL)
				{
					invocation.isSpecial= true;
				}

				// if (opcode==Const.INVOKESPECIAL && stack.size() > 0 &&
				// stack.peek() instanceof ClassInstanceCreation &&
				// methodBinding.getName().equals("<init>")) {
				// ClassInstanceCreation cic = (ClassInstanceCreation) stack.pop();
				// List args = invocation.getArguments();
				// for (int i=0; i<args.size(); i++)
				// cic.addArgument((Expression) args.get(i));
				// cic.signature = method.getSignature();
				// //invocation.setExpression(cic.getType());
				// instruction = cic;
				// } else {
				// instruction = invocation;
				// }
				instruction= invocation;
				break;
			}

			case Const.MONITORENTER:
			{
				// Format: monitorenter
				// Operand stack: ..., objectref() -> ...
				SynchronizedBlock sb= new SynchronizedBlock();
				sb.monitor= stack.pop();
				sb.widen(sb.monitor);
				sb.setEndIndex(currentIndex);
				instruction= sb;
				break;
			}
			case Const.MONITOREXIT:
				// Format: monitorexit
				// Operand stack: ..., objectref() -> ...
				instruction= new NoOperation();
				instruction.widen(stack.pop());
				instruction.setEndIndex(currentIndex);
				break;

			default:
				throw new UnsupportedOperationException("InstructionType " + instructionType.getName() + " not supported");
				// break;
		}

		if (opcode != Const.WIDE && wide)
			throw new RuntimeException("Expected wide operation");

		instruction.setStackDelta(opStackDelta);
		if (opcode < Const.DUP || opcode > Const.DUP2_X2)
		{
			instruction.leftWiden(currentIndex);
			instruction.rightWiden(bytes.getIndex() - 1);
		}
		currentNode= instruction;
		return instruction;
	}

	public static void setClassNotReversible(MethodDeclaration methodDeclaration)
	{
		ObjectType declaringClass= methodDeclaration.getMethodBinding().getDeclaringClass();
		Log.getLogger().debug("Not reversible method: " + methodDeclaration.getMethodBinding().getName() + " in: " + declaringClass);
		ClassUnit classUnit= Project.getSingleton().getClassUnit(declaringClass.getClassName());
		classUnit.addNotReversibleMethod(extractMethodNameSignature(methodDeclaration.getMethodBinding()));
		//	classUnit.setReversible(false);
	}

	public static String extractMethodNameSignature(MethodBinding methodBinding)
	{
		return methodBinding.getName() + "#" + methodBinding.getSignature();
	}

	ConditionalBranch createConditional(int currentIndex, InfixExpression.Operator operator) throws IOException
	{
		ConditionalBranch c= new ConditionalBranch(currentIndex + bytes.readShort());
		InfixExpression be= new InfixExpression(operator);
		Expression rightOperand= stack.pop();
		be.setOperands(stack.pop(), rightOperand);
		c.setExpression(be);
		return c;
	}

	ConditionalBranch createConditional(int currentIndex, InfixExpression.Operator operator, Expression rightOperand) throws IOException
	{
		ConditionalBranch c= new ConditionalBranch(currentIndex + bytes.readShort());
		Expression leftOperand= stack.pop();

		if (leftOperand.getTypeBinding() != null && leftOperand.getTypeBinding() == Type.BOOLEAN)
		{
			if (operator == InfixExpression.Operator.EQUALS && NumberLiteral.isZero(rightOperand))
			{
				c.setExpression(Optimizer.negate(leftOperand));
			}
			else
			{
				c.setExpression(leftOperand);
			}
		}
		else
		{
			InfixExpression be= new InfixExpression(operator);
			be.setOperands(leftOperand, rightOperand);
			c.setExpression(be);
		}

		return c;
	}

	private String getFieldName(ConstantFieldref fieldRef)
	{
		ConstantNameAndType nameAndType= (ConstantNameAndType) constantPool.getConstant(fieldRef.getNameAndTypeIndex());
		return nameAndType.getName(constantPool);
	}

	private Type getFieldType(ConstantFieldref fieldRef)
	{
		ConstantNameAndType nameAndType= (ConstantNameAndType) constantPool.getConstant(fieldRef.getNameAndTypeIndex());
		return Type.getType(nameAndType.getSignature(constantPool));
	}

	public static String constantToString(Constant c, ConstantPool constantPool) throws ClassFormatException
	{
		String str;
		byte tag= c.getTag();

		switch (tag)
		{
			case Constants.CONSTANT_Class:
				str= Utility.compactClassName(((ConstantClass) c).getBytes(constantPool), false);
				break;

			case Constants.CONSTANT_String:
				str= "\"" + Utils.escape(((ConstantString) c).getBytes(constantPool)) + "\"";
				break;

			case Constants.CONSTANT_Utf8:
				str= ((ConstantUtf8) c).getBytes();
				break;
			case Constants.CONSTANT_Double:
				str= "" + ((ConstantDouble) c).getBytes();
				break;
			case Constants.CONSTANT_Float:
				str= "" + ((ConstantFloat) c).getBytes();
				break;
			case Constants.CONSTANT_Long:
				str= "" + ((ConstantLong) c).getBytes();
				break;
			case Constants.CONSTANT_Integer:
				str= "" + ((ConstantInteger) c).getBytes();
				break;

			case Constants.CONSTANT_NameAndType:
				str= ((ConstantNameAndType) c).getName(constantPool);
				break;

			case Constants.CONSTANT_InterfaceMethodref:
			case Constants.CONSTANT_Methodref:
			case Constants.CONSTANT_Fieldref:
				str= ((ConstantCP) c).getClass(constantPool);
				break;

			default: // Never reached
				throw new RuntimeException("Unknown constant type " + tag);
		}

		return str;
	}

}
