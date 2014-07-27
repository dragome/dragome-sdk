/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.callbackevictor.serverside.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;

import com.dragome.callbackevictor.serverside.bytecode.transformation.ResourceTransformer;
import com.dragome.callbackevictor.serverside.bytecode.transformation.asm.AsmClassTransformer;
import com.dragome.callbackevictor.serverside.utils.RewritingUtils;

/**
 * Ant task that enhances class files with javaflow instrumentation.
 */
public class AntRewriteTask extends MatchingTask {

    private ResourceTransformer transformer;

    private File dstDir;
    private File srcDir;

    /**
     * Directory to which the transformed files will be written.
     * This can be the same as the source directory.
     */
    public void setDestdir(final File pFile) {
        dstDir = pFile;
    }

    /**
     * Directory from which the input files are read.
     * This and the inherited {@link MatchingTask} forms an implicit
     * {@link FileSet}.
     */
    public void setSrcDir(final File pFile) {
        srcDir = pFile;
        fileset.setDir(srcDir);
    }

    /**
     * Sets the transformer to use.
     *
     * <p>
     * This option is unpublished, because in a long run we'll
     * likely to just focus on one transformer and get rid
     * of the other (and this option will be removed then.)
     *
     * @param name
     *      "ASM". Case insensitive.
     */
    public void setMode(String name) {
        if(name.equalsIgnoreCase("asm")) {
            transformer = new AsmClassTransformer();
        } else {
            throw new BuildException("Unrecognized mode: "+name);
        }
    }
    
    /**
     * Check that all required attributes have been set and nothing
     * silly has been entered.
     *
     * @since Ant 1.5
     */
    protected void checkParameters() throws BuildException {
        checkDir(srcDir,"srcDir");
        checkDir(dstDir,"dstDir");
    }

    private void checkDir(final File pDir, final String pDescription) {
        if (pDir == null) {
            throw new BuildException("no " + pDescription + " directory is specified", getLocation());
        }
        if (!pDir.exists()) {
            throw new BuildException(pDescription + " directory \"" + pDir + "\" does not exist", getLocation());
        }
        if (!pDir.isDirectory()) {
            throw new BuildException(pDescription + " directory \"" + pDir + "\" is not a directory", getLocation());
        }
    }

    public void execute() throws BuildException {
        
        final DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
        final String[] fileNames = ds.getIncludedFiles();

        if(transformer == null) {
            transformer = new AsmClassTransformer();
        }

        try {
            for (int i = 0; i < fileNames.length; i++) {
                final String fileName = fileNames[i];
                
                final File source = new File(srcDir, fileName);
                final File destination = new File(dstDir, fileName);
                
                if (!destination.getParentFile().exists()) {
                    log("Creating dir: " + destination.getParentFile(), Project.MSG_VERBOSE);
                    destination.getParentFile().mkdirs();
                }

                if (source.lastModified() < destination.lastModified()) {
                    log("Omitting " + source + " as " + destination + " is up to date", Project.MSG_VERBOSE);
                    continue;
                }
                
                if (fileName.endsWith(".class")) {
                    log("Rewriting " + source + " to " + destination, Project.MSG_VERBOSE);
                    // System.out.println("Rewriting " + source);

                    RewritingUtils.rewriteClassFile( source, transformer, destination );
                }

                if (fileName.endsWith(".jar")
                    || fileName.endsWith(".ear")
                    || fileName.endsWith(".zip")
                    || fileName.endsWith(".war")) {

                    log("Rewriting " + source + " to " + destination, Project.MSG_VERBOSE);

                    RewritingUtils.rewriteJar(
                            new JarInputStream(new FileInputStream(source)),
                            transformer,
                            new JarOutputStream(new FileOutputStream(destination))
                            );
                    
                }
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
