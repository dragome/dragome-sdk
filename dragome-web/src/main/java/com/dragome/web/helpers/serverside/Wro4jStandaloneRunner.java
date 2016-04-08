package com.dragome.web.helpers.serverside;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dragome.web.serverside.servlets.DragomeWroModelFactory;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.http.support.DelegatingServletOutputStream;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.manager.factory.standalone.StandaloneContext;
import ro.isdc.wro.manager.factory.standalone.StandaloneContextAware;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.WroModelInspector;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.util.io.UnclosableBufferedInputStream;

public class Wro4jStandaloneRunner
{
	private static final Logger LOG= LoggerFactory.getLogger(Wro4jStandaloneRunner.class);
	private static String userDirectory= System.getProperty("user.dir");
	private final File defaultWroFile= newDefaultWroFile();

	private Properties wroConfigurationAsProperties;
	private String targetGroups;
	private File contextFolder= new File(".");
	private File destinationFolder= new File(".");
	private boolean parallelPreprocessing;
	private ProcessorsFactory processorsFactory;
	private boolean minimize;
	private WroModelFactory wroModelFactory;
	private File wroFile;
	private WroConfiguration config;
	private WroManagerFactory managerFactory;
	private boolean ignoreMissingResources;

	public Wro4jStandaloneRunner(WroConfiguration config, WroManagerFactory managerFactory, File destinationDirectory)
	{
		super();
		this.config= config;
		this.managerFactory= managerFactory;
		this.destinationFolder= destinationDirectory;
	}

	protected File newDefaultWroFile()
	{
		return new File(userDirectory, "wro.xml");
	}

	protected File newWroConfigurationFile()
	{
		return new File(userDirectory, "wro.properties");
	}

	protected File getContextFolder()
	{
		return contextFolder;
	}

	protected File getDestinationFolder()
	{
		return contextFolder;
	}

	protected void onRunnerException(final Exception e)
	{
		System.out.println(e.getMessage());
		System.exit(1); // non-zero exit code indicates there was an error
	}

	public void process()
	{
		try
		{
			Context.set(Context.standaloneContext());
			// create destinationFolder if needed
			if (!destinationFolder.exists())
			{
				destinationFolder.mkdirs();
			}
			final Collection<String> groupsAsList= getTargetGroupsAsList();
			for (final String group : groupsAsList)
			{
				for (final ResourceType resourceType : ResourceType.values())
				{
					final String groupWithExtension= group + "." + resourceType.name().toLowerCase();
					processGroup(groupWithExtension, destinationFolder);
				}
			}
		}
		catch (final IOException e)
		{
			System.err.println(e.getMessage());
		}
	}

	/**
	 * @return a list containing all groups needs to be processed.
	 */
	private List<String> getTargetGroupsAsList() throws IOException
	{
		if (targetGroups == null)
		{
			final WroModel model= managerFactory.create().getModelFactory().create();
			return new WroModelInspector(model).getGroupNames();
		}
		return Arrays.asList(targetGroups.split(","));
	}

	/**
	 * Process a single group.
	 *
	 * @throws IOException
	 *           if any IO related exception occurs.
	 */
	private void processGroup(final String group, final File parentFoder) throws IOException
	{
		final ByteArrayOutputStream resultOutputStream= new ByteArrayOutputStream();
		InputStream resultInputStream= null;
		try
		{
			LOG.info("processing group: " + group);
			initContext(group, resultOutputStream);
			doProcess();

			// encode version & write result to file
			resultInputStream= new UnclosableBufferedInputStream(resultOutputStream.toByteArray());
			final File destinationFile= new File(parentFoder, rename(group, resultInputStream));
			destinationFile.createNewFile();
			// allow the same stream to be read again
			resultInputStream.reset();
			LOG.debug("Created file: {}", destinationFile.getName());

			final OutputStream fos= new FileOutputStream(destinationFile);
			// use reader to detect encoding
			IOUtils.copy(resultInputStream, fos);
			fos.close();
			// delete empty files
			if (destinationFile.length() == 0)
			{
				LOG.debug("No content found for group: {}", group);
				destinationFile.delete();
			}
			else
			{
				LOG.info("file size: {} -> {}bytes", destinationFile.getName(), destinationFile.length());
				LOG.info("{} ({}bytes) has been created!", destinationFile.getAbsolutePath(), destinationFile.length());
			}
		}
		finally
		{
			if (resultOutputStream != null)
			{
				resultOutputStream.close();
			}
			if (resultInputStream != null)
			{
				resultInputStream.close();
			}
		}
	}

