/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

// Comment below before mustang integration
import java.util.Properties;

/**
 * <P>The basic service for managing a set of JDBC drivers.<br>
 * <B>NOTE:</B> The {@link <code>DataSource</code>} interface, new in the
 * JDBC 2.0 API, provides another way to connect to a data source.
 * The use of a <code>DataSource</code> object is the preferred means of
 * connecting to a data source.
 *
 * <P>As part of its initialization, the <code>DriverManager</code> class will
 * attempt to load the driver classes referenced in the "jdbc.drivers"
 * system property. This allows a user to customize the JDBC Drivers
 * used by their applications. For example in your
 * ~/.hotjava/properties file you might specify:
 * <pre>
 * <CODE>jdbc.drivers=foo.bah.Driver:wombat.sql.Driver:bad.taste.ourDriver</CODE>
 * </pre>
 *<P> The <code>DriverManager</code> methods <code>getConnection</code> and
 * <code>getDrivers</code> have been enhanced to support the Java Standard Edition
 * <a href="../../../technotes/guides/jar/jar.html#Service%20Provider">Service Provider</a> mechanism. JDBC 4.0 Drivers must
 * include the file <code>META-INF/services/java.sql.Driver</code>. This file contains the name of the JDBC drivers
 * implementation of <code>java.sql.Driver</code>.  For example, to load the <code>my.sql.Driver</code> class,
 * the <code>META-INF/services/java.sql.Driver</code> file would contain the entry:
 * <pre>
 * <code>my.sql.Driver</code>
 * </pre>
 *
 * <P>Applications no longer need to explictly load JDBC drivers using <code>Class.forName()</code>. Existing programs
 * which currently load JDBC drivers using <code>Class.forName()</code> will continue to work without
 * modification.
 *
 * <P>When the method <code>getConnection</code> is called,
 * the <code>DriverManager</code> will attempt to
 * locate a suitable driver from amongst those loaded at
 * initialization and those loaded explicitly using the same classloader
 * as the current applet or application.
 *
 * <P>
 * Starting with the Java 2 SDK, Standard Edition, version 1.3, a
 * logging stream can be set only if the proper
 * permission has been granted.  Normally this will be done with
 * the tool PolicyTool, which can be used to grant <code>permission
 * java.sql.SQLPermission "setLog"</code>.
 * @see Driver
 * @see Connection
 */
public class DriverManager
{

	public static java.io.PrintWriter getLogWriter()
	{
		return logWriter;
	}

	/**
	 * Sets the logging/tracing <code>PrintWriter</code> object
	 * that is used by the <code>DriverManager</code> and all drivers.
	 * <P>
	 * There is a minor versioning problem created by the introduction
	 * of the method <code>setLogWriter</code>.  The
	 * method <code>setLogWriter</code> cannot create a <code>PrintStream</code> object
	 * that will be returned by <code>getLogStream</code>---the Java platform does
	 * not provide a backward conversion.  As a result, a new application
	 * that uses <code>setLogWriter</code> and also uses a JDBC 1.0 driver that uses
	 * <code>getLogStream</code> will likely not see debugging information written
	 * by that driver.
	 *<P>
	 * Starting with the Java 2 SDK, Standard Edition, version 1.3 release, this method checks
	 * to see that there is an <code>SQLPermission</code> object before setting
	 * the logging stream.  If a <code>SecurityManager</code> exists and its
	 * <code>checkPermission</code> method denies setting the log writer, this
	 * method throws a <code>java.lang.SecurityException</code>.
	 *
	 * @param out the new logging/tracing <code>PrintStream</code> object;
	 *      <code>null</code> to disable logging and tracing
	 * @throws SecurityException
	 *    if a security manager exists and its
	 *    <code>checkPermission</code> method denies
	 *    setting the log writer
	 *
	 * @see SecurityManager#checkPermission
	 * @see #getLogWriter
	 * @since 1.2
	 */
	public static void setLogWriter(java.io.PrintWriter out)
	{
	}

	//---------------------------------------------------------------

	/**
	 * Attempts to establish a connection to the given database URL.
	 * The <code>DriverManager</code> attempts to select an appropriate driver from
	 * the set of registered JDBC drivers.
	 *
	 * @param url a database url of the form
	 * <code> jdbc:<em>subprotocol</em>:<em>subname</em></code>
	 * @param info a list of arbitrary string tag/value pairs as
	 * connection arguments; normally at least a "user" and
	 * "password" property should be included
	 * @return a Connection to the URL
	 * @exception SQLException if a database access error occurs
	 */

	/**
	 * Registers the given driver with the <code>DriverManager</code>.
	 * A newly-loaded driver class should call
	 * the method <code>registerDriver</code> to make itself
	 * known to the <code>DriverManager</code>.
	 *
	 * @param driver the new JDBC Driver that is to be registered with the
	 *               <code>DriverManager</code>
	 * @exception SQLException if a database access error occurs
	 */
	public static synchronized void registerDriver(java.sql.Driver driver) throws SQLException
	{
	}

	/**
	 * Drops a driver from the <code>DriverManager</code>'s list.
	 *  Applets can only deregister drivers from their own classloaders.
	 *
	 * @param driver the JDBC Driver to drop
	 * @exception SQLException if a database access error occurs
	 */
	public static synchronized void deregisterDriver(Driver driver) throws SQLException
	{
	}

	/**
	 * Sets the maximum time in seconds that a driver will wait
	 * while attempting to connect to a database.
	 *
	 * @param seconds the login time limit in seconds; zero means there is no limit
	 * @see #getLoginTimeout
	 */
	public static void setLoginTimeout(int seconds)
	{
		loginTimeout= seconds;
	}

	/**
	 * Gets the maximum time in seconds that a driver can wait
	 * when attempting to log in to a database.
	 *
	 * @return the driver login time limit in seconds
	 * @see #setLoginTimeout
	 */
	public static int getLoginTimeout()
	{
		return (loginTimeout);
	}

	/**
	 * Retrieves the logging/tracing PrintStream that is used by the <code>DriverManager</code>
	 * and all drivers.
	 *
	 * @return the logging/tracing PrintStream; if disabled, is <code>null</code>
	 * @deprecated
	 * @see #setLogStream
	 */
	public static java.io.PrintStream getLogStream()
	{
		return logStream;
	}

	// Class initialization.
	static void initialize()
	{
	}

	/* Prevent the DriverManager class from being instantiated. */
	private DriverManager()
	{
	}

	/* write copy of the drivers vector */
	private static java.util.Vector writeDrivers= new java.util.Vector();

	/* write copy of the drivers vector */
	private static java.util.Vector readDrivers= new java.util.Vector();

	private static int loginTimeout= 0;
	private static java.io.PrintWriter logWriter= null;
	private static java.io.PrintStream logStream= null;
	private static boolean initialized= false;

	private static Object logSync= new Object();

	/* Returns the caller's class loader, or null if none */
	private static native ClassLoader getCallerClassLoader();

	public static Connection getConnection(String string)
	{
		Class<?> className;
		try
		{
			className= Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				className= Class.forName("net.ar.dragome.jdbc.DragomeJdbcDriver");
			}
			catch (Exception e1)
			{
				throw new RuntimeException(e1);
			}
		}

		try
		{
			Driver newInstance= (Driver) className.newInstance();
			return newInstance.connect(string, new Properties());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
