/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package dragome.javascript;

import java.io.OutputStream;

public final class ConsoleOutputStream extends OutputStream
{
    public ConsoleOutputStream()
    {
    }
    
    public void write(int b)
    {
	write(new byte[] { (byte) b });
    }

    public void write(byte[] bytes)
    {
	write(new String(bytes));
    }

    public void write(String s)
    {
	ScriptHelper.put("s", s, this);
	ScriptHelper.eval("dragomeJs.print(s)", this);
    }
}
