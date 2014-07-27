package com.dragome.compiler.generators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaScriptCompressor
{

	private int CODE= 0;

	private int LINECOMMENT= 1;

	private int MULTILINECOMMENT= 2;

	private int STRING= 3;

	public String compress(InputStream input) throws FileNotFoundException, IOException
	{
		BufferedReader reader= new BufferedReader(new InputStreamReader(input));
		StringBuffer buffer= new StringBuffer();
		int state= CODE;
		char c;
		char lastC= 0;

		do
		{
			int i= reader.read();
			if (i == -1)
				break;
			c= (char) i;

			if (state == LINECOMMENT)
			{
				if (c == '\n')
				{
					state= CODE;
					buffer.append(c);
				}
			}
			else if (state == MULTILINECOMMENT)
			{
				if (c == '/' && lastC == '*')
				{
					state= CODE;
				}
			}
			else if (state == STRING)
			{
				if (c == '"')
				{
					state= CODE;
				}
				buffer.append(c);
			}
			else if (c == '/' && lastC == c)
			{
				state= LINECOMMENT;
				buffer.deleteCharAt(buffer.length() - 1);
			}
			else if (c == '*' && lastC == '/')
			{
				state= MULTILINECOMMENT;
				buffer.deleteCharAt(buffer.length() - 1);
			}
			else if (c == '"')
			{
				state= STRING;
				buffer.append(c);
			}
			else
			{
				int length= buffer.length();
				if (length > 0 && Character.isWhitespace(c) && Character.isWhitespace(buffer.charAt(length - 1)))
				{

				}
				else
				{
					buffer.append(c);
				}
			}
			lastC= c;
		}
		while (true);

		reader.close();

		return buffer.toString();
	}

}
