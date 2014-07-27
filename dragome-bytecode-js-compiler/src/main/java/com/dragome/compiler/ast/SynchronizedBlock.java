/*
 * Copyright 2005 by Wolfgang Kuehn
 * Created on 18.10.2005
 */
package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class SynchronizedBlock extends Block
{
	public Expression monitor;

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}
}
