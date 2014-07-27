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
/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * A stand-alone application that takes the overview.xml file from the tutorials
 * folder and generates a website from it.
 * <p>
 * Example Usage: <code>
 * <i>application</i>
 *   --overview=bar/overview.xml
 *   --template=bar/template.html
 *   --output=destination/foo
 * </code>
 */
public class TutorialWebGenerator
{

	private static class Params
	{
		public final String overviewPath;
		public final String outputPath;
		public final String templatePath;
		private static final String PARAM_OVERVIEW_XML= "--overview=";
		private static final String PARAM_OUTPUT= "--output=";
		private static final String PARAM_TEMPLATE= "--template=";

		public Params(String overviewPath, String outputPath, String template)
		{
			this.overviewPath= overviewPath;
			this.outputPath= outputPath;
			this.templatePath= template;
		}

		public static Params parse(String[] args)
		{
			String overviewPath= "";
			String outputPath= "";
			String templatePath= "";
			for (String arg : args)
			{
				if (arg.startsWith(PARAM_OVERVIEW_XML))
				{
					overviewPath= arg.substring(PARAM_OVERVIEW_XML.length());
				}
				if (arg.startsWith(PARAM_OUTPUT))
				{
					outputPath= arg.substring(PARAM_OUTPUT.length());
				}
				if (arg.startsWith(PARAM_TEMPLATE))
				{
					templatePath= arg.substring(PARAM_TEMPLATE.length());
				}
			}
			return new Params(overviewPath, outputPath, templatePath);
		}
	}

	private static final String OUTPUT_FILENAME= "index.html";

	public static void main(String[] args)
	{
		Params params= Params.parse(args);
		String template= readFromFile(params.templatePath);
		String html= generateHtml(readOverviewXml(params.overviewPath), template, params.outputPath);
		writeHtmlToDisk(html, params.outputPath);
		System.out.println("All done.");
	}

	private static String generateHtml(Document overview, String template, String outputPath)
	{
		@SuppressWarnings("unchecked")
		List<Element> iosApplications= overview.getRootElement().getChild("ios-tutorials").getChild("tutorials").getChildren("application");
		List<Element> androidApplications= overview.getRootElement().getChild("android-tutorials").getChild("tutorials").getChildren("application");

		StringBuilder tutorialEntries= new StringBuilder();
		StringBuilder generatedCode= new StringBuilder();
		tutorialEntries.append("<div class=\"ios\">");
		tutorialEntries.append("<div class=\"platformTitle\">iOS</div>");
		int i= 0;
		for (Element iosApplication : iosApplications)
		{
			tutorialEntries.append(generateTutorialEntry(++i, "ios", iosApplication));
			generatedCode.append(generateCodeFiles("ios", iosApplication, outputPath));
		}
		tutorialEntries.append("</div>");

		tutorialEntries.append("<div class=\"android\">");
		tutorialEntries.append("<div class=\"platformTitle\">Android</div>");
		i= 0;
		for (Element androidApplication : androidApplications)
		{
			tutorialEntries.append(generateTutorialEntry(++i, "android", androidApplication));
			generatedCode.append(generateCodeFiles("android", androidApplication, outputPath));
		}
		tutorialEntries.append("</div>");

		template= template.replace("{{TUTORIAL_LIST}}", tutorialEntries);
		template= template.replace("{{GENERATED_CODE}}", generatedCode.toString());
		return template;
	}

	private static String generateTutorialEntry(int index, String platform, Element application)
	{
		String title= application.getAttributeValue("name");
		String id= md5(platform + title);
		String slidesUrl= application.getAttributeValue("slides");
		String description= application.getChildText("text");

		StringBuilder html= new StringBuilder();
		html.append("<script>slidesUrl['" + id + "'] = '" + slidesUrl + "';</script>");
		html.append("<div id=\"" + id + "\" class=\"tutorialEntry\" onclick=\"javascript:switchToTutorial('" + id + "');\">");
		html.append("<div class=\"tutorialTitle\">");
		html.append(index + ". " + title);
		html.append("</div>");
		html.append("<div class=\"description\">");
		html.append(description);
		html.append("</div></div>");
		return html.toString();
	}

