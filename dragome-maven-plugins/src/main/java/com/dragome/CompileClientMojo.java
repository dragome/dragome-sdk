package com.dragome;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.dragome.web.helpers.serverside.StandaloneDragomeAppGenerator;

@Mojo(name= "compileclient")
public class CompileClientMojo extends AbstractMojo
{
	@Parameter
	private File destinationDirectory;
	@Parameter
	private File webappDirectory;
	@Parameter
	private boolean removeCache= true;
	@Parameter
	private boolean forceRebuild= true;

	public void execute() throws MojoExecutionException, MojoFailureException
	{
		new StandaloneDragomeAppGenerator(destinationDirectory, webappDirectory, removeCache, forceRebuild).execute();
	}
}