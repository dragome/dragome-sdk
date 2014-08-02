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

package org.xmlvm.proc;

import java.util.List;

import org.xmlvm.proc.out.OutputFile;

/**
 * The common interface for all processes.
 */
public interface XmlvmProcess
{
	/**
	 * This is phase one of a two-staged process. In this phase, processes are
	 * supposed to add all {@link XmlvmResource}s
	 * 
	 * @return Whether the processing was successful.
	 */
	public boolean processPhase1(BundlePhase1 resources);

	/**
	 * This is phase two of a two-staged process. In this phase, processes are
	 * not allowed to add any {@link XmlvmResource}s but instead add all
	 * {@link OutputFile}s.
	 * 
	 * @return Whether the processing was successful.
	 */
	public boolean processPhase2(BundlePhase2 resources);

	/**
	 * Processors can override it to do post-processing.
	 */
	public boolean postProcess();

	/**
	 * Returns a list of supported input classes.
	 */
	public List<Class<XmlvmProcess>> getSupportedInputs();

	/**
	 * Creates an instance of each XvmlmProcess that is supported as input and
	 * returns them in a list.
	 */
	public List<XmlvmProcess> createInputInstances();

	/**
	 * Determines whether the output of the given XmlvmProcess can be processed
	 * by this XmlvmProcess.
	 * 
	 * @param process
	 *            The process that should be used as the input.
	 * @return Whether this process can handle the given process as an input.
	 */
	public boolean supportsAsInput(XmlvmProcess process);

	/**
	 * Adds a process as a pre-process to this process.
	 */
	public void addPreprocess(XmlvmProcess xmlvmProcess);

	/**
	 * Adds a process to the list of processes that get executed directly after
	 * this process.
	 * 
	 * @param xmlvmProcess
	 *            A process that is executed directly after this process.
	 */
	public void addPostProcess(XmlvmProcess xmlvmProcess);

	/**
	 * Returns whether this process is active. An active process will be
	 * processed. To be active a process needs to have at least one active
	 * pre-process. Exception are InputProcesses, which are active by default.
	 * Once the processed is processed, the process is not active anymore.
	 */
	public boolean isActive();

	/**
	 * Returns whether this process has a cached version for the specified input
	 * resource.
	 * 
	 * @param name
	 *            The name of the input resource.
	 * @param lastModified
	 *            The timestamp from when the input resource was change the last
	 *            time.
	 * @return Whether a cached output exists.
	 */
	public boolean hasCachedOutput(String inputResourceName, long lastModified);

	/**
	 * Returns whether processing is required. It's not, if the given resource
	 * is cached somewhere down the line in a post-process.
	 * 
	 * @param inputResourceName
	 *            The name of the input resource.
	 * @param lastModified
	 *            The timestamp from when the input resource was change the last
	 *            time.
	 * @return Whether this process needs to process the given resource.
	 */
	public boolean isProcessingRequired(String inputResourceName, long lastModified);

	public boolean forwardOrProcessPhase1(BundlePhase1 resources);

	public boolean forwardOrProcessPhase2(BundlePhase2 resources);

	/**
	 * Runs {@link #postProcess()} on all preprocesses that have been processed.
	 */
	public boolean postProcessPreProcesses();

	/**
	 * Sets whether this process is the target process.
	 */
	public void setIsTargetProcess(boolean isTargetProcess);
}
