package com.dragome.web.enhancers.jsdelegate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;

public class InstrumentedClassPersister
{
	Map<Class<?>, File> generatedFiles= new HashMap<Class<?>, File>();
	private File baseDir;

	public InstrumentedClassPersister(File baseDir)
	{
		this.baseDir= baseDir;
	}

	public void persistClass(Class<?> interface1, String classname)
	{
		try
		{
			CtClass ctClass= ClassPool.getDefault().get(classname);
			File file= generatedFiles.get(interface1);
			ctClass.writeFile(getBaseDir().getAbsolutePath());
			file= new File(getBaseDir().getAbsolutePath() + File.separatorChar + classname.replace('.', File.separatorChar) + ".class");
			generatedFiles.put(interface1, file);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public File getBaseDir()
	{
		return baseDir;
	}
}