	/**
	 * Initialize the context for standalone execution.
	 */
	private void initContext(final String group, final ByteArrayOutputStream resultOutputStream) throws IOException
	{
		final HttpServletRequest request= (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { HttpServletRequest.class }, new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				if (method.getName().equals("getRequestURI"))
					return group;
				return null;
			}
		});

		final HttpServletResponse response= (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { HttpServletResponse.class }, new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				if (method.getName().equals("getOutputStream"))
					return new DelegatingServletOutputStream(resultOutputStream);
				return null;
			}
		});

		final FilterConfig filterConfig= (FilterConfig) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { FilterConfig.class }, new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				return null;
			}
		});

		Context.set(Context.webContext(request, response, filterConfig), initWroConfiguration());
		Context.get().setAggregatedFolderPath(computeAggregatedFolderPath());
	}

	/**
	 * Perform actual processing by delegating the process call to {@link WroManager}.
	 *
	 * @throws IOException
	 * @VisibleForTesting
	 */
	void doProcess() throws IOException
	{
		// perform processing
		getManagerFactory().create().process();
	}

	private WroConfiguration initWroConfiguration() throws IOException
	{
		return config;
	}

	/**
	 * This implementation is similar to the one from Wro4jMojo. TODO: reuse if possible.
	 */
	private String computeAggregatedFolderPath()
	{
		Validate.notNull(destinationFolder, "DestinationFolder cannot be null!");
		Validate.notNull(getContextFolder(), "ContextFolder cannot be null!");
		final File cssTargetFolder= destinationFolder;
		File rootFolder= null;
		if (cssTargetFolder.getPath().startsWith(getContextFolder().getPath()))
		{
			rootFolder= getContextFolder();
		}
		// compute aggregatedFolderPath
		String aggregatedFolderPath= null;
		if (rootFolder != null)
		{
			aggregatedFolderPath= StringUtils.removeStart(cssTargetFolder.getPath(), rootFolder.getPath());
		}
		LOG.debug("aggregatedFolderPath: {}", aggregatedFolderPath);
		return aggregatedFolderPath;
	}

	/**
	 * Encodes a version using some logic.
	 *
	 * @param group
	 *          the name of the resource to encode.
	 * @param input
	 *          the stream of the result content.
	 * @return the name of the resource with the version encoded.
	 */
	private String rename(final String group, final InputStream input) throws IOException
	{
		return getManagerFactory().create().getNamingStrategy().rename(group, input);
	}

	/**
	 * This method will ensure that you have a right and initialized instance of {@link StandaloneContextAware}.
	 */
	private WroManagerFactory getManagerFactory() throws IOException
	{
		return managerFactory;
	}

	/**
	 * Creates a {@link StandaloneContext} by setting properties passed after mojo is initialized.
	 */
	private StandaloneContext createStandaloneContext()
	{
		final StandaloneContext runContext= new StandaloneContext();
		runContext.setContextFoldersAsCSV(getContextFolder().getPath());
		runContext.setMinimize(minimize);
		runContext.setWroFile(wroFile);
		runContext.setIgnoreMissingResourcesAsString(Boolean.toString(ignoreMissingResources));
		return runContext;
	}

	/**
	 * @param destinationFolder
	 *          the destinationFolder to set
	 * @VisibleForTestOnly
	 */
	void setDestinationFolder(final File destinationFolder)
	{
		this.destinationFolder= destinationFolder;
	}
}