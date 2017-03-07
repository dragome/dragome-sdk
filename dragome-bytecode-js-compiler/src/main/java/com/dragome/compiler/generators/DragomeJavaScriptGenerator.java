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

package com.dragome.compiler.generators;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import com.dragome.commons.compiler.annotations.DragomeCompilerSettings;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.Project;
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
import com.dragome.compiler.ast.Expression;
import com.dragome.compiler.ast.FieldAccess;
import com.dragome.compiler.ast.FieldRead;
import com.dragome.compiler.ast.IfStatement;
import com.dragome.compiler.ast.InfixExpression;
import com.dragome.compiler.ast.InstanceofExpression;
import com.dragome.compiler.ast.MethodBinding;
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
import com.dragome.compiler.parser.Pass1;
import com.dragome.compiler.type.Signature;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.units.FieldUnit;
import com.dragome.compiler.units.ProcedureUnit;
import com.dragome.compiler.utils.Log;
import com.dragome.compiler.utils.Utils;

public class DragomeJavaScriptGenerator extends Generator
{

	public static final String DRAGOME_PACKAGE= "dragome";

	public static final String FIELD_TYPE_SEPARATOR= "___";

	private static int parametersSignaturesCounter;

	public static Map<String, Integer> parametersSignatures= new LinkedHashMap<String, Integer>();

	private ASTNode currentNode;

	private MethodDeclaration currentMethodDeclaration;

	private Project project;

	private ByteArrayOutputStream baStream= new ByteArrayOutputStream();

	//    private List<MethodInvocation> superMethods= new ArrayList<MethodInvocation>();

	public DragomeJavaScriptGenerator(Project theProject)
	{
		super();
		project= theProject;
		setOutputStream(new PrintStream(baStream));
	}

	private String reset()
	{
		flush();
		String s= baStream.toString();
		baStream.reset();
		return s;
	}

	private void consume(Object object)
	{
		object= object == null ? object : object;
	}

	public void visit(TypeDeclaration theTypeDecl)
	{
		Map<String, String> annotations= theTypeDecl.getAnnotations();

		ClassUnit classUnit= project.getClassUnit(theTypeDecl.getType());

		lastChar= '\0';
		currentNode= null;
		depth= 0;

		typeDecl= theTypeDecl;

		boolean isInterface= Modifier.isInterface(typeDecl.getAccess());

		String type= isInterface ? "Interface" : "Class";
		print("qx." + type + ".define(\"");
		print(normalizeExpression(theTypeDecl.getClassName()));
		println("\", ");
		println("{");

		depth++;

		if (classUnit.getSuperUnit() != null)
		{
			String superUnitName= normalizeExpression(classUnit.getSuperUnit().getName());
			print("extend: " + superUnitName);
			println(",");
		}
		else
		{
			if (!classUnit.getName().equals("java.lang.Object"))
			{
				print("extend: java_lang_Object");
			}
			else
			{
				print("extend: qx.core.Object");
			}
			println(",");
		}

		if (!classUnit.getName().equals("java.lang.Object"))
			println("construct: function(){},");

		classUnit.setData(reset());

		List fields= theTypeDecl.getFields();
		for (int i= 0; i < fields.size(); i++)
		{
			VariableDeclaration decl= (VariableDeclaration) fields.get(i);

			if (decl.getLocation() == VariableDeclaration.NON_LOCAL)
			{
				//	    if (Modifier.isStatic(decl.getModifiers()))
				//		continue;
				//		indent();
				decl.visit(this);
				//println(",");
			}
		}
		depth--;
		//	String superType= null;
		//
		//	if (theTypeDecl.getSuperType() != null && !Modifier.isInterface(theTypeDecl.getAccess()))
		//	{
		//	    superType= Project.getSingleton().getSignature(theTypeDecl.getSuperType().getClassName()).getCommentedId();
		//	}

		//	for (int i= 0; i < fields.size(); i++)
		//	{
		//	    VariableDeclaration decl= (VariableDeclaration) fields.get(i);
		//
		//	    if (!Modifier.isStatic(decl.getModifiers()))
		//		continue;
		//	    indent();
		//	    decl.visit(this);
		//	    println(";");
		//	}

		depth++;
		MethodDeclaration[] methods= theTypeDecl.getMethods();
		List<String> processedMethods= new ArrayList<String>();

		for (int i= 0; i < methods.length; i++)
		{
			MethodDeclaration method= methods[i];
			currentMethodDeclaration= method;
			try
			{
				String normalizeExpression= normalizeExpression(Project.getSingleton().getSignature(method.getMethodBinding().toString()).relative());

				if (!processedMethods.contains(normalizeExpression))
				{
					processedMethods.add(normalizeExpression);
					method.visit(this);
				}
				else
					System.out.println("duplicado!");
				//		System.out.println("llego!");
			}
			catch (RuntimeException ex)
			{
				throw Utils.generateException(ex, method, currentNode);
			}
		}
		processedMethods.clear();

		depth--;

		reset();
		depth++;
		//addSuperMethodsDefinition();
		depth--;

		classUnit.setData(classUnit.getData() + reset());
	}

