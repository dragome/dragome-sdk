package com.dragome.compiler.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;

import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.utils.Log;

public class JunkWriter extends FilterWriter
{

	private File assembly;

	private int junkCount= 0;

	private int sizeOfCurrentJunk;

	private int sizeOfAllJunks= 0;

	private String fileName;

	public JunkWriter(File assembly) throws IOException
	{
		super(new StringWriter());
		this.assembly= assembly;
		startNewJunk();
	}

	private void startNewJunk() throws IOException
	{
		sizeOfAllJunks+= sizeOfCurrentJunk;

		if (junkCount > 0)
		{
			write("dragomeJs.loadScript(" + sizeOfAllJunks + ");");
			out.flush();
			out.close();
		}

		Log logger= Log.getLogger();
		fileName= "webapp.js";
		logger.debug("Creating assembly " + fileName);
		out= new FileWriter(new File(assembly, fileName));
		sizeOfCurrentJunk= 0;
		junkCount++;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException
	{
		super.write(cbuf, off, len);
		sizeOfCurrentJunk+= len;
	}

	@Override
	public void write(int c) throws IOException
	{
		super.write(c);
		sizeOfCurrentJunk++;
	}

	@Override
	public void write(String str, int off, int len) throws IOException
	{
		super.write(str, off, len);
		sizeOfCurrentJunk+= len;
	}

	@Override
	public void flush() throws IOException
	{
		super.flush();
		if (sizeOfCurrentJunk / 1024 > DragomeJsCompiler.compiler.getJunkSizeInKiloBytes())
		{
			startNewJunk();
		}
	}

	@Override
	public void close() throws IOException
	{
		sizeOfAllJunks+= sizeOfCurrentJunk;
		// Set to 0 in case super.close() calls flush().
		sizeOfCurrentJunk= 0;
		super.close();

		File file= new File(assembly, fileName);
		File outFile= new File(assembly, fileName.replace(".js", "-1.js"));
		DeflateEncoder.encode(file, outFile);
	}

	public int getSize()
	{
		return sizeOfAllJunks;
	}

}
