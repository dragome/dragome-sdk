package com.dragome.compiler.type;

import java.io.File;

import org.xmlvm.ClassToJs;

import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.Project;
import com.dragome.compiler.ast.TypeDeclaration;
import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.parser.Parser;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.utils.Log;

public class TypeResolver implements TypeVisitor
{

	private AbstractVisitor generator;

	private Project project;

	public TypeResolver(Project theProject, AbstractVisitor theGenerator)
	{
		project= theProject;
		generator= theGenerator;
	}

	public void visit(ClassUnit clazz)
	{
		if (clazz.isResolved())
			return;

		Log logger= Log.getLogger();

		if (clazz.getSignature().toString().startsWith("["))
		{

		}
		else if (!clazz.isUpToDate())
		{
			clazz.clear();
			try
			{
				compile(clazz);
				DragomeJsCompiler.compiler.compileCount++;
			}
			catch (RuntimeException ex)
			{
				DragomeJsCompiler.errorCount++;
				logger.error(ex.toString());

				if (DragomeJsCompiler.compiler.failOnError)
				{
					throw ex;
				}
			}
		}
		else
		{
			logger.debug("Up to date: " + clazz);
		}

		clazz.setResolved(true);
	}

	private void compile(ClassUnit classUnit)
	{
		classUnit.getNotReversibleMethods().clear();

		if (classUnit.getClassFile() == null)
		{
			Log.getLogger().warn("Cannot read " + classUnit.getClassFile());
			return;
		}

		Log.getLogger().debug("Cross-Compiling " + classUnit);

		Log.getLogger().infoSameLine(".");

		TypeDeclaration typeDecl= null;
		try
		{
			typeDecl= parse(classUnit);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			//classUnit.setReversible(false);
		}

		{
			typeDecl.visit(generator);

			if (!classUnit.getNotReversibleMethods().isEmpty())
			{
				File file= classUnit.getClassFile().getFile();
				classUnit.setAlternativeCompilation(ClassToJs.transformClassFileToJs(file != null ? file.getName() : "", classUnit.getBytecode()));
				project.incrementBadMethods(classUnit.getNotReversibleMethods().size());
			}

			classUnit.setLastCompiled(classUnit.getLastModified());
		}
	}

	private TypeDeclaration parse(ClassUnit classUnit)
	{
		Parser parser= new Parser(classUnit);
		TypeDeclaration typeDecl= parser.parse();

		return typeDecl;
	}
}
