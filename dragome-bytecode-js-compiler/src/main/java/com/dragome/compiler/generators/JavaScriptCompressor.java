package com.dragome.compiler.generators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaScriptCompressor
{

	private final static int DEFAULT_CODE= 0;

	private final static int LINECOMMENT= 1;

	private final static int MULTILINECOMMENT= 2;

	private final static int STRING= 3;

	public String compress(InputStream input) throws FileNotFoundException, IOException
	{
		BufferedReader reader= new BufferedReader(new InputStreamReader(input));
		StringBuilder builder= new StringBuilder();

		int state= DEFAULT_CODE;
		char c;
		char lastC= 0;

		int i;

		while ((i= reader.read()) != -1)
		{
			c= (char) i;

			if (state == LINECOMMENT)
			{
				if (c == '\n')
				{
					state= DEFAULT_CODE;
					builder.append(c);
				}
			}
			else if (state == MULTILINECOMMENT)
			{
				if (c == '/' && lastC == '*')
				{
					state= DEFAULT_CODE;
				}
			}
			else if (state == STRING)
			{
				if (c == '"')
				{
					state= DEFAULT_CODE;
				}
				builder.append(c);
			}
			else if (c == '/' && lastC == c)
			{
				state= LINECOMMENT;
				builder.deleteCharAt(builder.length() - 1);
			}
			else if (c == '*' && lastC == '/')
			{
				state= MULTILINECOMMENT;
				builder.deleteCharAt(builder.length() - 1);
			}
			else if (c == '"')
			{
				state= STRING;
				builder.append(c);
			}
			else
			{
				int length= builder.length();
				if (length > 0 && Character.isWhitespace(c) && Character.isWhitespace(builder.charAt(length - 1)))
				{

				}
				else
				{
					builder.append(c);
				}
			}
			lastC= c;
		}

		reader.close();

		builder.trimToSize();
		return builder.toString();
	}

}