	//    private void addSuperMethodsDefinition()
	//    {
	//	for (MethodInvocation methodInvocation : superMethods)
	//	{
	//	    indent();
	//	    String declaringClass= normalizeExpression(methodInvocation.getMethodBinding().getDeclaringClass().getClassName());
	//	    String signature= normalizeExpression(getSignatureOfInvocation(methodInvocation));
	//
	//	    print("this.");
	//	    print(declaringClass);
	//	    print("_");
	//	    print(signature);
	//	    print("= ");
	//	    print("this.");
	//	    print(signature);
	//	    println(";");
	//	}
	//
	//	superMethods.clear();
	//    }

	public void visit(MethodDeclaration method)
	{
		String local_alias= method.getAnnotationsValues().get(MethodAlias.class.getName() + "#" + "local_alias");
		boolean hasLocalAlias= local_alias != null;

		String className= normalizeExpression(method.getMethodBinding().getDeclaringClass().getClassName());

		String annotationKey= DragomeCompilerSettings.class.getName() + "#" + "value";
		String compiler= DragomeJsCompiler.compiler.compilerType.name();
		String methodCompilerType= method.getAnnotationsValues().get(annotationKey);
		String classCompilerType= typeDecl.getAnnotations().get(annotationKey);

		if (methodCompilerType != null)
			compiler= methodCompilerType;
		else if (classCompilerType != null)
			compiler= classCompilerType;

		//	String className2= method.getMethodBinding().getDeclaringClass().getClassName();
		// !className2.startsWith("java.lang") && (method.getAnnotationsValues().get("alias") == null || className2.contains("JSONTokener")

		if ("Strict".equalsIgnoreCase(compiler))
		{
			ClassUnit classUnit= project.getClassUnit(method.getMethodBinding().getDeclaringClass().getClassName());
			classUnit.addNotReversibleMethod(Pass1.extractMethodNameSignature(method.getMethodBinding()));
		}

		MethodBinding methodBinding= method.getMethodBinding();
		ProcedureUnit unit= project.getProcedureUnit(methodBinding);

		boolean isNative= Modifier.isNative(method.getAccess());
		//		if (method.getBody() == null && isNative)
		//		{
		//			if (isNative || Modifier.isAbstract(method.getAccess()) || Modifier.isInterface(typeDecl.getAccess()))
		//			{
		//				return;
		//			}
		//			throw new RuntimeException("Method " + method + " with access " + method.getAccess() + " may not have empty body");
		//		}

		//	if (!dragomeJsCompiler.compiler.isCompression())
		//	{
		//	    println("/* " + unit.getAbsoluteSignature() + " */");
		//	}

		String closingString;
		Signature signature= Project.getSingleton().getSignature(methodBinding.toString()).relative();
		String signatureReplaced= normalizeExpression(signature);

		boolean isString= typeDecl.getClassName().equals("java.lang.String");
		if (isString && method.isInstanceConstructor())
		{

			Block body= method.getBody();

			body.removeChild(body.getFirstChild());

			MethodInvocation consume= (MethodInvocation) body.getLastChild();
			body.removeChild(consume);

			ReturnStatement r= new ReturnStatement(0, 0);
			r.setExpression((Expression) consume.getArguments().get(0));
			body.appendChild(r);

			print("_dragomeJs.StringInit" + signatureReplaced + " = function(");
			closingString= "};\n";
		}
		else
		{
			if (Modifier.isStatic(method.getAccess()))
			{
				print(ClassUnit.STATIC_MEMBER);
				//		print(className + ".");
			}
			else
			{
				//		if (typeDecl.getClassName().equals("java.lang.String"))
				//		    print("String.prototype." + signatureReplaced + "=");

				//		print("this.");
			}

			print(signatureReplaced);
			print(": ");
			String alias= method.getAnnotationsValues().get(MethodAlias.class.getName() + "#" + "alias");
			if (alias != null)
				print(alias + "= ");

			if (hasLocalAlias)
				print(className + "_$_" + local_alias + "= ");

			print("function (");
			closingString= "}";
		}

		printParams(method);
		println(")");
		println("{");

		depth= 1;

		Collection<VariableDeclaration> localVariables= method.getLocalVariables();
		if (localVariables.size() > 0)
			print("var ");

		int i= 0;
		for (VariableDeclaration decl : localVariables)
		{
			decl.visit(this);

			if (++i < localVariables.size())
				print(",");
		}

		if (localVariables.size() > 0)
			println(";");

		depth= 0;

		if (method.getBody() != null)
			visit_(method.getBody());
		else if (isNative)
			println("return dragomeJs.resolveNativeMethod(this, \"" + signatureReplaced + "\").apply(this, arguments);");

		if (method.isInstanceConstructor() && !isString)
			print("return this;\n");

		print(closingString);

		if (hasLocalAlias)
		{
			print(", \n");
			print(local_alias + ": " + className + "_$_" + local_alias);
		}

		unit.setData(reset());
		Log.getLogger().debug("Generating JavaScript for " + unit);
	}

