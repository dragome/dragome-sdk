package com.dragome.compiler.generators;

import com.dragome.compiler.ast.ASTNode;
import com.dragome.compiler.ast.ArrayAccess;
import com.dragome.compiler.ast.ArrayCreation;
import com.dragome.compiler.ast.ArrayInitializer;
import com.dragome.compiler.ast.Assignment;
import com.dragome.compiler.ast.Block;
import com.dragome.compiler.ast.BooleanLiteral;
import com.dragome.compiler.ast.BreakStatement;
import com.dragome.compiler.ast.CastExpression;
import com.dragome.compiler.ast.CatchClause;
import com.dragome.compiler.ast.ClassInstanceCreation;
import com.dragome.compiler.ast.ClassLiteral;
import com.dragome.compiler.ast.ConditionalExpression;
import com.dragome.compiler.ast.ContinueStatement;
import com.dragome.compiler.ast.DoStatement;
import com.dragome.compiler.ast.FieldAccess;
import com.dragome.compiler.ast.IfStatement;
import com.dragome.compiler.ast.InfixExpression;
import com.dragome.compiler.ast.InstanceofExpression;
import com.dragome.compiler.ast.MethodDeclaration;
import com.dragome.compiler.ast.MethodInvocation;
import com.dragome.compiler.ast.Name;
import com.dragome.compiler.ast.NullLiteral;
import com.dragome.compiler.ast.NumberLiteral;
import com.dragome.compiler.ast.PostfixExpression;
import com.dragome.compiler.ast.PrefixExpression;
import com.dragome.compiler.ast.PrimitiveCast;
import com.dragome.compiler.ast.ReturnStatement;
import com.dragome.compiler.ast.StringLiteral;
import com.dragome.compiler.ast.SwitchCase;
import com.dragome.compiler.ast.SwitchStatement;
import com.dragome.compiler.ast.SynchronizedBlock;
import com.dragome.compiler.ast.ThisExpression;
import com.dragome.compiler.ast.ThrowStatement;
import com.dragome.compiler.ast.TryStatement;
import com.dragome.compiler.ast.TypeDeclaration;
import com.dragome.compiler.ast.VariableBinding;
import com.dragome.compiler.ast.VariableDeclaration;
import com.dragome.compiler.ast.WhileStatement;

/**
 * Copyright by Wolfgang Kuehn 2005
 */
public abstract class AbstractVisitor
{

	public abstract void visit(ASTNode node);

	public void visit(TypeDeclaration node)
	{
		visit((ASTNode) node);
	}

	public void visit(MethodDeclaration node)
	{
		visit((ASTNode) node);
	}

	public void visit(DoStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(WhileStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(IfStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(TryStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(Block node)
	{
		visit((ASTNode) node);
	}

	public void visit(InfixExpression node)
	{
		visit((ASTNode) node);
	}

	public void visit(PrefixExpression node)
	{
		visit((ASTNode) node);
	}

	public void visit(PostfixExpression node)
	{
		visit((ASTNode) node);
	}

	public void visit(SwitchStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(SwitchCase node)
	{
		visit((ASTNode) node);
	}

	public void visit(CatchClause node)
	{
		visit((ASTNode) node);
	}

	public void visit(ReturnStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(Assignment node)
	{
		visit((ASTNode) node);
	}

	public void visit(NumberLiteral node)
	{
		visit((ASTNode) node);
	}

	public void visit(StringLiteral node)
	{
		visit((ASTNode) node);
	}

	public void visit(ClassLiteral node)
	{
		visit((ASTNode) node);
	}

	public void visit(NullLiteral node)
	{
		visit((ASTNode) node);
	}

	public void visit(MethodInvocation node)
	{
		visit((ASTNode) node);
	}

	public void visit(ClassInstanceCreation node)
	{
		visit((ASTNode) node);
	}

	public void visit(ArrayInitializer node)
	{
		visit((ASTNode) node);
	}

	public void visit(ArrayCreation node)
	{
		visit((ASTNode) node);
	}

	public void visit(ArrayAccess node)
	{
		visit((ASTNode) node);
	}

	public void visit(VariableDeclaration node)
	{
		visit((ASTNode) node);
	}

	public void visit(VariableBinding node)
	{
		visit((ASTNode) node);
	}

	public void visit(ThisExpression node)
	{
		visit((ASTNode) node);
	}

	public void visit(FieldAccess node)
	{
		visit((ASTNode) node);
	}

	public void visit(BreakStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(ContinueStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(CastExpression node)
	{
		visit((ASTNode) node);
	}

	public void visit(BooleanLiteral node)
	{
		visit((ASTNode) node);
	}

	public void visit(ThrowStatement node)
	{
		visit((ASTNode) node);
	}

	public void visit(Name node)
	{
		visit((ASTNode) node);
	}

	public void visit(InstanceofExpression node)
	{
		visit((ASTNode) node);
	}

	public void visit(ConditionalExpression node)
	{
		visit((ASTNode) node);
	}

	public void visit(SynchronizedBlock node)
	{
		visit((ASTNode) node);
	}

	public void visit(PrimitiveCast node)
	{
		visit((ASTNode) node);
	}

}