	/**
	 * Returns HTML/JS that needs to be included in the page somewhere.
	 */
	private static String generateCodeFiles(String platform, Element application, String outputPath)
	{
		outputPath= outputPath + "/code";
		File output= new File(outputPath);
		System.out.println("Output Path: " + output.getAbsolutePath());
		if (!output.exists())
		{
			if (!output.mkdirs())
			{
				System.err.println("Could not create output directory");
				return "";
			}
		}

		String title= application.getAttributeValue("name");
		String id= md5(platform + title);
		StringBuilder htmlJs= new StringBuilder();
		htmlJs.append("<script>\n");
		htmlJs.append("codeFiles['" + id + "'] = [");
		Element filesElement= application.getChild("files");
		File basePath= new File(filesElement.getAttributeValue("base"));

		@SuppressWarnings("unchecked")
		List<Element> fileElements= filesElement.getChildren("file");
		for (Element fileElement : fileElements)
		{
			String name= fileElement.getAttributeValue("name");
			File file= new File(basePath + "/" + name);
			if (file.exists() && file.isFile())
			{
				String fileName= id + "-" + name.replace('/', '-') + ".html";
				String pathName= output.getAbsolutePath() + "/" + fileName;
				System.out.println("FileName: " + pathName);
				String sourceCode= readFromFile(file.getAbsolutePath());
				String brush= "java";
				String displayName= name;
				if (name.endsWith(".xml"))
				{
					brush= "xml";
				}
				if (name.endsWith(".java"))
				{
					displayName= name.substring(name.lastIndexOf('/') + 1);
				}
				String fileContent= createCodeFile(sourceCode, brush);

				// Hack, because our syntax highlighter has a problem otherwise:
				fileContent= fileContent.replace("/* Copyright", "/** Copyright");
				fileContent= fileContent.replace("/*\n", "/**\n");
				writeToDisk(fileContent, pathName);
				htmlJs.append("{'loc':'code/" + fileName + "', 'label':'" + displayName + "'},");
			}
			else
			{
				System.err.println("File does not exist or is not a file: " + file.getAbsolutePath());
			}
		}
		htmlJs.append("{}];\n");
		htmlJs.append("</script>\n");
		return htmlJs.toString();
	}

	private static String createCodeFile(String sourceCode, String brush)
	{
		StringBuilder fileContent= new StringBuilder();
		fileContent.append("<div class=\"listing\">" + "<script type=\"syntaxhighlighter\" class=\"brush: " + brush + "\"><![CDATA[\n");
		fileContent.append(sourceCode);
		fileContent.append("\n]]></script></div>");
		return fileContent.toString();
	}

	private static Document readOverviewXml(String path)
	{
		SAXBuilder builder= new SAXBuilder();
		File overviewFile= new File(path);
		if (!overviewFile.exists())
		{
			System.err.println("Overview file doesn't exist: " + overviewFile.getAbsolutePath());
			return null;
		}
		try
		{
			return builder.build(overviewFile);
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static void writeHtmlToDisk(String html, String pathStr)
	{
		if (pathStr == null || pathStr.equals(""))
		{
			pathStr= ".";
		}
		File path= new File(pathStr);
		if (path.exists() && path.isFile())
		{
			System.err.println("Output path is a file.");
			return;
		}

		if (!path.exists())
		{
			if (!path.mkdirs())
			{
				System.err.println("Couldn't create output folder.");
				return;
			}
		}

		File outputFile= new File(path.getAbsoluteFile() + File.separator + OUTPUT_FILENAME);
		try
		{
			System.out.println("Writing to: " + outputFile.getAbsolutePath());
			FileWriter writer= new FileWriter(outputFile);
			writer.write(html);
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void writeToDisk(String content, String filePath)
	{
		File outputFile= new File(filePath);
		try
		{
			System.out.println("Writing to: " + outputFile.getAbsolutePath());
			FileWriter writer= new FileWriter(outputFile);
			writer.write(content);
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static String readFromFile(String path)
	{
		UniversalFile file= UniversalFileCreator.createFile(new File(path));
		if (!file.exists())
		{
			System.err.println("File doesn't exist: " + path);
			return null;
		}
		return file.getFileAsString();
	}

	private static String md5(String text)
	{
		try
		{
			MessageDigest digest= MessageDigest.getInstance("MD5");
			digest.update(text.getBytes("UTF-8"));
			byte[] messageDigest= digest.digest();
			StringBuilder hexString= new StringBuilder();
			for (int i= 0; i < messageDigest.length; i++)
			{
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			return hexString.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