	private void printParams(MethodDeclaration method)
	{
		Iterator<VariableDeclaration> iterator= method.getParameters().iterator();
		while (iterator.hasNext())
		{
			VariableDeclaration decl= iterator.next();

			//			if (hasToEscapeVariable(decl.getName()))
			//				print("_");

			decl.visit(this);
			print(iterator.hasNext() ? ", " : "");
		}
	}

	public static String normalizeExpression(Object object)
	{
		if (object instanceof Signature)
		{
			Signature signature= (Signature) object;
			String string= signature.toString();

			string= string.replaceAll("\\[\\]", "_ARRAYTYPE");
			String result= string.replaceAll("\\(\\)", "\\$");
			result= result.replaceAll("\\)", "\\$").replaceAll("\\(", "___").replaceAll("\\.", "_").replaceAll(",", "__").replaceAll("<", "").replaceAll(">", "").replaceAll("\\[", "_").replaceAll("\\]", "_").replaceAll(";", "\\$");

			if (signature.isMethod() || signature.isConstructor())
			{
				result= "$" + result;

				if (signature.isConstructor())
				{
					result= result.replaceAll("___$", "");
					result= result.replace("$init", "$init_");
					return "$" + result;
				}
				else
				{
					result= result.replaceAll("___$", "");
					if (result.contains("clinit"))
						result= "$" + result + "_";

					if ("$$clinit$void_".equals(result))
						result= "$$clinit_";

					return result;
				}
			}

			return result;
		}
		else
		{
			String string= object.toString();

			string= string.replaceAll("\\[\\]", "_ARRAYTYPE");

			//string= modifyMethodName(string);
			return string.replaceAll("\\(", "_").replaceAll("\\)", "_").replaceAll("\\.", "_").replaceAll(",", "__").replaceAll("<", "_").replaceAll(">", "_").replaceAll("\\[", "_").replaceAll("\\]", "_").replaceAll(";", "\\$");
		}
	}

	public static String modifyMethodName(String string)
	{
		if (string.contains("("))
		{
			int indexOf= string.indexOf("(");
			String parametersPart= string.substring(indexOf);

			Integer counter= parametersSignatures.get(parametersPart);
			if (counter == null)
				parametersSignatures.put(parametersPart, counter= ++parametersSignaturesCounter);

			string= string.substring(0, indexOf) + "_" + counter;
		}
		return string;
	}

	public void visit(DoStatement doStmt)
	{
		println("do {");
		visit_(doStmt.getBlock());
		indent("} while (");
		doStmt.getExpression().visit(this);
		print(")");
	}

	public void visit(WhileStatement whileStmt)
	{
		print("while (");
		whileStmt.getExpression().visit(this);
		println(") {");
		visit_(whileStmt.getBlock());
		indent("}");
	}

	public void visit(IfStatement ifStmt)
	{
		print("if (");
		ifStmt.getExpression().visit(this);
		println(") {");
		visit_(ifStmt.getIfBlock());
		indent("}");
		if (ifStmt.getElseBlock() != null)
		{
			println(" else {");
			visit_(ifStmt.getElseBlock());
			indent("}");
		}
	}

	public void visit(TryStatement tryStmt)
	{
		println("try {");
		visit_(tryStmt.getTryBlock());
		indent("} ");
		Block clauses= tryStmt.getCatchStatements();
		CatchClause clause= (CatchClause) clauses.getFirstChild();

		String ex= null;
		if (clause != null)
		{
			ex= clause.getException().getName();
		}

		//	if (clauses.getChildCount() == 1)
		//	{
		//	    print("catch(" + ex + ") ");
		//	    clause.visit(this);
		//	}
		if (clauses.getChildCount() > 0)
		{
			println("catch(" + ex + ") {");
			depth++;
			indent();
			while (clause != null)
			{
				if (clause.getException().getType() != null)
					print("if (dragomeJs.isInstanceof(" + ex + ", " + normalizeExpression(Utils.getSignature(clause.getException().getType())) + ")) ");
				else
					print("if (true)");

				clause.visit(this);
				clause= (CatchClause) clause.getNextSibling();
				if (clause == null)
					break;
				print(" else ");
			}

			print(" else throw dragomeJs.nullSaveException(" + ex + "); ");

			println("");
			depth--;
			indent("}");
		}

		Block finallyBlock= tryStmt.getFinallyBlock();
		if (finallyBlock != null)
		{
			println(" finally {");
			visit_(finallyBlock);
			indent("}");
		}
	}

	public void visit(CatchClause clause)
	{
		visit((Block) clause);
	}

