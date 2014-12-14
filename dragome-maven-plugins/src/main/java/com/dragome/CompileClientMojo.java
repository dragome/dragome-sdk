package com.dragome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.dragome.helpers.serverside.DragomeCompilerLauncher;
import com.dragome.services.ServiceLocator;

@Mojo(name = "compileclient"
)
public class CompileClientMojo extends AbstractMojo {

    @Parameter
    private File destinationDirectory;

    @Parameter
    private boolean removeCache = true;

    @Parameter
    private boolean forceRebuild = true;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    private void copyResource(String aResourceName, String aLocation) {
        getLog().info("Copy " + aResourceName+" to " + aLocation);
        InputStream theInputStream = getClass().getResourceAsStream(aResourceName);
        if (theInputStream != null) {
            int theLastPathIndex = aLocation.lastIndexOf('/');
            if (theLastPathIndex > 0) {
                String thePath = aLocation.substring(0, theLastPathIndex);
                thePath = thePath.replace('/', IOUtils.DIR_SEPARATOR);
                File theTargetDir = new File(destinationDirectory, thePath);
                if (!theTargetDir.exists()) {
                    if (!theTargetDir.mkdirs()) {
                        throw new RuntimeException("Cannot create directory " +theTargetDir);
                    }
                }
            }
            String theSystemLocation = aLocation.replace('/', IOUtils.DIR_SEPARATOR);
            File theDestinatioFile = new File(destinationDirectory, theSystemLocation);
            try (FileOutputStream theOutputStream = new FileOutputStream(theDestinatioFile)) {
                IOUtils.copy(theInputStream, theOutputStream);
            } catch (Exception e) {
                throw new RuntimeException("Cannot write data to " + theDestinatioFile, e);
            }
        } else {
            throw new IllegalArgumentException("Cannot find ressource " + aResourceName+" in ClassPath");
        }
    }

    private void compile() throws URISyntaxException, DependencyResolutionRequiredException, MalformedURLException {

    	System.setProperty("dragome-compile-mode", "release");

        // Fire the compiler
        final StringBuilder theClassPathForCompiler= new StringBuilder();

        URLClassLoader theCurrentClassLoader= (URLClassLoader) getClass().getClassLoader();
        URL[] theConfiguredURLs= theCurrentClassLoader.getURLs();
        for (URL theURL : theConfiguredURLs)
        {
            getLog().info("Found classpath element " + theURL);
            String theClassPathEntry= new File(theURL.toURI()).toString();
            boolean isClassesFolder= theURL.toString().endsWith("/classes/") || theURL.toString().endsWith("/classes");
            boolean addToClasspath = ServiceLocator.getInstance().getConfigurator().filterClassPath(theClassPathEntry);
            if (isClassesFolder || addToClasspath) {
                theClassPathForCompiler.append(theClassPathEntry + ";");
            } else {
                getLog().warn("Skipping, it is not configured as an included artifact.");
            }
        }

        List<String> theClassPathElements = (List<String>) project.getTestClasspathElements();
        for (String theSingleElement : theClassPathElements) {
            URL theURL = new File(theSingleElement).toURI().toURL();
            boolean isClassesFolder= theURL.toString().endsWith("/classes/") || theURL.toString().endsWith("/classes");
            if (isClassesFolder) {
                getLog().info("Found classpath element " + theSingleElement);
                theClassPathForCompiler.append(theSingleElement + ";");
            }
        }

        File theTargetDir = new File(destinationDirectory, "compiled-js");
        if (!theTargetDir.exists() && !theTargetDir.mkdirs()) {
            throw new RuntimeException("Cannot create directory " + theTargetDir);
        }

        File theWebAppJS = new File(theTargetDir, "webapp.js");
        if (forceRebuild && theWebAppJS.exists()) {
            if (!theWebAppJS.delete()) {
                throw new RuntimeException("Cannot delete file " + theWebAppJS);
            }
        }

        // Store the dragome cache file here
        System.setProperty("cache-dir", theTargetDir.toString());

        getLog().info("Using Dragome compiler classpath : " + theClassPathForCompiler.toString());

        DragomeCompilerLauncher.compileWithMainClass(theClassPathForCompiler.toString(), theTargetDir.toString());

        // Finally remove the cache file
        if (removeCache) {
            File theCacheFile = new File(theTargetDir, "dragome.cache");
            if (!theCacheFile.delete()) {
                throw new RuntimeException("Cannot delete cache file" + theCacheFile);
            }
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Generating Dragome Client Application at " + destinationDirectory);

        // Copy Resources
        copyResource("/dragome-debug.js", "dragome-resources/dragome-debug.js");
        copyResource("/js/jquery.js", "dragome-resources/js/jquery.js");
        copyResource("/css/dragome.css", "dragome-resources/css/dragome.css");
        copyResource("/js/hashtable.js", "dragome-resources/js/hashtable.js");
        copyResource("/js/deflate.js", "dragome-resources/js/deflate.js");
        copyResource("/js/console.js", "dragome-resources/js/console.js");
        copyResource("/js/helpers.js", "dragome-resources/js/helpers.js");
        copyResource("/js/String.js", "dragome-resources/js/String.js");
        copyResource("/js/jquery.atmosphere.js", "dragome-resources/js/jquery.atmosphere.js");
        copyResource("/js/application.js", "dragome-resources/js/application.js");
        copyResource("/js/q-3.0.js", "dragome-resources/js/q-3.0.js");

        try {
            compile();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}