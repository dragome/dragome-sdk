/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.sql;

/**
 * The subclass of {@link SQLException} is thrown when one or more client info properties
 * could not be set on a <code>Connection</code>.  In addition to the information provided
 * by <code>SQLException</code>, a <code>SQLClientInfoException</code> provides a list of client info
 * properties that were not set.
 *
 * Some databases do not allow multiple client info properties to be set
 * atomically.  For those databases, it is possible that some of the client
 * info properties had been set even though the <code>Connection.setClientInfo</code>
 * method threw an exception.  An application can use the <code>getFailedProperties </code>
 * method to retrieve a list of client info properties that were not set.  The
 * properties are identified by passing a
 * <code>Map&lt;String,ClientInfoStatus&gt;</code> to
 * the appropriate <code>SQLClientInfoException</code> constructor.
 * <p>
 * @see ClientInfoStatus
 * @see Connection#setClientInfo
 * @since 1.6
 */
public class SQLClientInfoException extends SQLException
{
}
