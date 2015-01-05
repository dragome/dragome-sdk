/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.helpers;

import java.io.File;

public class Utils
{
	public static final int TEMP_DIR_ATTEMPTS= 10000;

	public static String convertDashedToCamel(String input)
	{
		StringBuilder result= new StringBuilder();
		for (int i= 0; i < input.length(); i++)
		{
			char charToAdd= input.charAt(i);
			if (charToAdd == '-')
				charToAdd= (input.charAt(++i) + "").toUpperCase().charAt(0);
			result.append(charToAdd);
		}
		return result.toString();
	}

	public static File createTempDir()
	{
		File baseDir= new File(System.getProperty("java.io.tmpdir"));
		String baseName= System.currentTimeMillis() + "-";

		for (int counter= 0; counter < TEMP_DIR_ATTEMPTS; counter++)
		{
			File tempDir= new File(baseDir, baseName + counter);
			if (tempDir.mkdir())
			{
				return tempDir;
			}
		}
		throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried " + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}

	public static File createTempDir(String name)
	{
		File baseDir= new File(System.getProperty("java.io.tmpdir"));

		File tempDir= new File(baseDir, name);
		if (tempDir.exists() || tempDir.mkdir())
			return tempDir;
		else
			throw new IllegalStateException("Failed to create directory within " + baseDir + ": " + name);

	}

}