	public void visit_(Block block)
	{
		depth++;
		ASTNode node= block.getFirstChild();
		while (node != null)
		{
			currentNode= node;
			if (DragomeJsCompiler.compiler.isGenerateLineNumbers())
			{
				int lineNumber= currentMethodDeclaration.getLineNumberCursor().getAndMarkLineNumber(node);
				if (lineNumber != -1)
				{
					print("//ln=" + lineNumber + ";\n");
				}
			}

			indent();
			if (node instanceof Block && ((Block) node).isLabeled())
			{
				print(((Block) node).getLabel() + ": ");
			}

			node.visit(this);

			if (lastChar == '}')
			{
				println("");
			}
			else if (lastChar == ';')
			{
				getOutputStream().println("");
			}
			else
			{
				println(";");
			}
			node= node.getNextSibling();
		}
		depth--;
	}

	public void visit(Block block)
	{
		println("{");
		visit_(block);
		indent("}");
	}

	public void visit(SynchronizedBlock block)
	{
		println("{ // Synchronized.");
		visit_(block);
		indent("}");
	}

	public void visit(PrefixExpression binOp)
	{
		print(binOp.getOperator().toString() + "(");
		binOp.getOperand().visit(this);
		print(")");
	}

	public void visit(PostfixExpression binOp)
	{

		binOp.getOperand().visit(this);
		print(binOp.getOperator().toString());
	}

	private void bracket(ASTNode node, InfixExpression.Operator op)
	{
		boolean b= false; //node instanceof InfixExpression && ((InfixExpression) node).getOperator() == op;
		if (b || node instanceof NumberLiteral || node instanceof NullLiteral || node instanceof FieldAccess || node instanceof VariableBinding)
		{
			node.visit(this);
		}
		else
		{
			print("(");
			node.visit(this);
			print(")");
		}
	}

	public void visit(InfixExpression binOp)
	{
		InfixExpression.Operator op= binOp.getOperator();
		Expression left= binOp.getLeftOperand();
		Expression right= binOp.getRightOperand();

		boolean isTruncate= false;
		Type type= binOp.getTypeBinding();

		if (op == InfixExpression.Operator.DIVIDE && (type.equals(Type.LONG) || type.equals(Type.INT)))
		{
			isTruncate= true;
			print("dragomeJs.trunc(");
		}

		bracket(left, op);
		//		boolean leftIsObject= ObjectType.class.isAssignableFrom(left.getTypeBinding().getClass());
		//		boolean rightIsObject= ObjectType.class.isAssignableFrom(right.getTypeBinding().getClass());
		//		boolean isEqualOrNotEqual= op.toString().equals("==") || op.toString().equals("!=");
		//
		//		if ((leftIsObject && rightIsObject) && isEqualOrNotEqual)
		//			print(" " + op + "= ");
		//		else
		print(" " + op + " ");

		bracket(right, op);

		if (isTruncate)
		{
			print(")");
		}
	}

	public void visit(ConditionalExpression ce)
	{
		ce.getConditionExpression().visit(this);
		print("?");
		ce.getThenExpression().visit(this);
		print(":");
		ce.getElseExpression().visit(this);
	}

	public void visit(InstanceofExpression node)
	{
		Signature signature= Project.getSingleton().getArraySignature(node.getRightOperand());

		if (signature.isArrayType())
			print("dragomeJs.isInstanceofArray (");
		else
			print("dragomeJs.isInstanceof (");

		node.getLeftOperand().visit(this);
		print(", ");

		String normalizeExpression= normalizeExpression(signature.toString());

		if (signature.isArrayType())
			normalizeExpression= "\"" + normalizeExpression + "\"";

		print(normalizeExpression); 
		print(")");
	}

	public void visit(SwitchStatement switchStmt)
	{
		print("switch (");
		switchStmt.getExpression().visit(this);
		println(") {");
		ASTNode node= switchStmt.getFirstChild();
		while (node != null)
		{
			SwitchCase sc= (SwitchCase) node;
			sc.visit(this);
			node= node.getNextSibling();
		}
		indentln("}");
	}

	public void visit(SwitchCase switchCase)
	{
		Iterator<NumberLiteral> iter= switchCase.getExpressions().iterator();
		if (iter.hasNext())
		{
			while (iter.hasNext())
			{
				NumberLiteral expression= iter.next();
				indent("case ");
				expression.visit(this);
				println(":");
			}
		}
		else
		{
			indentln("default:");
		}
		visit_(switchCase);
	}

	public void visit(ASTNode stmt)
	{
		print(stmt.toString());
	}

	public void visit(ReturnStatement r)
	{
		print("return");
		if (r.getExpression() != null)
		{
			print(" ");
			r.getExpression().visit(this);
		}
	}

