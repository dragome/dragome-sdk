package com.dragome.compiler.ast;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.generic.Type;

import com.dragome.compiler.Project;
import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.parser.LineNumberCursor;

public class MethodDeclaration extends ASTNode
{

	private String tempPrefix= "_";

	private Block block;

	private Map<String, VariableDeclaration> parameters= new LinkedHashMap<String, VariableDeclaration>();

	private Map<String, VariableDeclaration> localVariables= new LinkedHashMap<String, VariableDeclaration>();

	private int accessFlags;

	private Code code;

	private MethodBinding methodBinding;

	private LineNumberCursor lineNumberCursor;

	private Map<String, String> annotationsValues;

	public Map<String, String> getAnnotationsValues()
	{
		return annotationsValues;
	}

	public void setAnnotationsValues(Map<String, String> annotationsValues)
	{
		this.annotationsValues= annotationsValues;
	}

	public MethodDeclaration(MethodBinding theMethodBinding, int theAccessFlags, Code theCode, Map<String, String> annotationsValues)
	{
		methodBinding= theMethodBinding;
		accessFlags= theAccessFlags;
		code= theCode;
		this.annotationsValues= annotationsValues;
		lineNumberCursor= new LineNumberCursor(code);
		Project.getSingleton().getOrCreateProcedureUnit(methodBinding);
	}

	public int getAccess()
	{
		return accessFlags;
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public boolean isInstanceConstructor()
	{
		return methodBinding.getName().equals("<init>");
	}

	public Block getBody()
	{
		return block;
	}

	public void setBody(Block theBlock)
	{
		block= theBlock;
		theBlock.setParentNode(this);
	}

	public VariableBinding createVariableBinding(String name, Type type, boolean isWrite)
	{
		if (type == null)
			throw new NullPointerException();

		VariableDeclaration decl= getParameter(name);
		if (decl == null)
		{
			decl= getLocalVariable(name);
		}
		if (decl == null)
		{
			decl= new VariableDeclaration(!isWrite);
			decl.setName(name);
			decl.setType(type);
			addLocalVariable(decl);
		}

		VariableBinding binding= new VariableBinding(decl);

		return binding;
	}

	private int vbCount= 0;

	public VariableBinding createAnonymousVariableBinding(Type type, boolean isWrite)
	{
		String name= tempPrefix + (vbCount++);
		VariableBinding vb= createVariableBinding(name, type, isWrite);
		vb.setTemporary(true);
		return vb;
	}

	public void addParameter(VariableDeclaration variableDecl)
	{
		parameters.put(variableDecl.getName(), variableDecl);
	}

	public Collection<VariableDeclaration> getParameters()
	{
		return parameters.values();
	}

	public VariableDeclaration getParameter(String name)
	{
		return parameters.get(name);
	}

	public void addLocalVariable(VariableDeclaration variableDecl)
	{
		localVariables.put(variableDecl.getName(), variableDecl);
	}

	public void removeLocalVariable(String name)
	{
		localVariables.remove(name);
	}

	public Collection<VariableDeclaration> getLocalVariables()
	{
		return localVariables.values();
	}

	public VariableDeclaration getLocalVariable(String name)
	{
		return localVariables.get(name);
	}

	public MethodBinding getMethodBinding()
	{
		return methodBinding;
	}

	public String toString()
	{
		return methodBinding.toString();
	}

	public Code getCode()
	{
		return code;
	}

	public LineNumberCursor getLineNumberCursor()
	{
		return lineNumberCursor;
	}
}