	public void visit(Assignment a)
	{
		Expression rhs= a.getRightHandSide();

		if (rhs instanceof ClassInstanceCreation)
		{
			ClassInstanceCreation cic= (ClassInstanceCreation) rhs;
			if (cic.getTypeBinding().toString().equals("java.lang.String"))
			{
				return;
			}
		}

		a.getLeftHandSide().visit(this);
		print(" " + a.getOperator() + " ");
		if (VariableBinding.isBoolean(a.getLeftHandSide()))
		{
			if (NumberLiteral.isZero(rhs))
			{
				print("false");
			}
			else if (NumberLiteral.isOne(rhs))
			{
				print("true");
			}
			else
			{
				rhs.visit(this);
			}
		}
		else
		{
			rhs.visit(this);
		}
	}

	public void visit(NumberLiteral literal)
	{
		print("" + literal.getValue());
	}

	public void visit(StringLiteral literal)
	{
		print(Utils.escape(literal.getValue()));
	}

	public void visit(ClassLiteral literal)
	{
		MethodBinding binding= MethodBinding.lookup("java.lang.Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
		MethodInvocation mi= new MethodInvocation(currentMethodDeclaration, binding);
		mi.addArgument(new StringLiteral(literal.getSignature().toString()));
		visit(mi);
	}

	public void visit(NullLiteral literal)
	{
		consume(literal);
		print("null");
	}

	private void generateList(List arguments)
	{
		for (int i= 0; i < arguments.size(); i++)
		{
			print(i == 0 ? "" : ", ");
			((ASTNode) arguments.get(i)).visit(this);
		}
	}

	private boolean isW3C(MethodInvocation invocation)
	{
		MethodBinding methodBinding= invocation.getMethodBinding();

		String name= methodBinding.getName();
		int argCount= invocation.getArguments().size();
		boolean isSetter= name.startsWith("set") && argCount == 1;
		boolean isGetter= name.startsWith("get") && argCount == 0;

		if (!isSetter && !isGetter)
			return false;

		if (methodBinding.equals("org.w3c.dom5.NamedNodeMap"))
		{
			if (name.equals("setNamedItemNS") || name.equals("setNamedItem"))
				return false;
		}
		if (methodBinding.equals("org.w3c.dom5.Element"))
		{
			if (name.equals("setAttributeNode") || name.equals("setAttributeNodeNS"))
				return false;
		}

		if (name.equals("getContentDocument"))
			return false;
		if (name.equals("getButton"))
			return false;

		return true;

	}

	private void generateScriptCode(MethodInvocation invocation)
	{
		MethodBinding methodBinding= invocation.getMethodBinding();
		String name= methodBinding.getName();
		List args= invocation.getArguments();
		String firstArg= null;
		boolean isVariable= false;
		if (!(args.get(0) instanceof StringLiteral))
		{
			if (args.get(0) instanceof VariableBinding)
			{
				VariableBinding variableBinding= (VariableBinding) args.get(0);
				firstArg= variableBinding.getName();
				isVariable= true;
			}
			else if (args.get(0) instanceof MethodInvocation)
			{
			}
			else
				throw new RuntimeException("First argument to " + methodBinding + " must be a string literal");
		}
		else
			firstArg= ((StringLiteral) args.get(0)).getValue();

		if (name.equals("put") || name.equals("putMethodReference"))
		{
			if (isVariable)
			{
				print("eval(\"var \"+");
				print(firstArg + "+\"=");
				((ASTNode) args.get(1)).visit(this);
				print("\")");
			}
			else
			{
				if (firstArg.indexOf('.') == -1)
				{
					print("var ");
				}
				print(firstArg + "=");
				((ASTNode) args.get(1)).visit(this);
			}
		}
		else if (name.startsWith("eval"))
		{
			String evalScript= firstArg;
			if (firstArg == null && args.get(0) instanceof MethodInvocation)
			{
				MethodInvocation methodInvocation= (MethodInvocation) args.get(0);
				print("eval(");
				methodInvocation.visit(this);
				print(")");
			}
			else
			{
				if (isVariable)
					evalScript= "eval(" + firstArg + ")";

				if (name.startsWith("evalCasting"))
				{
					ASTNode nextSibling= invocation.getFirstChild().getNextSibling();

					if (nextSibling instanceof ClassLiteral)
					{
						Signature signature= ((ClassLiteral) nextSibling).getSignature();
						print("dragomeJs.castTo(" + evalScript + ", \"" + signature + "\")");
					}
					else
					{
						VariableBinding variableBinding= (VariableBinding) nextSibling;
						print("dragomeJs.castTo2(" + evalScript + ", " + variableBinding.getName() + ")");
					}
				}
				else
					print(evalScript);
			}
		}
		else
			throw new IllegalArgumentException("Cannot handle method " + name);
	}

	private void generateArguments(MethodInvocation invocation)
	{
		Signature signature= getSignatureOfInvocation(invocation);
		print(normalizeExpression(signature));

		print("(");
		generateList(invocation.getArguments());
		print(")");
	}

	private static Signature getSignatureOfInvocation(MethodInvocation invocation)
	{
		MethodBinding methodBinding= invocation.getMethodBinding();
		Signature signature= Project.getSingleton().getSignature(methodBinding.getDeclaringClass().getClassName());
		signature= Project.getSingleton().getSignature(methodBinding.getRelativeSignature());
		return signature;
	}

	public void visit(MethodInvocation invocation)
	{
		MethodBinding methodBinding= invocation.getMethodBinding();
		String name= methodBinding.getName();
		String className= methodBinding.getDeclaringClass().getClassName();

		if (className.equals("com.dragome.commons.javascript.ScriptHelper"))
		{
			generateScriptCode(invocation);
			return;
		}

		if (className.equals("javax.script.ScriptEngine") && (name.equals("put") || name.equals("eval")))
		{
			generateScriptCode(invocation);
			return;
		}

		ASTNode expression= invocation.getExpression();

		// expression.visit(this);
		// } else {
		// //throw new UnsupportedOperationException("Invocation of " +
		// methodBinding + " not supported");
		// }
		// //return;
		// }

		if (className.equals("java.lang.String") && methodBinding.isConstructor())
		{
			if (expression instanceof VariableBinding)
			{
				expression.visit(this);
				print(" = ");
			}
			else
			{
				assert expression instanceof ClassInstanceCreation;
			}

			Signature signature= Project.getSingleton().getSignature(methodBinding.toString()).relative();
			String signatureReplaced= normalizeExpression(signature);

			print("dragomeJs.StringInit" + signatureReplaced + "(");
			//	    print("dragomeJs.StringInit" + signature.getId() + "(");
			generateList(invocation.getArguments());
			print(")");
			return;
		}

		if (invocation.isSuper(typeDecl.getClassName()))
		{
			//	    print(prefix);
			//	    print(INVOKESUPER);
			//	    print("(");

			String string= "arguments.callee.self.superclass.prototype.{methodName}.call(this";

			if (methodBinding.getDeclaringClass().referencesInterface())
			{
				String invokeClassName= normalizeExpression(methodBinding.getDeclaringClass());
				string= invokeClassName + ".$$members.{methodName}.call(this";
			}

			if (Modifier.isStatic(invocation.methodDecl.getAccess()))
				string= "this.superclass.prototype.{methodName}.call(arguments[0]";

			Signature signature= getSignatureOfInvocation(invocation);
			String methodName= normalizeExpression(signature);
			string= string.replace("{methodName}", methodName);

			if (invocation.getArguments().isEmpty())
			{
				print(string);
				print(")");
			}
			else
			{
				print(string);
				print(", ");
				generateList(invocation.getArguments());
				print(")");
			}

			//	    print("this.");
			//	    String declaringClass= normalizeExpression(methodBinding.getDeclaringClass().getClassName());
			//	    print(declaringClass);
			//	    print("_");
			//	    //	    generateArguments(invocation);
			//	    generateList(invocation.getArguments());
			//	    print(")");
			//	    superMethods.add(invocation);

			//	    if (expression == null)
			//	    {
			//		print("null");
			//	    }
			//	    else
			//	    {
			//		expression.visit(this);
			//	    }
			//	    print(", ");
		}
		else if (invocation.isSpecial)
		{
			// print(getSignatureReference(Signature.getSignature(className)));
			// print(", ");

			if (methodBinding.isConstructor() && expression instanceof ThisExpression && !"java.lang.String".equals(className))
			{
				String normalizeExpression= normalizeExpression(className);
				print(normalizeExpression);
				print(".prototype.");
				Signature signature= getSignatureOfInvocation(invocation);
				print(normalizeExpression(signature));
				print(".call(this");
				if (!invocation.getArguments().isEmpty())
					print(",");
				generateList(invocation.getArguments());
				print(")");
			}
			else
			{
				expression.visit(this);
				print(".");
				generateArguments(invocation);
			}

		}
		else if (expression == null)
		{
			boolean isStatic= true;//Modifier.isStatic(invocation.methodDecl.getAccess());

			Signature signature= Project.getSingleton().getSignature(methodBinding.getDeclaringClass().getClassName());
			//			if (isStatic)
			//				print("_getClassForStatic(");
			print(normalizeExpression(signature));
			//			if (isStatic)
			//			{
			//				print(", \"");
			//				Signature signature2= getSignatureOfInvocation(invocation);
			//				print(normalizeExpression(signature2));
			//				print("\")");
			//			}
			print(".");
			generateArguments(invocation);
		}
		else
		{
			boolean ready= false;
			if (expression instanceof CastExpression)
			{
				CastExpression castExpression= (CastExpression) expression;

				if (castExpression.getExpression() instanceof MethodInvocation)
				{
					MethodInvocation methodInvocation= (MethodInvocation) castExpression.getExpression();
					MethodBinding methodBinding2= methodInvocation.getMethodBinding();

					if (methodBinding2.toString().startsWith("com.dragome.commons.javascript.ScriptHelper#putMethodReference"))
					{
						Signature signature= getSignatureOfInvocation(invocation);
						String normalizeExpression= normalizeExpression(signature);
						String varName= ((StringLiteral) methodInvocation.getArguments().get(0)).getValue();
						Object classContainer= methodInvocation.getArguments().get(1);

						String ownerClass= "";
						if (classContainer instanceof ClassLiteral)
						{
							ownerClass= "\"" + ((ClassLiteral) classContainer).getSignature().toString() + "\"";
						}
						else if (classContainer instanceof VariableBinding)
						{
							VariableBinding variableBinding= (VariableBinding) classContainer;
							ownerClass= variableBinding.getName() + ".$getName$java_lang_String()";

						}
						print("var " + varName + "= dragomeJs.resolveMethod (" + ownerClass + ", \"" + normalizeExpression + "\")");
						ready= true;
					}
				}
			}

			if (!ready)
			{
				expression.visit(this);
				print(".");
				generateArguments(invocation);
			}
		}

	}

	public void visit(ClassInstanceCreation cic)
	{
		//	print(prefix);
		//	print(NEWINSTANCE);
		//	print("(");
		print("new ");
		Object className= Project.getSingleton().getSignature(((ObjectType) cic.getTypeBinding()).getClassName());
		print(normalizeExpression(className));
		//	print(Project.getSingleton().getSignature(((ObjectType) cic.getTypeBinding()).getClassName()).getCommentedId());

		print("(");
		if (cic.getMethodBinding() != null)
		{
			// We never get here!
			print(", ");
			generateArguments(cic);
		}

		print(")");
	}

	public void visit(ArrayInitializer ai)
	{
		print("[");
		for (int i= 0; i < ai.getExpressions().size(); i++)
		{
			print(i == 0 ? "" : ", ");
			((ASTNode) ai.getExpressions().get(i)).visit(this);
		}
		print("]");
	}

	public void visit(ArrayCreation ac)
	{
		if (ac.getDimensions().size() <= 0)
		{
			throw new RuntimeException("Expected array dimension > 0, but was" + ac.getDimensions().size());
		}

		if (ac.getInitializer() != null)
		{
			ac.getInitializer().visit(this);
		}
		else
		{
			print("dragomeJs.newArray('");
			Signature signature= Project.getSingleton().getArraySignature(ac.getTypeBinding());
			print(signature.toString());
			print("', [");
			for (int i= 0; i < ac.getDimensions().size(); i++)
			{
				print(i == 0 ? "" : ", ");
				ac.getDimensions().get(i).visit(this);
			}
			print("])");
		}
	}

	public void visit(ArrayAccess aa)
	{
		aa.getArray().visit(this);
		print("[");
		aa.getIndex().visit(this);
		print("]");
	}

	private String normalizeAccess(FieldAccess fr)
	{
		String prefix= "$$$";
		if (fr.getFirstChild() instanceof FieldRead)
		{
			FieldRead fieldRead= (FieldRead) fr.getFirstChild();
			//	    if (variableBinding.getTypeBinding() instanceof ArrayType)
			//		prefix="";
		}

		if (fr.getType() == null && "length".equals(fr.getName()))
			prefix= "";

		String name= prefix + fr.getName();
		String name2= name;

		if (!prefix.equals(""))
			name2+= FIELD_TYPE_SEPARATOR + normalizeExpression(fr.getFieldType());

		if (!fr.getName().matches("\\w*"))
		{
			// Name contains non-word characters, for example generated by
			// AspectJ.
			return "[\"" + name2 + "\"]";
		}
		return "." + name2;
	}

	public void visit(VariableDeclaration decl)
	{
		String name= escapeVariable(decl.getName());

		if (decl.getLocation() == VariableDeclaration.LOCAL_PARAMETER)
		{
			print(name);
			return;
		}

		if (decl.getLocation() == VariableDeclaration.NON_LOCAL)
		{
			FieldUnit fieldUnit= project.getOrCreateFieldUnit(typeDecl.getType(), name);

			if (Modifier.isStatic(decl.getModifiers()))
			{
				//		print(normalizeExpression(typeDecl.toString()));
				print(ClassUnit.STATIC_MEMBER);
			}
			else
			{
				//		print("this");
			}

			print("$$$");
			//	    print(normalizeAccess(name));
			print(name + FIELD_TYPE_SEPARATOR + normalizeExpression(decl.getType()));
			initializeField(decl, ":");
			fieldUnit.setData(reset());
			return;
		}
		else
		{
			if (decl.getLocation() != VariableDeclaration.LOCAL)
				throw new RuntimeException("Declaration must be local");

			print(name);
		}

		initializeField(decl, "=");
	}

	private void initializeField(VariableDeclaration decl, String assignmentOperator)
	{
		if (!decl.isInitialized())
			return;

		print(" " + assignmentOperator + " ");

		switch (decl.getType().getType())
		{
			case Constants.T_INT:
			case Constants.T_SHORT:
			case Constants.T_BYTE:
			case Constants.T_LONG:
			case Constants.T_DOUBLE:
			case Constants.T_FLOAT:
			case Constants.T_CHAR:
				print("0");
				break;
			case Constants.T_BOOLEAN:
				print("false");
				break;
			default:
				print("null");
				break;
		}
		//		addFieldTypeAsJsField(decl, assignmentOperator);
	}

	private void addFieldTypeAsJsField(VariableDeclaration decl, String assignmentOperator)
	{
		if (assignmentOperator.equals(":"))
		{
			print(", $T$" + decl.getName());
			print(" : ");
			print("\"");
			Type type= decl.getType();
			if (type instanceof ArrayType)
			{
				print(type.getSignature().replaceAll("/", "."));
			}
			else
				print(type.toString());

			print("\"");
		}
	}

	public void visit(VariableBinding reference)
	{
		// if (!reference.isField()) {
		// print("l");
		// }
		//		if (reference.getVariableDeclaration().getLocation() == VariableDeclaration.LOCAL_PARAMETER)

		print(escapeVariable(reference.getName()));
	}

	private boolean hasToEscapeVariable(String name)
	{
		return "function".equals(name) || "default".equals(name) || "var".equals(name) || "enum".equals(name) || "this".equals(name);
	}

	private String escapeVariable(String name)
	{
		if (hasToEscapeVariable(name))
			return "_" + name;
		else
			return name;
	}

	public void visit(ThisExpression reference)
	{
		consume(reference);
		print("this");
	}

	public void visit(FieldAccess fr)
	{
		ASTNode expression= fr.getExpression();
		if (expression == null)
		{
			// Static access.
			String normalizeExpression= normalizeExpression(Project.getSingleton().getSignature(fr.getType().getClassName()));
			boolean sameType= currentMethodDeclaration.getMethodBinding().getDeclaringClass().equals(fr.getType());
			if (true || "<clinit>".equals(currentMethodDeclaration.getMethodBinding().getName()) && !sameType)
			{
				String clinitExpression= normalizeExpression(new Signature("<clinit>()void", 0));
				print("" + normalizeExpression + "." + clinitExpression + "()");
			}
			else
				print(normalizeExpression);
		}
		else if (expression instanceof ThisExpression)
		{
			expression.visit(this);
		}
		else
		{
			//print(prefix + "cn(");
			expression.visit(this);
			//print(")");
		}

		print(normalizeAccess(fr));
	}

	public void visit(BreakStatement stmt)
	{
		print("break");
		if (stmt.getLabel() != null)
		{
			print(" " + stmt.getLabel());
		}
	}

	public void visit(ContinueStatement stmt)
	{
		print("continue");
		if (stmt.getLabel() != null)
		{
			print(" " + stmt.getLabel());
		}
	}

	public void visit(CastExpression cast)
	{
		if (cast.getTypeBinding() != Type.VOID && DragomeJsCompiler.compiler.compilerConfiguration.isCheckingCast())
		{
			print("dragomeJs.checkCast(");
			cast.getExpression().visit(this);
			String string= cast.getTypeBinding().toString();
			String normalizeExpression= normalizeExpression(string);
			if (string.startsWith("[L"))
				normalizeExpression= "'" + string + "'";
			print("," + normalizeExpression + ")");
		}
		else
		{
			// TODO: Is it correct to remove casts to void (i.e. pop)?
			// print("void ");
			cast.getExpression().visit(this);
		}
	}

	public void visit(BooleanLiteral be)
	{
		print(Boolean.toString(be.getValue()));
	}

	public void visit(ThrowStatement node)
	{
		print("throw dragomeJs.nullSaveException(");
		node.getExpression().visit(this);
		print(")");
	}

	public void visit(Name node)
	{
		if (false && node.getIdentifier().equals("javascript.Global"))
		{
			print("self");
		}
		else
		{
			print(node.getIdentifier());
		}
	}

	public void visit(PrimitiveCast node)
	{
		// TODO: Review cast to long.
		Type type= node.getTypeBinding();
		if (type.equals(Type.LONG))
		{
			print("dragomeJs.trunc(");
			node.getExpression().visit(this);
			print(")");
		}
		else if (type.equals(Type.INT))
		{
			print("dragomeJs.narrow(");
			node.getExpression().visit(this);
			print(", 0xffffffff)");
		}
		else if (type.equals(Type.SHORT))
		{
			print("dragomeJs.narrow(");
			node.getExpression().visit(this);
			print(", 0xffff)");
		}
		else if (type.equals(Type.BYTE))
		{
			print("dragomeJs.narrow(");
			node.getExpression().visit(this);
			print(", 0xff)");
		}
		else
			node.getExpression().visit(this);
	}

}
