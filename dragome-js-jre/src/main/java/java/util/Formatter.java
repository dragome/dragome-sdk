/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.util;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DecimalFormatSymbols;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Pattern;

/**
 * An interpreter for printf-style format strings.  This class provides support
 * for layout justification and alignment, common formats for numeric, string,
 * and date/time data, and locale-specific output.  Common Java types such as
 * {@code byte}, {@link java.math.BigDecimal BigDecimal}, and {@link Calendar}
 * are supported.  Limited formatting customization for arbitrary user types is
 * provided through the {@link Formattable} interface.
 *
 * <p> Formatters are not necessarily safe for multithreaded access.  Thread
 * safety is optional and is the responsibility of users of methods in this
 * class.
 *
 * <p> Formatted printing for the Java language is heavily inspired by C's
 * {@code printf}.  Although the format strings are similar to C, some
 * customizations have been made to accommodate the Java language and exploit
 * some of its features.  Also, Java formatting is more strict than C's; for
 * example, if a conversion is incompatible with a flag, an exception will be
 * thrown.  In C inapplicable flags are silently ignored.  The format strings
 * are thus intended to be recognizable to C programmers but not necessarily
 * completely compatible with those in C.
 *
 * <p> Examples of expected usage:
 *
 * <blockquote><pre>
 *   StringBuilder sb = new StringBuilder();
 *   // Send all output to the Appendable object sb
 *   Formatter formatter = new Formatter(sb, Locale.US);
 *
 *   // Explicit argument indices may be used to re-order output.
 *   formatter.format("%4$2s %3$2s %2$2s %1$2s", "a", "b", "c", "d")
 *   // -&gt; " d  c  b  a"
 *
 *   // Optional locale as the first argument can be used to get
 *   // locale-specific formatting of numbers.  The precision and width can be
 *   // given to round and align the value.
 *   formatter.format(Locale.FRANCE, "e = %+10.4f", Math.E);
 *   // -&gt; "e =    +2,7183"
 *
 *   // The '(' numeric flag may be used to format negative numbers with
 *   // parentheses rather than a minus sign.  Group separators are
 *   // automatically inserted.
 *   formatter.format("Amount gained or lost since last statement: $ %(,.2f",
 *                    balanceDelta);
 *   // -&gt; "Amount gained or lost since last statement: $ (6,217.58)"
 * </pre></blockquote>
 *
 * <p> Convenience methods for common formatting requests exist as illustrated
 * by the following invocations:
 *
 * <blockquote><pre>
 *   // Writes a formatted string to System.out.
 *   System.out.format("Local time: %tT", Calendar.getInstance());
 *   // -&gt; "Local time: 13:34:18"
 *
 *   // Writes formatted output to System.err.
 *   System.err.printf("Unable to open file '%1$s': %2$s",
 *                     fileName, exception.getMessage());
 *   // -&gt; "Unable to open file 'food': No such file or directory"
 * </pre></blockquote>
 *
 * <p> Like C's {@code sprintf(3)}, Strings may be formatted using the static
 * method {@link String#format(String,Object...) String.format}:
 *
 * <blockquote><pre>
 *   // Format a string containing a date.
 *   import java.util.Calendar;
 *   import java.util.GregorianCalendar;
 *   import static java.util.Calendar.*;
 *
 *   Calendar c = new GregorianCalendar(1995, MAY, 23);
 *   String s = String.format("Duke's Birthday: %1$tb %1$te, %1$tY", c);
 *   // -&gt; s == "Duke's Birthday: May 23, 1995"
 * </pre></blockquote>
 *
 * <h3><a name="org">Organization</a></h3>
 *
 * <p> This specification is divided into two sections.  The first section, <a
 * href="#summary">Summary</a>, covers the basic formatting concepts.  This
 * section is intended for users who want to get started quickly and are
 * familiar with formatted printing in other programming languages.  The second
 * section, <a href="#detail">Details</a>, covers the specific implementation
 * details.  It is intended for users who want more precise specification of
 * formatting behavior.
 *
 * <h3><a name="summary">Summary</a></h3>
 *
 * <p> This section is intended to provide a brief overview of formatting
 * concepts.  For precise behavioral details, refer to the <a
 * href="#detail">Details</a> section.
 *
 * <h4><a name="syntax">Format String Syntax</a></h4>
 *
 * <p> Every method which produces formatted output requires a <i>format
 * string</i> and an <i>argument list</i>.  The format string is a {@link
 * String} which may contain fixed text and one or more embedded <i>format
 * specifiers</i>.  Consider the following example:
 *
 * <blockquote><pre>
 *   Calendar c = ...;
 *   String s = String.format("Duke's Birthday: %1$tm %1$te,%1$tY", c);
 * </pre></blockquote>
 *
 * This format string is the first argument to the {@code format} method.  It
 * contains three format specifiers "{@code %1$tm}", "{@code %1$te}", and
 * "{@code %1$tY}" which indicate how the arguments should be processed and
 * where they should be inserted in the text.  The remaining portions of the
 * format string are fixed text including {@code "Dukes Birthday: "} and any
 * other spaces or punctuation.
 *
 * The argument list consists of all arguments passed to the method after the
 * format string.  In the above example, the argument list is of size one and
 * consists of the {@link java.util.Calendar Calendar} object {@code c}.
 *
 * <ul>
 *
 * <li> The format specifiers for general, character, and numeric types have
 * the following syntax:
 *
 * <blockquote><pre>
 *   %[argument_index$][flags][width][.precision]conversion
 * </pre></blockquote>
 *
 * <p> The optional <i>argument_index</i> is a decimal integer indicating the
 * position of the argument in the argument list.  The first argument is
 * referenced by "{@code 1$}", the second by "{@code 2$}", etc.
 *
 * <p> The optional <i>flags</i> is a set of characters that modify the output
 * format.  The set of valid flags depends on the conversion.
 *
 * <p> The optional <i>width</i> is a positive decimal integer indicating
 * the minimum number of characters to be written to the output.
 *
 * <p> The optional <i>precision</i> is a non-negative decimal integer usually
 * used to restrict the number of characters.  The specific behavior depends on
 * the conversion.
 *
 * <p> The required <i>conversion</i> is a character indicating how the
 * argument should be formatted.  The set of valid conversions for a given
 * argument depends on the argument's data type.
 *
 * <li> The format specifiers for types which are used to represents dates and
 * times have the following syntax:
 *
 * <blockquote><pre>
 *   %[argument_index$][flags][width]conversion
 * </pre></blockquote>
 *
 * <p> The optional <i>argument_index</i>, <i>flags</i> and <i>width</i> are
 * defined as above.
 *
 * <p> The required <i>conversion</i> is a two character sequence.  The first
 * character is {@code 't'} or {@code 'T'}.  The second character indicates
 * the format to be used.  These characters are similar to but not completely
 * identical to those defined by GNU {@code date} and POSIX
 * {@code strftime(3c)}.
 *
 * <li> The format specifiers which do not correspond to arguments have the
 * following syntax:
 *
 * <blockquote><pre>
 *   %[flags][width]conversion
 * </pre></blockquote>
 *
 * <p> The optional <i>flags</i> and <i>width</i> is defined as above.
 *
 * <p> The required <i>conversion</i> is a character indicating content to be
 * inserted in the output.
 *
 * </ul>
 *
 * <h4> Conversions </h4>
 *
 * <p> Conversions are divided into the following categories:
 *
 * <ol>
 *
 * <li> <b>General</b> - may be applied to any argument
 * type
 *
 * <li> <b>Character</b> - may be applied to basic types which represent
 * Unicode characters: {@code char}, {@link Character}, {@code byte}, {@link
 * Byte}, {@code short}, and {@link Short}. This conversion may also be
 * applied to the types {@code int} and {@link Integer} when {@link
 * Character#isValidCodePoint} returns {@code true}
 *
 * <li> <b>Numeric</b>
 *
 * <ol>
 *
 * <li> <b>Integral</b> - may be applied to Java integral types: {@code byte},
 * {@link Byte}, {@code short}, {@link Short}, {@code int} and {@link
 * Integer}, {@code long}, {@link Long}, and {@link java.math.BigInteger
 * BigInteger} (but not {@code char} or {@link Character})
 *
 * <li><b>Floating Point</b> - may be applied to Java floating-point types:
 * {@code float}, {@link Float}, {@code double}, {@link Double}, and {@link
 * java.math.BigDecimal BigDecimal}
 *
 * </ol>
 *
 * <li> <b>Date/Time</b> - may be applied to Java types which are capable of
 * encoding a date or time: {@code long}, {@link Long}, {@link Calendar},
 * {@link Date} and {@link TemporalAccessor TemporalAccessor}
 *
 * <li> <b>Percent</b> - produces a literal {@code '%'}
 * (<tt>'&#92;u0025'</tt>)
 *
 * <li> <b>Line Separator</b> - produces the platform-specific line separator
 *
 * </ol>
 *
 * <p> The following table summarizes the supported conversions.  Conversions
 * denoted by an upper-case character (i.e. {@code 'B'}, {@code 'H'},
 * {@code 'S'}, {@code 'C'}, {@code 'X'}, {@code 'E'}, {@code 'G'},
 * {@code 'A'}, and {@code 'T'}) are the same as those for the corresponding
 * lower-case conversion characters except that the result is converted to
 * upper case according to the rules of the prevailing {@link java.util.Locale
 * Locale}.  The result is equivalent to the following invocation of {@link
 * String#toUpperCase()}
 *
 * <pre>
 *    out.toUpperCase() </pre>
 *
 * <table cellpadding=5 summary="genConv">
 *
 * <tr><th valign="bottom"> Conversion
 *     <th valign="bottom"> Argument Category
 *     <th valign="bottom"> Description
 *
 * <tr><td valign="top"> {@code 'b'}, {@code 'B'}
 *     <td valign="top"> general
 *     <td> If the argument <i>arg</i> is {@code null}, then the result is
 *     "{@code false}".  If <i>arg</i> is a {@code boolean} or {@link
 *     Boolean}, then the result is the string returned by {@link
 *     String#valueOf(boolean) String.valueOf(arg)}.  Otherwise, the result is
 *     "true".
 *
 * <tr><td valign="top"> {@code 'h'}, {@code 'H'}
 *     <td valign="top"> general
 *     <td> If the argument <i>arg</i> is {@code null}, then the result is
 *     "{@code null}".  Otherwise, the result is obtained by invoking
 *     {@code Integer.toHexString(arg.hashCode())}.
 *
 * <tr><td valign="top"> {@code 's'}, {@code 'S'}
 *     <td valign="top"> general
 *     <td> If the argument <i>arg</i> is {@code null}, then the result is
 *     "{@code null}".  If <i>arg</i> implements {@link Formattable}, then
 *     {@link Formattable#formatTo arg.formatTo} is invoked. Otherwise, the
 *     result is obtained by invoking {@code arg.toString()}.
 *
 * <tr><td valign="top">{@code 'c'}, {@code 'C'}
 *     <td valign="top"> character
 *     <td> The result is a Unicode character
 *
 * <tr><td valign="top">{@code 'd'}
 *     <td valign="top"> integral
 *     <td> The result is formatted as a decimal integer
 *
 * <tr><td valign="top">{@code 'o'}
 *     <td valign="top"> integral
 *     <td> The result is formatted as an octal integer
 *
 * <tr><td valign="top">{@code 'x'}, {@code 'X'}
 *     <td valign="top"> integral
 *     <td> The result is formatted as a hexadecimal integer
 *
 * <tr><td valign="top">{@code 'e'}, {@code 'E'}
 *     <td valign="top"> floating point
 *     <td> The result is formatted as a decimal number in computerized
 *     scientific notation
 *
 * <tr><td valign="top">{@code 'f'}
 *     <td valign="top"> floating point
 *     <td> The result is formatted as a decimal number
 *
 * <tr><td valign="top">{@code 'g'}, {@code 'G'}
 *     <td valign="top"> floating point
 *     <td> The result is formatted using computerized scientific notation or
 *     decimal format, depending on the precision and the value after rounding.
 *
 * <tr><td valign="top">{@code 'a'}, {@code 'A'}
 *     <td valign="top"> floating point
 *     <td> The result is formatted as a hexadecimal floating-point number with
 *     a significand and an exponent. This conversion is <b>not</b> supported
 *     for the {@code BigDecimal} type despite the latter's being in the
 *     <i>floating point</i> argument category.
 *
 * <tr><td valign="top">{@code 't'}, {@code 'T'}
 *     <td valign="top"> date/time
 *     <td> Prefix for date and time conversion characters.  See <a
 *     href="#dt">Date/Time Conversions</a>.
 *
 * <tr><td valign="top">{@code '%'}
 *     <td valign="top"> percent
 *     <td> The result is a literal {@code '%'} (<tt>'&#92;u0025'</tt>)
 *
 * <tr><td valign="top">{@code 'n'}
 *     <td valign="top"> line separator
 *     <td> The result is the platform-specific line separator
 *
 * </table>
 *
 * <p> Any characters not explicitly defined as conversions are illegal and are
 * reserved for future extensions.
 *
 * <h4><a name="dt">Date/Time Conversions</a></h4>
 *
 * <p> The following date and time conversion suffix characters are defined for
 * the {@code 't'} and {@code 'T'} conversions.  The types are similar to but
 * not completely identical to those defined by GNU {@code date} and POSIX
 * {@code strftime(3c)}.  Additional conversion types are provided to access
 * Java-specific functionality (e.g. {@code 'L'} for milliseconds within the
 * second).
 *
 * <p> The following conversion characters are used for formatting times:
 *
 * <table cellpadding=5 summary="time">
 *
 * <tr><td valign="top"> {@code 'H'}
 *     <td> Hour of the day for the 24-hour clock, formatted as two digits with
 *     a leading zero as necessary i.e. {@code 00 - 23}.
 *
 * <tr><td valign="top">{@code 'I'}
 *     <td> Hour for the 12-hour clock, formatted as two digits with a leading
 *     zero as necessary, i.e.  {@code 01 - 12}.
 *
 * <tr><td valign="top">{@code 'k'}
 *     <td> Hour of the day for the 24-hour clock, i.e. {@code 0 - 23}.
 *
 * <tr><td valign="top">{@code 'l'}
 *     <td> Hour for the 12-hour clock, i.e. {@code 1 - 12}.
 *
 * <tr><td valign="top">{@code 'M'}
 *     <td> Minute within the hour formatted as two digits with a leading zero
 *     as necessary, i.e.  {@code 00 - 59}.
 *
 * <tr><td valign="top">{@code 'S'}
 *     <td> Seconds within the minute, formatted as two digits with a leading
 *     zero as necessary, i.e. {@code 00 - 60} ("{@code 60}" is a special
 *     value required to support leap seconds).
 *
 * <tr><td valign="top">{@code 'L'}
 *     <td> Millisecond within the second formatted as three digits with
 *     leading zeros as necessary, i.e. {@code 000 - 999}.
 *
 * <tr><td valign="top">{@code 'N'}
 *     <td> Nanosecond within the second, formatted as nine digits with leading
 *     zeros as necessary, i.e. {@code 000000000 - 999999999}.
 *
 * <tr><td valign="top">{@code 'p'}
 *     <td> Locale-specific {@linkplain
 *     java.text.DateFormatSymbols#getAmPmStrings morning or afternoon} marker
 *     in lower case, e.g."{@code am}" or "{@code pm}". Use of the conversion
 *     prefix {@code 'T'} forces this output to upper case.
 *
 * <tr><td valign="top">{@code 'z'}
 *     <td> <a href="http://www.ietf.org/rfc/rfc0822.txt">RFC&nbsp;822</a>
 *     style numeric time zone offset from GMT, e.g. {@code -0800}.  This
 *     value will be adjusted as necessary for Daylight Saving Time.  For
 *     {@code long}, {@link Long}, and {@link Date} the time zone used is
 *     the {@linkplain TimeZone#getDefault() default time zone} for this
 *     instance of the Java virtual machine.
 *
 * <tr><td valign="top">{@code 'Z'}
 *     <td> A string representing the abbreviation for the time zone.  This
 *     value will be adjusted as necessary for Daylight Saving Time.  For
 *     {@code long}, {@link Long}, and {@link Date} the  time zone used is
 *     the {@linkplain TimeZone#getDefault() default time zone} for this
 *     instance of the Java virtual machine.  The Formatter's locale will
 *     supersede the locale of the argument (if any).
 *
 * <tr><td valign="top">{@code 's'}
 *     <td> Seconds since the beginning of the epoch starting at 1 January 1970
 *     {@code 00:00:00} UTC, i.e. {@code Long.MIN_VALUE/1000} to
 *     {@code Long.MAX_VALUE/1000}.
 *
 * <tr><td valign="top">{@code 'Q'}
 *     <td> Milliseconds since the beginning of the epoch starting at 1 January
 *     1970 {@code 00:00:00} UTC, i.e. {@code Long.MIN_VALUE} to
 *     {@code Long.MAX_VALUE}.
 *
 * </table>
 *
 * <p> The following conversion characters are used for formatting dates:
 *
 * <table cellpadding=5 summary="date">
 *
 * <tr><td valign="top">{@code 'B'}
 *     <td> Locale-specific {@linkplain java.text.DateFormatSymbols#getMonths
 *     full month name}, e.g. {@code "January"}, {@code "February"}.
 *
 * <tr><td valign="top">{@code 'b'}
 *     <td> Locale-specific {@linkplain
 *     java.text.DateFormatSymbols#getShortMonths abbreviated month name},
 *     e.g. {@code "Jan"}, {@code "Feb"}.
 *
 * <tr><td valign="top">{@code 'h'}
 *     <td> Same as {@code 'b'}.
 *
 * <tr><td valign="top">{@code 'A'}
 *     <td> Locale-specific full name of the {@linkplain
 *     java.text.DateFormatSymbols#getWeekdays day of the week},
 *     e.g. {@code "Sunday"}, {@code "Monday"}
 *
 * <tr><td valign="top">{@code 'a'}
 *     <td> Locale-specific short name of the {@linkplain
 *     java.text.DateFormatSymbols#getShortWeekdays day of the week},
 *     e.g. {@code "Sun"}, {@code "Mon"}
 *
 * <tr><td valign="top">{@code 'C'}
 *     <td> Four-digit year divided by {@code 100}, formatted as two digits
 *     with leading zero as necessary, i.e. {@code 00 - 99}
 *
 * <tr><td valign="top">{@code 'Y'}
 *     <td> Year, formatted as at least four digits with leading zeros as
 *     necessary, e.g. {@code 0092} equals {@code 92} CE for the Gregorian
 *     calendar.
 *
 * <tr><td valign="top">{@code 'y'}
 *     <td> Last two digits of the year, formatted with leading zeros as
 *     necessary, i.e. {@code 00 - 99}.
 *
 * <tr><td valign="top">{@code 'j'}
 *     <td> Day of year, formatted as three digits with leading zeros as
 *     necessary, e.g. {@code 001 - 366} for the Gregorian calendar.
 *
 * <tr><td valign="top">{@code 'm'}
 *     <td> Month, formatted as two digits with leading zeros as necessary,
 *     i.e. {@code 01 - 13}.
 *
 * <tr><td valign="top">{@code 'd'}
 *     <td> Day of month, formatted as two digits with leading zeros as
 *     necessary, i.e. {@code 01 - 31}
 *
 * <tr><td valign="top">{@code 'e'}
 *     <td> Day of month, formatted as two digits, i.e. {@code 1 - 31}.
 *
 * </table>
 *
 * <p> The following conversion characters are used for formatting common
 * date/time compositions.
 *
 * <table cellpadding=5 summary="composites">
 *
 * <tr><td valign="top">{@code 'R'}
 *     <td> Time formatted for the 24-hour clock as {@code "%tH:%tM"}
 *
 * <tr><td valign="top">{@code 'T'}
 *     <td> Time formatted for the 24-hour clock as {@code "%tH:%tM:%tS"}.
 *
 * <tr><td valign="top">{@code 'r'}
 *     <td> Time formatted for the 12-hour clock as {@code "%tI:%tM:%tS %Tp"}.
 *     The location of the morning or afternoon marker ({@code '%Tp'}) may be
 *     locale-dependent.
 *
 * <tr><td valign="top">{@code 'D'}
 *     <td> Date formatted as {@code "%tm/%td/%ty"}.
 *
 * <tr><td valign="top">{@code 'F'}
 *     <td> <a href="http://www.w3.org/TR/NOTE-datetime">ISO&nbsp;8601</a>
 *     complete date formatted as {@code "%tY-%tm-%td"}.
 *
 * <tr><td valign="top">{@code 'c'}
 *     <td> Date and time formatted as {@code "%ta %tb %td %tT %tZ %tY"},
 *     e.g. {@code "Sun Jul 20 16:17:00 EDT 1969"}.
 *
 * </table>
 *
 * <p> Any characters not explicitly defined as date/time conversion suffixes
 * are illegal and are reserved for future extensions.
 *
 * <h4> Flags </h4>
 *
 * <p> The following table summarizes the supported flags.  <i>y</i> means the
 * flag is supported for the indicated argument types.
 *
 * <table cellpadding=5 summary="genConv">
 *
 * <tr><th valign="bottom"> Flag <th valign="bottom"> General
 *     <th valign="bottom"> Character <th valign="bottom"> Integral
 *     <th valign="bottom"> Floating Point
 *     <th valign="bottom"> Date/Time
 *     <th valign="bottom"> Description
 *
 * <tr><td> '-' <td align="center" valign="top"> y
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> y
 *     <td> The result will be left-justified.
 *
 * <tr><td> '#' <td align="center" valign="top"> y<sup>1</sup>
 *     <td align="center" valign="top"> -
 *     <td align="center" valign="top"> y<sup>3</sup>
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> -
 *     <td> The result should use a conversion-dependent alternate form
 *
 * <tr><td> '+' <td align="center" valign="top"> -
 *     <td align="center" valign="top"> -
 *     <td align="center" valign="top"> y<sup>4</sup>
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> -
 *     <td> The result will always include a sign
 *
 * <tr><td> '&nbsp;&nbsp;' <td align="center" valign="top"> -
 *     <td align="center" valign="top"> -
 *     <td align="center" valign="top"> y<sup>4</sup>
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> -
 *     <td> The result will include a leading space for positive values
 *
 * <tr><td> '0' <td align="center" valign="top"> -
 *     <td align="center" valign="top"> -
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> y
 *     <td align="center" valign="top"> -
 *     <td> The result will be zero-padded
 *
 * <tr><td> ',' <td align="center" valign="top"> -
 *     <td align="center" valign="top"> -
 *     <td align="center" valign="top"> y<sup>2</sup>
 *     <td align="center" valign="top"> y<sup>5</sup>
 *     <td align="center" valign="top"> -
 *     <td> The result will include locale-specific {@linkplain
 *     java.text.DecimalFormatSymbols#getGroupingSeparator grouping separators}
 *
 * <tr><td> '(' <td align="center" valign="top"> -
 *     <td align="center" valign="top"> -
 *     <td align="center" valign="top"> y<sup>4</sup>
 *     <td align="center" valign="top"> y<sup>5</sup>
 *     <td align="center"> -
 *     <td> The result will enclose negative numbers in parentheses
 *
 * </table>
 *
 * <p> <sup>1</sup> Depends on the definition of {@link Formattable}.
 *
 * <p> <sup>2</sup> For {@code 'd'} conversion only.
 *
 * <p> <sup>3</sup> For {@code 'o'}, {@code 'x'}, and {@code 'X'}
 * conversions only.
 *
 * <p> <sup>4</sup> For {@code 'd'}, {@code 'o'}, {@code 'x'}, and
 * {@code 'X'} conversions applied to {@link java.math.BigInteger BigInteger}
 * or {@code 'd'} applied to {@code byte}, {@link Byte}, {@code short}, {@link
 * Short}, {@code int} and {@link Integer}, {@code long}, and {@link Long}.
 *
 * <p> <sup>5</sup> For {@code 'e'}, {@code 'E'}, {@code 'f'},
 * {@code 'g'}, and {@code 'G'} conversions only.
 *
 * <p> Any characters not explicitly defined as flags are illegal and are
 * reserved for future extensions.
 *
 * <h4> Width </h4>
 *
 * <p> The width is the minimum number of characters to be written to the
 * output.  For the line separator conversion, width is not applicable; if it
 * is provided, an exception will be thrown.
 *
 * <h4> Precision </h4>
 *
 * <p> For general argument types, the precision is the maximum number of
 * characters to be written to the output.
 *
 * <p> For the floating-point conversions {@code 'a'}, {@code 'A'}, {@code 'e'},
 * {@code 'E'}, and {@code 'f'} the precision is the number of digits after the
 * radix point.  If the conversion is {@code 'g'} or {@code 'G'}, then the
 * precision is the total number of digits in the resulting magnitude after
 * rounding.
 *
 * <p> For character, integral, and date/time argument types and the percent
 * and line separator conversions, the precision is not applicable; if a
 * precision is provided, an exception will be thrown.
 *
 * <h4> Argument Index </h4>
 *
 * <p> The argument index is a decimal integer indicating the position of the
 * argument in the argument list.  The first argument is referenced by
 * "{@code 1$}", the second by "{@code 2$}", etc.
 *
 * <p> Another way to reference arguments by position is to use the
 * {@code '<'} (<tt>'&#92;u003c'</tt>) flag, which causes the argument for
 * the previous format specifier to be re-used.  For example, the following two
 * statements would produce identical strings:
 *
 * <blockquote><pre>
 *   Calendar c = ...;
 *   String s1 = String.format("Duke's Birthday: %1$tm %1$te,%1$tY", c);
 *
 *   String s2 = String.format("Duke's Birthday: %1$tm %&lt;te,%&lt;tY", c);
 * </pre></blockquote>
 *
 * <hr>
 * <h3><a name="detail">Details</a></h3>
 *
 * <p> This section is intended to provide behavioral details for formatting,
 * including conditions and exceptions, supported data types, localization, and
 * interactions between flags, conversions, and data types.  For an overview of
 * formatting concepts, refer to the <a href="#summary">Summary</a>
 *
 * <p> Any characters not explicitly defined as conversions, date/time
 * conversion suffixes, or flags are illegal and are reserved for
 * future extensions.  Use of such a character in a format string will
 * cause an {@link UnknownFormatConversionException} or {@link
 * UnknownFormatFlagsException} to be thrown.
 *
 * <p> If the format specifier contains a width or precision with an invalid
 * value or which is otherwise unsupported, then a {@link
 * IllegalFormatWidthException} or {@link IllegalFormatPrecisionException}
 * respectively will be thrown.
 *
 * <p> If a format specifier contains a conversion character that is not
 * applicable to the corresponding argument, then an {@link
 * IllegalFormatConversionException} will be thrown.
 *
 * <p> All specified exceptions may be thrown by any of the {@code format}
 * methods of {@code Formatter} as well as by any {@code format} convenience
 * methods such as {@link String#format(String,Object...) String.format} and
 * {@link java.io.PrintStream#printf(String,Object...) PrintStream.printf}.
 *
 * <p> Conversions denoted by an upper-case character (i.e. {@code 'B'},
 * {@code 'H'}, {@code 'S'}, {@code 'C'}, {@code 'X'}, {@code 'E'},
 * {@code 'G'}, {@code 'A'}, and {@code 'T'}) are the same as those for the
 * corresponding lower-case conversion characters except that the result is
 * converted to upper case according to the rules of the prevailing {@link
 * java.util.Locale Locale}.  The result is equivalent to the following
 * invocation of {@link String#toUpperCase()}
 *
 * <pre>
 *    out.toUpperCase() </pre>
 *
 * <h4><a name="dgen">General</a></h4>
 *
 * <p> The following general conversions may be applied to any argument type:
 *
 * <table cellpadding=5 summary="dgConv">
 *
 * <tr><td valign="top"> {@code 'b'}
 *     <td valign="top"> <tt>'&#92;u0062'</tt>
 *     <td> Produces either "{@code true}" or "{@code false}" as returned by
 *     {@link Boolean#toString(boolean)}.
 *
 *     <p> If the argument is {@code null}, then the result is
 *     "{@code false}".  If the argument is a {@code boolean} or {@link
 *     Boolean}, then the result is the string returned by {@link
 *     String#valueOf(boolean) String.valueOf()}.  Otherwise, the result is
 *     "{@code true}".
 *
 *     <p> If the {@code '#'} flag is given, then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'B'}
 *     <td valign="top"> <tt>'&#92;u0042'</tt>
 *     <td> The upper-case variant of {@code 'b'}.
 *
 * <tr><td valign="top"> {@code 'h'}
 *     <td valign="top"> <tt>'&#92;u0068'</tt>
 *     <td> Produces a string representing the hash code value of the object.
 *
 *     <p> If the argument, <i>arg</i> is {@code null}, then the
 *     result is "{@code null}".  Otherwise, the result is obtained
 *     by invoking {@code Integer.toHexString(arg.hashCode())}.
 *
 *     <p> If the {@code '#'} flag is given, then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'H'}
 *     <td valign="top"> <tt>'&#92;u0048'</tt>
 *     <td> The upper-case variant of {@code 'h'}.
 *
 * <tr><td valign="top"> {@code 's'}
 *     <td valign="top"> <tt>'&#92;u0073'</tt>
 *     <td> Produces a string.
 *
 *     <p> If the argument is {@code null}, then the result is
 *     "{@code null}".  If the argument implements {@link Formattable}, then
 *     its {@link Formattable#formatTo formatTo} method is invoked.
 *     Otherwise, the result is obtained by invoking the argument's
 *     {@code toString()} method.
 *
 *     <p> If the {@code '#'} flag is given and the argument is not a {@link
 *     Formattable} , then a {@link FormatFlagsConversionMismatchException}
 *     will be thrown.
 *
 * <tr><td valign="top"> {@code 'S'}
 *     <td valign="top"> <tt>'&#92;u0053'</tt>
 *     <td> The upper-case variant of {@code 's'}.
 *
 * </table>
 *
 * <p> The following <a name="dFlags">flags</a> apply to general conversions:
 *
 * <table cellpadding=5 summary="dFlags">
 *
 * <tr><td valign="top"> {@code '-'}
 *     <td valign="top"> <tt>'&#92;u002d'</tt>
 *     <td> Left justifies the output.  Spaces (<tt>'&#92;u0020'</tt>) will be
 *     added at the end of the converted value as required to fill the minimum
 *     width of the field.  If the width is not provided, then a {@link
 *     MissingFormatWidthException} will be thrown.  If this flag is not given
 *     then the output will be right-justified.
 *
 * <tr><td valign="top"> {@code '#'}
 *     <td valign="top"> <tt>'&#92;u0023'</tt>
 *     <td> Requires the output use an alternate form.  The definition of the
 *     form is specified by the conversion.
 *
 * </table>
 *
 * <p> The <a name="genWidth">width</a> is the minimum number of characters to
 * be written to the
 * output.  If the length of the converted value is less than the width then
 * the output will be padded by <tt>'&nbsp;&nbsp;'</tt> (<tt>'&#92;u0020'</tt>)
 * until the total number of characters equals the width.  The padding is on
 * the left by default.  If the {@code '-'} flag is given, then the padding
 * will be on the right.  If the width is not specified then there is no
 * minimum.
 *
 * <p> The precision is the maximum number of characters to be written to the
 * output.  The precision is applied before the width, thus the output will be
 * truncated to {@code precision} characters even if the width is greater than
 * the precision.  If the precision is not specified then there is no explicit
 * limit on the number of characters.
 *
 * <h4><a name="dchar">Character</a></h4>
 *
 * This conversion may be applied to {@code char} and {@link Character}.  It
 * may also be applied to the types {@code byte}, {@link Byte},
 * {@code short}, and {@link Short}, {@code int} and {@link Integer} when
 * {@link Character#isValidCodePoint} returns {@code true}.  If it returns
 * {@code false} then an {@link IllegalFormatCodePointException} will be
 * thrown.
 *
 * <table cellpadding=5 summary="charConv">
 *
 * <tr><td valign="top"> {@code 'c'}
 *     <td valign="top"> <tt>'&#92;u0063'</tt>
 *     <td> Formats the argument as a Unicode character as described in <a
 *     href="../lang/Character.html#unicode">Unicode Character
 *     Representation</a>.  This may be more than one 16-bit {@code char} in
 *     the case where the argument represents a supplementary character.
 *
 *     <p> If the {@code '#'} flag is given, then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'C'}
 *     <td valign="top"> <tt>'&#92;u0043'</tt>
 *     <td> The upper-case variant of {@code 'c'}.
 *
 * </table>
 *
 * <p> The {@code '-'} flag defined for <a href="#dFlags">General
 * conversions</a> applies.  If the {@code '#'} flag is given, then a {@link
 * FormatFlagsConversionMismatchException} will be thrown.
 *
 * <p> The width is defined as for <a href="#genWidth">General conversions</a>.
 *
 * <p> The precision is not applicable.  If the precision is specified then an
 * {@link IllegalFormatPrecisionException} will be thrown.
 *
 * <h4><a name="dnum">Numeric</a></h4>
 *
 * <p> Numeric conversions are divided into the following categories:
 *
 * <ol>
 *
 * <li> <a href="#dnint"><b>Byte, Short, Integer, and Long</b></a>
 *
 * <li> <a href="#dnbint"><b>BigInteger</b></a>
 *
 * <li> <a href="#dndec"><b>Float and Double</b></a>
 *
 * <li> <a href="#dnbdec"><b>BigDecimal</b></a>
 *
 * </ol>
 *
 * <p> Numeric types will be formatted according to the following algorithm:
 *
 * <p><b><a name="L10nAlgorithm"> Number Localization Algorithm</a></b>
 *
 * <p> After digits are obtained for the integer part, fractional part, and
 * exponent (as appropriate for the data type), the following transformation
 * is applied:
 *
 * <ol>
 *
 * <li> Each digit character <i>d</i> in the string is replaced by a
 * locale-specific digit computed relative to the current locale's
 * {@linkplain java.text.DecimalFormatSymbols#getZeroDigit() zero digit}
 * <i>z</i>; that is <i>d&nbsp;-&nbsp;</i> {@code '0'}
 * <i>&nbsp;+&nbsp;z</i>.
 *
 * <li> If a decimal separator is present, a locale-specific {@linkplain
 * java.text.DecimalFormatSymbols#getDecimalSeparator decimal separator} is
 * substituted.
 *
 * <li> If the {@code ','} (<tt>'&#92;u002c'</tt>)
 * <a name="L10nGroup">flag</a> is given, then the locale-specific {@linkplain
 * java.text.DecimalFormatSymbols#getGroupingSeparator grouping separator} is
 * inserted by scanning the integer part of the string from least significant
 * to most significant digits and inserting a separator at intervals defined by
 * the locale's {@linkplain java.text.DecimalFormat#getGroupingSize() grouping
 * size}.
 *
 * <li> If the {@code '0'} flag is given, then the locale-specific {@linkplain
 * java.text.DecimalFormatSymbols#getZeroDigit() zero digits} are inserted
 * after the sign character, if any, and before the first non-zero digit, until
 * the length of the string is equal to the requested field width.
 *
 * <li> If the value is negative and the {@code '('} flag is given, then a
 * {@code '('} (<tt>'&#92;u0028'</tt>) is prepended and a {@code ')'}
 * (<tt>'&#92;u0029'</tt>) is appended.
 *
 * <li> If the value is negative (or floating-point negative zero) and
 * {@code '('} flag is not given, then a {@code '-'} (<tt>'&#92;u002d'</tt>)
 * is prepended.
 *
 * <li> If the {@code '+'} flag is given and the value is positive or zero (or
 * floating-point positive zero), then a {@code '+'} (<tt>'&#92;u002b'</tt>)
 * will be prepended.
 *
 * </ol>
 *
 * <p> If the value is NaN or positive infinity the literal strings "NaN" or
 * "Infinity" respectively, will be output.  If the value is negative infinity,
 * then the output will be "(Infinity)" if the {@code '('} flag is given
 * otherwise the output will be "-Infinity".  These values are not localized.
 *
 * <p><a name="dnint"><b> Byte, Short, Integer, and Long </b></a>
 *
 * <p> The following conversions may be applied to {@code byte}, {@link Byte},
 * {@code short}, {@link Short}, {@code int} and {@link Integer},
 * {@code long}, and {@link Long}.
 *
 * <table cellpadding=5 summary="IntConv">
 *
 * <tr><td valign="top"> {@code 'd'}
 *     <td valign="top"> <tt>'&#92;u0064'</tt>
 *     <td> Formats the argument as a decimal integer. The <a
 *     href="#L10nAlgorithm">localization algorithm</a> is applied.
 *
 *     <p> If the {@code '0'} flag is given and the value is negative, then
 *     the zero padding will occur after the sign.
 *
 *     <p> If the {@code '#'} flag is given then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'o'}
 *     <td valign="top"> <tt>'&#92;u006f'</tt>
 *     <td> Formats the argument as an integer in base eight.  No localization
 *     is applied.
 *
 *     <p> If <i>x</i> is negative then the result will be an unsigned value
 *     generated by adding 2<sup>n</sup> to the value where {@code n} is the
 *     number of bits in the type as returned by the static {@code SIZE} field
 *     in the {@linkplain Byte#SIZE Byte}, {@linkplain Short#SIZE Short},
 *     {@linkplain Integer#SIZE Integer}, or {@linkplain Long#SIZE Long}
 *     classes as appropriate.
 *
 *     <p> If the {@code '#'} flag is given then the output will always begin
 *     with the radix indicator {@code '0'}.
 *
 *     <p> If the {@code '0'} flag is given then the output will be padded
 *     with leading zeros to the field width following any indication of sign.
 *
 *     <p> If {@code '('}, {@code '+'}, '&nbsp;&nbsp;', or {@code ','} flags
 *     are given then a {@link FormatFlagsConversionMismatchException} will be
 *     thrown.
 *
 * <tr><td valign="top"> {@code 'x'}
 *     <td valign="top"> <tt>'&#92;u0078'</tt>
 *     <td> Formats the argument as an integer in base sixteen. No
 *     localization is applied.
 *
 *     <p> If <i>x</i> is negative then the result will be an unsigned value
 *     generated by adding 2<sup>n</sup> to the value where {@code n} is the
 *     number of bits in the type as returned by the static {@code SIZE} field
 *     in the {@linkplain Byte#SIZE Byte}, {@linkplain Short#SIZE Short},
 *     {@linkplain Integer#SIZE Integer}, or {@linkplain Long#SIZE Long}
 *     classes as appropriate.
 *
 *     <p> If the {@code '#'} flag is given then the output will always begin
 *     with the radix indicator {@code "0x"}.
 *
 *     <p> If the {@code '0'} flag is given then the output will be padded to
 *     the field width with leading zeros after the radix indicator or sign (if
 *     present).
 *
 *     <p> If {@code '('}, <tt>'&nbsp;&nbsp;'</tt>, {@code '+'}, or
 *     {@code ','} flags are given then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'X'}
 *     <td valign="top"> <tt>'&#92;u0058'</tt>
 *     <td> The upper-case variant of {@code 'x'}.  The entire string
 *     representing the number will be converted to {@linkplain
 *     String#toUpperCase upper case} including the {@code 'x'} (if any) and
 *     all hexadecimal digits {@code 'a'} - {@code 'f'}
 *     (<tt>'&#92;u0061'</tt> -  <tt>'&#92;u0066'</tt>).
 *
 * </table>
 *
 * <p> If the conversion is {@code 'o'}, {@code 'x'}, or {@code 'X'} and
 * both the {@code '#'} and the {@code '0'} flags are given, then result will
 * contain the radix indicator ({@code '0'} for octal and {@code "0x"} or
 * {@code "0X"} for hexadecimal), some number of zeros (based on the width),
 * and the value.
 *
 * <p> If the {@code '-'} flag is not given, then the space padding will occur
 * before the sign.
 *
 * <p> The following <a name="intFlags">flags</a> apply to numeric integral
 * conversions:
 *
 * <table cellpadding=5 summary="intFlags">
 *
 * <tr><td valign="top"> {@code '+'}
 *     <td valign="top"> <tt>'&#92;u002b'</tt>
 *     <td> Requires the output to include a positive sign for all positive
 *     numbers.  If this flag is not given then only negative values will
 *     include a sign.
 *
 *     <p> If both the {@code '+'} and <tt>'&nbsp;&nbsp;'</tt> flags are given
 *     then an {@link IllegalFormatFlagsException} will be thrown.
 *
 * <tr><td valign="top"> <tt>'&nbsp;&nbsp;'</tt>
 *     <td valign="top"> <tt>'&#92;u0020'</tt>
 *     <td> Requires the output to include a single extra space
 *     (<tt>'&#92;u0020'</tt>) for non-negative values.
 *
 *     <p> If both the {@code '+'} and <tt>'&nbsp;&nbsp;'</tt> flags are given
 *     then an {@link IllegalFormatFlagsException} will be thrown.
 *
 * <tr><td valign="top"> {@code '0'}
 *     <td valign="top"> <tt>'&#92;u0030'</tt>
 *     <td> Requires the output to be padded with leading {@linkplain
 *     java.text.DecimalFormatSymbols#getZeroDigit zeros} to the minimum field
 *     width following any sign or radix indicator except when converting NaN
 *     or infinity.  If the width is not provided, then a {@link
 *     MissingFormatWidthException} will be thrown.
 *
 *     <p> If both the {@code '-'} and {@code '0'} flags are given then an
 *     {@link IllegalFormatFlagsException} will be thrown.
 *
 * <tr><td valign="top"> {@code ','}
 *     <td valign="top"> <tt>'&#92;u002c'</tt>
 *     <td> Requires the output to include the locale-specific {@linkplain
 *     java.text.DecimalFormatSymbols#getGroupingSeparator group separators} as
 *     described in the <a href="#L10nGroup">"group" section</a> of the
 *     localization algorithm.
 *
 * <tr><td valign="top"> {@code '('}
 *     <td valign="top"> <tt>'&#92;u0028'</tt>
 *     <td> Requires the output to prepend a {@code '('}
 *     (<tt>'&#92;u0028'</tt>) and append a {@code ')'}
 *     (<tt>'&#92;u0029'</tt>) to negative values.
 *
 * </table>
 *
 * <p> If no <a name="intdFlags">flags</a> are given the default formatting is
 * as follows:
 *
 * <ul>
 *
 * <li> The output is right-justified within the {@code width}
 *
 * <li> Negative numbers begin with a {@code '-'} (<tt>'&#92;u002d'</tt>)
 *
 * <li> Positive numbers and zero do not include a sign or extra leading
 * space
 *
 * <li> No grouping separators are included
 *
 * </ul>
 *
 * <p> The <a name="intWidth">width</a> is the minimum number of characters to
 * be written to the output.  This includes any signs, digits, grouping
 * separators, radix indicator, and parentheses.  If the length of the
 * converted value is less than the width then the output will be padded by
 * spaces (<tt>'&#92;u0020'</tt>) until the total number of characters equals
 * width.  The padding is on the left by default.  If {@code '-'} flag is
 * given then the padding will be on the right.  If width is not specified then
 * there is no minimum.
 *
 * <p> The precision is not applicable.  If precision is specified then an
 * {@link IllegalFormatPrecisionException} will be thrown.
 *
 * <p><a name="dnbint"><b> BigInteger </b></a>
 *
 * <p> The following conversions may be applied to {@link
 * java.math.BigInteger}.
 *
 * <table cellpadding=5 summary="BIntConv">
 *
 * <tr><td valign="top"> {@code 'd'}
 *     <td valign="top"> <tt>'&#92;u0064'</tt>
 *     <td> Requires the output to be formatted as a decimal integer. The <a
 *     href="#L10nAlgorithm">localization algorithm</a> is applied.
 *
 *     <p> If the {@code '#'} flag is given {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'o'}
 *     <td valign="top"> <tt>'&#92;u006f'</tt>
 *     <td> Requires the output to be formatted as an integer in base eight.
 *     No localization is applied.
 *
 *     <p> If <i>x</i> is negative then the result will be a signed value
 *     beginning with {@code '-'} (<tt>'&#92;u002d'</tt>).  Signed output is
 *     allowed for this type because unlike the primitive types it is not
 *     possible to create an unsigned equivalent without assuming an explicit
 *     data-type size.
 *
 *     <p> If <i>x</i> is positive or zero and the {@code '+'} flag is given
 *     then the result will begin with {@code '+'} (<tt>'&#92;u002b'</tt>).
 *
 *     <p> If the {@code '#'} flag is given then the output will always begin
 *     with {@code '0'} prefix.
 *
 *     <p> If the {@code '0'} flag is given then the output will be padded
 *     with leading zeros to the field width following any indication of sign.
 *
 *     <p> If the {@code ','} flag is given then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'x'}
 *     <td valign="top"> <tt>'&#92;u0078'</tt>
 *     <td> Requires the output to be formatted as an integer in base
 *     sixteen.  No localization is applied.
 *
 *     <p> If <i>x</i> is negative then the result will be a signed value
 *     beginning with {@code '-'} (<tt>'&#92;u002d'</tt>).  Signed output is
 *     allowed for this type because unlike the primitive types it is not
 *     possible to create an unsigned equivalent without assuming an explicit
 *     data-type size.
 *
 *     <p> If <i>x</i> is positive or zero and the {@code '+'} flag is given
 *     then the result will begin with {@code '+'} (<tt>'&#92;u002b'</tt>).
 *
 *     <p> If the {@code '#'} flag is given then the output will always begin
 *     with the radix indicator {@code "0x"}.
 *
 *     <p> If the {@code '0'} flag is given then the output will be padded to
 *     the field width with leading zeros after the radix indicator or sign (if
 *     present).
 *
 *     <p> If the {@code ','} flag is given then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'X'}
 *     <td valign="top"> <tt>'&#92;u0058'</tt>
 *     <td> The upper-case variant of {@code 'x'}.  The entire string
 *     representing the number will be converted to {@linkplain
 *     String#toUpperCase upper case} including the {@code 'x'} (if any) and
 *     all hexadecimal digits {@code 'a'} - {@code 'f'}
 *     (<tt>'&#92;u0061'</tt> - <tt>'&#92;u0066'</tt>).
 *
 * </table>
 *
 * <p> If the conversion is {@code 'o'}, {@code 'x'}, or {@code 'X'} and
 * both the {@code '#'} and the {@code '0'} flags are given, then result will
 * contain the base indicator ({@code '0'} for octal and {@code "0x"} or
 * {@code "0X"} for hexadecimal), some number of zeros (based on the width),
 * and the value.
 *
 * <p> If the {@code '0'} flag is given and the value is negative, then the
 * zero padding will occur after the sign.
 *
 * <p> If the {@code '-'} flag is not given, then the space padding will occur
 * before the sign.
 *
 * <p> All <a href="#intFlags">flags</a> defined for Byte, Short, Integer, and
 * Long apply.  The <a href="#intdFlags">default behavior</a> when no flags are
 * given is the same as for Byte, Short, Integer, and Long.
 *
 * <p> The specification of <a href="#intWidth">width</a> is the same as
 * defined for Byte, Short, Integer, and Long.
 *
 * <p> The precision is not applicable.  If precision is specified then an
 * {@link IllegalFormatPrecisionException} will be thrown.
 *
 * <p><a name="dndec"><b> Float and Double</b></a>
 *
 * <p> The following conversions may be applied to {@code float}, {@link
 * Float}, {@code double} and {@link Double}.
 *
 * <table cellpadding=5 summary="floatConv">
 *
 * <tr><td valign="top"> {@code 'e'}
 *     <td valign="top"> <tt>'&#92;u0065'</tt>
 *     <td> Requires the output to be formatted using <a
 *     name="scientific">computerized scientific notation</a>.  The <a
 *     href="#L10nAlgorithm">localization algorithm</a> is applied.
 *
 *     <p> The formatting of the magnitude <i>m</i> depends upon its value.
 *
 *     <p> If <i>m</i> is NaN or infinite, the literal strings "NaN" or
 *     "Infinity", respectively, will be output.  These values are not
 *     localized.
 *
 *     <p> If <i>m</i> is positive-zero or negative-zero, then the exponent
 *     will be {@code "+00"}.
 *
 *     <p> Otherwise, the result is a string that represents the sign and
 *     magnitude (absolute value) of the argument.  The formatting of the sign
 *     is described in the <a href="#L10nAlgorithm">localization
 *     algorithm</a>. The formatting of the magnitude <i>m</i> depends upon its
 *     value.
 *
 *     <p> Let <i>n</i> be the unique integer such that 10<sup><i>n</i></sup>
 *     &lt;= <i>m</i> &lt; 10<sup><i>n</i>+1</sup>; then let <i>a</i> be the
 *     mathematically exact quotient of <i>m</i> and 10<sup><i>n</i></sup> so
 *     that 1 &lt;= <i>a</i> &lt; 10. The magnitude is then represented as the
 *     integer part of <i>a</i>, as a single decimal digit, followed by the
 *     decimal separator followed by decimal digits representing the fractional
 *     part of <i>a</i>, followed by the exponent symbol {@code 'e'}
 *     (<tt>'&#92;u0065'</tt>), followed by the sign of the exponent, followed
 *     by a representation of <i>n</i> as a decimal integer, as produced by the
 *     method {@link Long#toString(long, int)}, and zero-padded to include at
 *     least two digits.
 *
 *     <p> The number of digits in the result for the fractional part of
 *     <i>m</i> or <i>a</i> is equal to the precision.  If the precision is not
 *     specified then the default value is {@code 6}. If the precision is less
 *     than the number of digits which would appear after the decimal point in
 *     the string returned by {@link Float#toString(float)} or {@link
 *     Double#toString(double)} respectively, then the value will be rounded
 *     using the {@linkplain java.math.BigDecimal#ROUND_HALF_UP round half up
 *     algorithm}.  Otherwise, zeros may be appended to reach the precision.
 *     For a canonical representation of the value, use {@link
 *     Float#toString(float)} or {@link Double#toString(double)} as
 *     appropriate.
 *
 *     <p>If the {@code ','} flag is given, then an {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'E'}
 *     <td valign="top"> <tt>'&#92;u0045'</tt>
 *     <td> The upper-case variant of {@code 'e'}.  The exponent symbol
 *     will be {@code 'E'} (<tt>'&#92;u0045'</tt>).
 *
 * <tr><td valign="top"> {@code 'g'}
 *     <td valign="top"> <tt>'&#92;u0067'</tt>
 *     <td> Requires the output to be formatted in general scientific notation
 *     as described below. The <a href="#L10nAlgorithm">localization
 *     algorithm</a> is applied.
 *
 *     <p> After rounding for the precision, the formatting of the resulting
 *     magnitude <i>m</i> depends on its value.
 *
 *     <p> If <i>m</i> is greater than or equal to 10<sup>-4</sup> but less
 *     than 10<sup>precision</sup> then it is represented in <i><a
 *     href="#decimal">decimal format</a></i>.
 *
 *     <p> If <i>m</i> is less than 10<sup>-4</sup> or greater than or equal to
 *     10<sup>precision</sup>, then it is represented in <i><a
 *     href="#scientific">computerized scientific notation</a></i>.
 *
 *     <p> The total number of significant digits in <i>m</i> is equal to the
 *     precision.  If the precision is not specified, then the default value is
 *     {@code 6}.  If the precision is {@code 0}, then it is taken to be
 *     {@code 1}.
 *
 *     <p> If the {@code '#'} flag is given then an {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'G'}
 *     <td valign="top"> <tt>'&#92;u0047'</tt>
 *     <td> The upper-case variant of {@code 'g'}.
 *
 * <tr><td valign="top"> {@code 'f'}
 *     <td valign="top"> <tt>'&#92;u0066'</tt>
 *     <td> Requires the output to be formatted using <a name="decimal">decimal
 *     format</a>.  The <a href="#L10nAlgorithm">localization algorithm</a> is
 *     applied.
 *
 *     <p> The result is a string that represents the sign and magnitude
 *     (absolute value) of the argument.  The formatting of the sign is
 *     described in the <a href="#L10nAlgorithm">localization
 *     algorithm</a>. The formatting of the magnitude <i>m</i> depends upon its
 *     value.
 *
 *     <p> If <i>m</i> NaN or infinite, the literal strings "NaN" or
 *     "Infinity", respectively, will be output.  These values are not
 *     localized.
 *
 *     <p> The magnitude is formatted as the integer part of <i>m</i>, with no
 *     leading zeroes, followed by the decimal separator followed by one or
 *     more decimal digits representing the fractional part of <i>m</i>.
 *
 *     <p> The number of digits in the result for the fractional part of
 *     <i>m</i> or <i>a</i> is equal to the precision.  If the precision is not
 *     specified then the default value is {@code 6}. If the precision is less
 *     than the number of digits which would appear after the decimal point in
 *     the string returned by {@link Float#toString(float)} or {@link
 *     Double#toString(double)} respectively, then the value will be rounded
 *     using the {@linkplain java.math.BigDecimal#ROUND_HALF_UP round half up
 *     algorithm}.  Otherwise, zeros may be appended to reach the precision.
 *     For a canonical representation of the value, use {@link
 *     Float#toString(float)} or {@link Double#toString(double)} as
 *     appropriate.
 *
 * <tr><td valign="top"> {@code 'a'}
 *     <td valign="top"> <tt>'&#92;u0061'</tt>
 *     <td> Requires the output to be formatted in hexadecimal exponential
 *     form.  No localization is applied.
 *
 *     <p> The result is a string that represents the sign and magnitude
 *     (absolute value) of the argument <i>x</i>.
 *
 *     <p> If <i>x</i> is negative or a negative-zero value then the result
 *     will begin with {@code '-'} (<tt>'&#92;u002d'</tt>).
 *
 *     <p> If <i>x</i> is positive or a positive-zero value and the
 *     {@code '+'} flag is given then the result will begin with {@code '+'}
 *     (<tt>'&#92;u002b'</tt>).
 *
 *     <p> The formatting of the magnitude <i>m</i> depends upon its value.
 *
 *     <ul>
 *
 *     <li> If the value is NaN or infinite, the literal strings "NaN" or
 *     "Infinity", respectively, will be output.
 *
 *     <li> If <i>m</i> is zero then it is represented by the string
 *     {@code "0x0.0p0"}.
 *
 *     <li> If <i>m</i> is a {@code double} value with a normalized
 *     representation then substrings are used to represent the significand and
 *     exponent fields.  The significand is represented by the characters
 *     {@code "0x1."} followed by the hexadecimal representation of the rest
 *     of the significand as a fraction.  The exponent is represented by
 *     {@code 'p'} (<tt>'&#92;u0070'</tt>) followed by a decimal string of the
 *     unbiased exponent as if produced by invoking {@link
 *     Integer#toString(int) Integer.toString} on the exponent value.  If the
 *     precision is specified, the value is rounded to the given number of
 *     hexadecimal digits.
 *
 *     <li> If <i>m</i> is a {@code double} value with a subnormal
 *     representation then, unless the precision is specified to be in the range
 *     1 through 12, inclusive, the significand is represented by the characters
 *     {@code '0x0.'} followed by the hexadecimal representation of the rest of
 *     the significand as a fraction, and the exponent represented by
 *     {@code 'p-1022'}.  If the precision is in the interval
 *     [1,&nbsp;12], the subnormal value is normalized such that it
 *     begins with the characters {@code '0x1.'}, rounded to the number of
 *     hexadecimal digits of precision, and the exponent adjusted
 *     accordingly.  Note that there must be at least one nonzero digit in a
 *     subnormal significand.
 *
 *     </ul>
 *
 *     <p> If the {@code '('} or {@code ','} flags are given, then a {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'A'}
 *     <td valign="top"> <tt>'&#92;u0041'</tt>
 *     <td> The upper-case variant of {@code 'a'}.  The entire string
 *     representing the number will be converted to upper case including the
 *     {@code 'x'} (<tt>'&#92;u0078'</tt>) and {@code 'p'}
 *     (<tt>'&#92;u0070'</tt> and all hexadecimal digits {@code 'a'} -
 *     {@code 'f'} (<tt>'&#92;u0061'</tt> - <tt>'&#92;u0066'</tt>).
 *
 * </table>
 *
 * <p> All <a href="#intFlags">flags</a> defined for Byte, Short, Integer, and
 * Long apply.
 *
 * <p> If the {@code '#'} flag is given, then the decimal separator will
 * always be present.
 *
 * <p> If no <a name="floatdFlags">flags</a> are given the default formatting
 * is as follows:
 *
 * <ul>
 *
 * <li> The output is right-justified within the {@code width}
 *
 * <li> Negative numbers begin with a {@code '-'}
 *
 * <li> Positive numbers and positive zero do not include a sign or extra
 * leading space
 *
 * <li> No grouping separators are included
 *
 * <li> The decimal separator will only appear if a digit follows it
 *
 * </ul>
 *
 * <p> The <a name="floatDWidth">width</a> is the minimum number of characters
 * to be written to the output.  This includes any signs, digits, grouping
 * separators, decimal separators, exponential symbol, radix indicator,
 * parentheses, and strings representing infinity and NaN as applicable.  If
 * the length of the converted value is less than the width then the output
 * will be padded by spaces (<tt>'&#92;u0020'</tt>) until the total number of
 * characters equals width.  The padding is on the left by default.  If the
 * {@code '-'} flag is given then the padding will be on the right.  If width
 * is not specified then there is no minimum.
 *
 * <p> If the <a name="floatDPrec">conversion</a> is {@code 'e'},
 * {@code 'E'} or {@code 'f'}, then the precision is the number of digits
 * after the decimal separator.  If the precision is not specified, then it is
 * assumed to be {@code 6}.
 *
 * <p> If the conversion is {@code 'g'} or {@code 'G'}, then the precision is
 * the total number of significant digits in the resulting magnitude after
 * rounding.  If the precision is not specified, then the default value is
 * {@code 6}.  If the precision is {@code 0}, then it is taken to be
 * {@code 1}.
 *
 * <p> If the conversion is {@code 'a'} or {@code 'A'}, then the precision
 * is the number of hexadecimal digits after the radix point.  If the
 * precision is not provided, then all of the digits as returned by {@link
 * Double#toHexString(double)} will be output.
 *
 * <p><a name="dnbdec"><b> BigDecimal </b></a>
 *
 * <p> The following conversions may be applied {@link java.math.BigDecimal
 * BigDecimal}.
 *
 * <table cellpadding=5 summary="floatConv">
 *
 * <tr><td valign="top"> {@code 'e'}
 *     <td valign="top"> <tt>'&#92;u0065'</tt>
 *     <td> Requires the output to be formatted using <a
 *     name="bscientific">computerized scientific notation</a>.  The <a
 *     href="#L10nAlgorithm">localization algorithm</a> is applied.
 *
 *     <p> The formatting of the magnitude <i>m</i> depends upon its value.
 *
 *     <p> If <i>m</i> is positive-zero or negative-zero, then the exponent
 *     will be {@code "+00"}.
 *
 *     <p> Otherwise, the result is a string that represents the sign and
 *     magnitude (absolute value) of the argument.  The formatting of the sign
 *     is described in the <a href="#L10nAlgorithm">localization
 *     algorithm</a>. The formatting of the magnitude <i>m</i> depends upon its
 *     value.
 *
 *     <p> Let <i>n</i> be the unique integer such that 10<sup><i>n</i></sup>
 *     &lt;= <i>m</i> &lt; 10<sup><i>n</i>+1</sup>; then let <i>a</i> be the
 *     mathematically exact quotient of <i>m</i> and 10<sup><i>n</i></sup> so
 *     that 1 &lt;= <i>a</i> &lt; 10. The magnitude is then represented as the
 *     integer part of <i>a</i>, as a single decimal digit, followed by the
 *     decimal separator followed by decimal digits representing the fractional
 *     part of <i>a</i>, followed by the exponent symbol {@code 'e'}
 *     (<tt>'&#92;u0065'</tt>), followed by the sign of the exponent, followed
 *     by a representation of <i>n</i> as a decimal integer, as produced by the
 *     method {@link Long#toString(long, int)}, and zero-padded to include at
 *     least two digits.
 *
 *     <p> The number of digits in the result for the fractional part of
 *     <i>m</i> or <i>a</i> is equal to the precision.  If the precision is not
 *     specified then the default value is {@code 6}.  If the precision is
 *     less than the number of digits to the right of the decimal point then
 *     the value will be rounded using the
 *     {@linkplain java.math.BigDecimal#ROUND_HALF_UP round half up
 *     algorithm}.  Otherwise, zeros may be appended to reach the precision.
 *     For a canonical representation of the value, use {@link
 *     BigDecimal#toString()}.
 *
 *     <p> If the {@code ','} flag is given, then an {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'E'}
 *     <td valign="top"> <tt>'&#92;u0045'</tt>
 *     <td> The upper-case variant of {@code 'e'}.  The exponent symbol
 *     will be {@code 'E'} (<tt>'&#92;u0045'</tt>).
 *
 * <tr><td valign="top"> {@code 'g'}
 *     <td valign="top"> <tt>'&#92;u0067'</tt>
 *     <td> Requires the output to be formatted in general scientific notation
 *     as described below. The <a href="#L10nAlgorithm">localization
 *     algorithm</a> is applied.
 *
 *     <p> After rounding for the precision, the formatting of the resulting
 *     magnitude <i>m</i> depends on its value.
 *
 *     <p> If <i>m</i> is greater than or equal to 10<sup>-4</sup> but less
 *     than 10<sup>precision</sup> then it is represented in <i><a
 *     href="#bdecimal">decimal format</a></i>.
 *
 *     <p> If <i>m</i> is less than 10<sup>-4</sup> or greater than or equal to
 *     10<sup>precision</sup>, then it is represented in <i><a
 *     href="#bscientific">computerized scientific notation</a></i>.
 *
 *     <p> The total number of significant digits in <i>m</i> is equal to the
 *     precision.  If the precision is not specified, then the default value is
 *     {@code 6}.  If the precision is {@code 0}, then it is taken to be
 *     {@code 1}.
 *
 *     <p> If the {@code '#'} flag is given then an {@link
 *     FormatFlagsConversionMismatchException} will be thrown.
 *
 * <tr><td valign="top"> {@code 'G'}
 *     <td valign="top"> <tt>'&#92;u0047'</tt>
 *     <td> The upper-case variant of {@code 'g'}.
 *
 * <tr><td valign="top"> {@code 'f'}
 *     <td valign="top"> <tt>'&#92;u0066'</tt>
 *     <td> Requires the output to be formatted using <a name="bdecimal">decimal
 *     format</a>.  The <a href="#L10nAlgorithm">localization algorithm</a> is
 *     applied.
 *
 *     <p> The result is a string that represents the sign and magnitude
 *     (absolute value) of the argument.  The formatting of the sign is
 *     described in the <a href="#L10nAlgorithm">localization
 *     algorithm</a>. The formatting of the magnitude <i>m</i> depends upon its
 *     value.
 *
 *     <p> The magnitude is formatted as the integer part of <i>m</i>, with no
 *     leading zeroes, followed by the decimal separator followed by one or
 *     more decimal digits representing the fractional part of <i>m</i>.
 *
 *     <p> The number of digits in the result for the fractional part of
 *     <i>m</i> or <i>a</i> is equal to the precision. If the precision is not
 *     specified then the default value is {@code 6}.  If the precision is
 *     less than the number of digits to the right of the decimal point
 *     then the value will be rounded using the
 *     {@linkplain java.math.BigDecimal#ROUND_HALF_UP round half up
 *     algorithm}.  Otherwise, zeros may be appended to reach the precision.
 *     For a canonical representation of the value, use {@link
 *     BigDecimal#toString()}.
 *
 * </table>
 *
 * <p> All <a href="#intFlags">flags</a> defined for Byte, Short, Integer, and
 * Long apply.
 *
 * <p> If the {@code '#'} flag is given, then the decimal separator will
 * always be present.
 *
 * <p> The <a href="#floatdFlags">default behavior</a> when no flags are
 * given is the same as for Float and Double.
 *
 * <p> The specification of <a href="#floatDWidth">width</a> and <a
 * href="#floatDPrec">precision</a> is the same as defined for Float and
 * Double.
 *
 * <h4><a name="ddt">Date/Time</a></h4>
 *
 * <p> This conversion may be applied to {@code long}, {@link Long}, {@link
 * Calendar}, {@link Date} and {@link TemporalAccessor TemporalAccessor}
 *
 * <table cellpadding=5 summary="DTConv">
 *
 * <tr><td valign="top"> {@code 't'}
 *     <td valign="top"> <tt>'&#92;u0074'</tt>
 *     <td> Prefix for date and time conversion characters.
 * <tr><td valign="top"> {@code 'T'}
 *     <td valign="top"> <tt>'&#92;u0054'</tt>
 *     <td> The upper-case variant of {@code 't'}.
 *
 * </table>
 *
 * <p> The following date and time conversion character suffixes are defined
 * for the {@code 't'} and {@code 'T'} conversions.  The types are similar to
 * but not completely identical to those defined by GNU {@code date} and
 * POSIX {@code strftime(3c)}.  Additional conversion types are provided to
 * access Java-specific functionality (e.g. {@code 'L'} for milliseconds
 * within the second).
 *
 * <p> The following conversion characters are used for formatting times:
 *
 * <table cellpadding=5 summary="time">
 *
 * <tr><td valign="top"> {@code 'H'}
 *     <td valign="top"> <tt>'&#92;u0048'</tt>
 *     <td> Hour of the day for the 24-hour clock, formatted as two digits with
 *     a leading zero as necessary i.e. {@code 00 - 23}. {@code 00}
 *     corresponds to midnight.
 *
 * <tr><td valign="top">{@code 'I'}
 *     <td valign="top"> <tt>'&#92;u0049'</tt>
 *     <td> Hour for the 12-hour clock, formatted as two digits with a leading
 *     zero as necessary, i.e.  {@code 01 - 12}.  {@code 01} corresponds to
 *     one o'clock (either morning or afternoon).
 *
 * <tr><td valign="top">{@code 'k'}
 *     <td valign="top"> <tt>'&#92;u006b'</tt>
 *     <td> Hour of the day for the 24-hour clock, i.e. {@code 0 - 23}.
 *     {@code 0} corresponds to midnight.
 *
 * <tr><td valign="top">{@code 'l'}
 *     <td valign="top"> <tt>'&#92;u006c'</tt>
 *     <td> Hour for the 12-hour clock, i.e. {@code 1 - 12}.  {@code 1}
 *     corresponds to one o'clock (either morning or afternoon).
 *
 * <tr><td valign="top">{@code 'M'}
 *     <td valign="top"> <tt>'&#92;u004d'</tt>
 *     <td> Minute within the hour formatted as two digits with a leading zero
 *     as necessary, i.e.  {@code 00 - 59}.
 *
 * <tr><td valign="top">{@code 'S'}
 *     <td valign="top"> <tt>'&#92;u0053'</tt>
 *     <td> Seconds within the minute, formatted as two digits with a leading
 *     zero as necessary, i.e. {@code 00 - 60} ("{@code 60}" is a special
 *     value required to support leap seconds).
 *
 * <tr><td valign="top">{@code 'L'}
 *     <td valign="top"> <tt>'&#92;u004c'</tt>
 *     <td> Millisecond within the second formatted as three digits with
 *     leading zeros as necessary, i.e. {@code 000 - 999}.
 *
 * <tr><td valign="top">{@code 'N'}
 *     <td valign="top"> <tt>'&#92;u004e'</tt>
 *     <td> Nanosecond within the second, formatted as nine digits with leading
 *     zeros as necessary, i.e. {@code 000000000 - 999999999}.  The precision
 *     of this value is limited by the resolution of the underlying operating
 *     system or hardware.
 *
 * <tr><td valign="top">{@code 'p'}
 *     <td valign="top"> <tt>'&#92;u0070'</tt>
 *     <td> Locale-specific {@linkplain
 *     java.text.DateFormatSymbols#getAmPmStrings morning or afternoon} marker
 *     in lower case, e.g."{@code am}" or "{@code pm}".  Use of the
 *     conversion prefix {@code 'T'} forces this output to upper case.  (Note
 *     that {@code 'p'} produces lower-case output.  This is different from
 *     GNU {@code date} and POSIX {@code strftime(3c)} which produce
 *     upper-case output.)
 *
 * <tr><td valign="top">{@code 'z'}
 *     <td valign="top"> <tt>'&#92;u007a'</tt>
 *     <td> <a href="http://www.ietf.org/rfc/rfc0822.txt">RFC&nbsp;822</a>
 *     style numeric time zone offset from GMT, e.g. {@code -0800}.  This
 *     value will be adjusted as necessary for Daylight Saving Time.  For
 *     {@code long}, {@link Long}, and {@link Date} the time zone used is
 *     the {@linkplain TimeZone#getDefault() default time zone} for this
 *     instance of the Java virtual machine.
 *
 * <tr><td valign="top">{@code 'Z'}
 *     <td valign="top"> <tt>'&#92;u005a'</tt>
 *     <td> A string representing the abbreviation for the time zone.  This
 *     value will be adjusted as necessary for Daylight Saving Time.  For
 *     {@code long}, {@link Long}, and {@link Date} the time zone used is
 *     the {@linkplain TimeZone#getDefault() default time zone} for this
 *     instance of the Java virtual machine.  The Formatter's locale will
 *     supersede the locale of the argument (if any).
 *
 * <tr><td valign="top">{@code 's'}
 *     <td valign="top"> <tt>'&#92;u0073'</tt>
 *     <td> Seconds since the beginning of the epoch starting at 1 January 1970
 *     {@code 00:00:00} UTC, i.e. {@code Long.MIN_VALUE/1000} to
 *     {@code Long.MAX_VALUE/1000}.
 *
 * <tr><td valign="top">{@code 'Q'}
 *     <td valign="top"> <tt>'&#92;u004f'</tt>
 *     <td> Milliseconds since the beginning of the epoch starting at 1 January
 *     1970 {@code 00:00:00} UTC, i.e. {@code Long.MIN_VALUE} to
 *     {@code Long.MAX_VALUE}. The precision of this value is limited by
 *     the resolution of the underlying operating system or hardware.
 *
 * </table>
 *
 * <p> The following conversion characters are used for formatting dates:
 *
 * <table cellpadding=5 summary="date">
 *
 * <tr><td valign="top">{@code 'B'}
 *     <td valign="top"> <tt>'&#92;u0042'</tt>
 *     <td> Locale-specific {@linkplain java.text.DateFormatSymbols#getMonths
 *     full month name}, e.g. {@code "January"}, {@code "February"}.
 *
 * <tr><td valign="top">{@code 'b'}
 *     <td valign="top"> <tt>'&#92;u0062'</tt>
 *     <td> Locale-specific {@linkplain
 *     java.text.DateFormatSymbols#getShortMonths abbreviated month name},
 *     e.g. {@code "Jan"}, {@code "Feb"}.
 *
 * <tr><td valign="top">{@code 'h'}
 *     <td valign="top"> <tt>'&#92;u0068'</tt>
 *     <td> Same as {@code 'b'}.
 *
 * <tr><td valign="top">{@code 'A'}
 *     <td valign="top"> <tt>'&#92;u0041'</tt>
 *     <td> Locale-specific full name of the {@linkplain
 *     java.text.DateFormatSymbols#getWeekdays day of the week},
 *     e.g. {@code "Sunday"}, {@code "Monday"}
 *
 * <tr><td valign="top">{@code 'a'}
 *     <td valign="top"> <tt>'&#92;u0061'</tt>
 *     <td> Locale-specific short name of the {@linkplain
 *     java.text.DateFormatSymbols#getShortWeekdays day of the week},
 *     e.g. {@code "Sun"}, {@code "Mon"}
 *
 * <tr><td valign="top">{@code 'C'}
 *     <td valign="top"> <tt>'&#92;u0043'</tt>
 *     <td> Four-digit year divided by {@code 100}, formatted as two digits
 *     with leading zero as necessary, i.e. {@code 00 - 99}
 *
 * <tr><td valign="top">{@code 'Y'}
 *     <td valign="top"> <tt>'&#92;u0059'</tt> <td> Year, formatted to at least
 *     four digits with leading zeros as necessary, e.g. {@code 0092} equals
 *     {@code 92} CE for the Gregorian calendar.
 *
 * <tr><td valign="top">{@code 'y'}
 *     <td valign="top"> <tt>'&#92;u0079'</tt>
 *     <td> Last two digits of the year, formatted with leading zeros as
 *     necessary, i.e. {@code 00 - 99}.
 *
 * <tr><td valign="top">{@code 'j'}
 *     <td valign="top"> <tt>'&#92;u006a'</tt>
 *     <td> Day of year, formatted as three digits with leading zeros as
 *     necessary, e.g. {@code 001 - 366} for the Gregorian calendar.
 *     {@code 001} corresponds to the first day of the year.
 *
 * <tr><td valign="top">{@code 'm'}
 *     <td valign="top"> <tt>'&#92;u006d'</tt>
 *     <td> Month, formatted as two digits with leading zeros as necessary,
 *     i.e. {@code 01 - 13}, where "{@code 01}" is the first month of the
 *     year and ("{@code 13}" is a special value required to support lunar
 *     calendars).
 *
 * <tr><td valign="top">{@code 'd'}
 *     <td valign="top"> <tt>'&#92;u0064'</tt>
 *     <td> Day of month, formatted as two digits with leading zeros as
 *     necessary, i.e. {@code 01 - 31}, where "{@code 01}" is the first day
 *     of the month.
 *
 * <tr><td valign="top">{@code 'e'}
 *     <td valign="top"> <tt>'&#92;u0065'</tt>
 *     <td> Day of month, formatted as two digits, i.e. {@code 1 - 31} where
 *     "{@code 1}" is the first day of the month.
 *
 * </table>
 *
 * <p> The following conversion characters are used for formatting common
 * date/time compositions.
 *
 * <table cellpadding=5 summary="composites">
 *
 * <tr><td valign="top">{@code 'R'}
 *     <td valign="top"> <tt>'&#92;u0052'</tt>
 *     <td> Time formatted for the 24-hour clock as {@code "%tH:%tM"}
 *
 * <tr><td valign="top">{@code 'T'}
 *     <td valign="top"> <tt>'&#92;u0054'</tt>
 *     <td> Time formatted for the 24-hour clock as {@code "%tH:%tM:%tS"}.
 *
 * <tr><td valign="top">{@code 'r'}
 *     <td valign="top"> <tt>'&#92;u0072'</tt>
 *     <td> Time formatted for the 12-hour clock as {@code "%tI:%tM:%tS
 *     %Tp"}.  The location of the morning or afternoon marker
 *     ({@code '%Tp'}) may be locale-dependent.
 *
 * <tr><td valign="top">{@code 'D'}
 *     <td valign="top"> <tt>'&#92;u0044'</tt>
 *     <td> Date formatted as {@code "%tm/%td/%ty"}.
 *
 * <tr><td valign="top">{@code 'F'}
 *     <td valign="top"> <tt>'&#92;u0046'</tt>
 *     <td> <a href="http://www.w3.org/TR/NOTE-datetime">ISO&nbsp;8601</a>
 *     complete date formatted as {@code "%tY-%tm-%td"}.
 *
 * <tr><td valign="top">{@code 'c'}
 *     <td valign="top"> <tt>'&#92;u0063'</tt>
 *     <td> Date and time formatted as {@code "%ta %tb %td %tT %tZ %tY"},
 *     e.g. {@code "Sun Jul 20 16:17:00 EDT 1969"}.
 *
 * </table>
 *
 * <p> The {@code '-'} flag defined for <a href="#dFlags">General
 * conversions</a> applies.  If the {@code '#'} flag is given, then a {@link
 * FormatFlagsConversionMismatchException} will be thrown.
 *
 * <p> The width is the minimum number of characters to
 * be written to the output.  If the length of the converted value is less than
 * the {@code width} then the output will be padded by spaces
 * (<tt>'&#92;u0020'</tt>) until the total number of characters equals width.
 * The padding is on the left by default.  If the {@code '-'} flag is given
 * then the padding will be on the right.  If width is not specified then there
 * is no minimum.
 *
 * <p> The precision is not applicable.  If the precision is specified then an
 * {@link IllegalFormatPrecisionException} will be thrown.
 *
 * <h4><a name="dper">Percent</a></h4>
 *
 * <p> The conversion does not correspond to any argument.
 *
 * <table cellpadding=5 summary="DTConv">
 *
 * <tr><td valign="top">{@code '%'}
 *     <td> The result is a literal {@code '%'} (<tt>'&#92;u0025'</tt>)
 *
 * <p> The width is the minimum number of characters to
 * be written to the output including the {@code '%'}.  If the length of the
 * converted value is less than the {@code width} then the output will be
 * padded by spaces (<tt>'&#92;u0020'</tt>) until the total number of
 * characters equals width.  The padding is on the left.  If width is not
 * specified then just the {@code '%'} is output.
 *
 * <p> The {@code '-'} flag defined for <a href="#dFlags">General
 * conversions</a> applies.  If any other flags are provided, then a
 * {@link FormatFlagsConversionMismatchException} will be thrown.
 *
 * <p> The precision is not applicable.  If the precision is specified an
 * {@link IllegalFormatPrecisionException} will be thrown.
 *
 * </table>
 *
 * <h4><a name="dls">Line Separator</a></h4>
 *
 * <p> The conversion does not correspond to any argument.
 *
 * <table cellpadding=5 summary="DTConv">
 *
 * <tr><td valign="top">{@code 'n'}
 *     <td> the platform-specific line separator as returned by {@link
 *     System#getProperty System.getProperty("line.separator")}.
 *
 * </table>
 *
 * <p> Flags, width, and precision are not applicable.  If any are provided an
 * {@link IllegalFormatFlagsException}, {@link IllegalFormatWidthException},
 * and {@link IllegalFormatPrecisionException}, respectively will be thrown.
 *
 * <h4><a name="dpos">Argument Index</a></h4>
 *
 * <p> Format specifiers can reference arguments in three ways:
 *
 * <ul>
 *
 * <li> <i>Explicit indexing</i> is used when the format specifier contains an
 * argument index.  The argument index is a decimal integer indicating the
 * position of the argument in the argument list.  The first argument is
 * referenced by "{@code 1$}", the second by "{@code 2$}", etc.  An argument
 * may be referenced more than once.
 *
 * <p> For example:
 *
 * <blockquote><pre>
 *   formatter.format("%4$s %3$s %2$s %1$s %4$s %3$s %2$s %1$s",
 *                    "a", "b", "c", "d")
 *   // -&gt; "d c b a d c b a"
 * </pre></blockquote>
 *
 * <li> <i>Relative indexing</i> is used when the format specifier contains a
 * {@code '<'} (<tt>'&#92;u003c'</tt>) flag which causes the argument for
 * the previous format specifier to be re-used.  If there is no previous
 * argument, then a {@link MissingFormatArgumentException} is thrown.
 *
 * <blockquote><pre>
 *    formatter.format("%s %s %&lt;s %&lt;s", "a", "b", "c", "d")
 *    // -&gt; "a b b b"
 *    // "c" and "d" are ignored because they are not referenced
 * </pre></blockquote>
 *
 * <li> <i>Ordinary indexing</i> is used when the format specifier contains
 * neither an argument index nor a {@code '<'} flag.  Each format specifier
 * which uses ordinary indexing is assigned a sequential implicit index into
 * argument list which is independent of the indices used by explicit or
 * relative indexing.
 *
 * <blockquote><pre>
 *   formatter.format("%s %s %s %s", "a", "b", "c", "d")
 *   // -&gt; "a b c d"
 * </pre></blockquote>
 *
 * </ul>
 *
 * <p> It is possible to have a format string which uses all forms of indexing,
 * for example:
 *
 * <blockquote><pre>
 *   formatter.format("%2$s %s %&lt;s %s", "a", "b", "c", "d")
 *   // -&gt; "b a a b"
 *   // "c" and "d" are ignored because they are not referenced
 * </pre></blockquote>
 *
 * <p> The maximum number of arguments is limited by the maximum dimension of a
 * Java array as defined by
 * <cite>The Java&trade; Virtual Machine Specification</cite>.
 * If the argument index is does not correspond to an
 * available argument, then a {@link MissingFormatArgumentException} is thrown.
 *
 * <p> If there are more arguments than format specifiers, the extra arguments
 * are ignored.
 *
 * <p> Unless otherwise specified, passing a {@code null} argument to any
 * method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @author  Iris Clark
 * @since 1.5
 */
public final class Formatter implements Closeable, Flushable
{
	private Appendable a;
	private IOException lastException;

	private static double scaleUp;

	// 1 (sign) + 19 (max # sig digits) + 1 ('.') + 1 ('e') + 1 (sign)
	// + 3 (max # exp digits) + 4 (error) = 30
	private static final int MAX_FD_CHARS= 30;

	/**
	 * Returns a charset object for the given charset name.
	 * @throws NullPointerException          is csn is null
	 * @throws UnsupportedEncodingException  if the charset is not supported
	 */
	private static Charset toCharset(String csn) throws UnsupportedEncodingException
	{
		Objects.requireNonNull(csn, "charsetName");
		try
		{
			return Charset.forName(csn);
		}
		catch (IllegalCharsetNameException | UnsupportedCharsetException unused)
		{
			// UnsupportedEncodingException should be thrown
			throw new UnsupportedEncodingException(csn);
		}
	}

	private static char getZero(Locale l)
	{
		if ((l != null) && !l.equals(Locale.US))
		{
			DecimalFormatSymbols dfs= DecimalFormatSymbols.getInstance(l);
			return dfs.getZeroDigit();
		}
		else
		{
			return '0';
		}
	}

	/**
	 * Returns the destination for the output.
	 *
	 * @return  The destination for the output
	 *
	 * @throws  FormatterClosedException
	 *          If this formatter has been closed by invoking its {@link
	 *          #close()} method
	 */
	public Appendable out()
	{
		ensureOpen();
		return a;
	}

	/**
	 * Returns the result of invoking {@code toString()} on the destination
	 * for the output.  For example, the following code formats text into a
	 * {@link StringBuilder} then retrieves the resultant string:
	 *
	 * <blockquote><pre>
	 *   Formatter f = new Formatter();
	 *   f.format("Last reboot at %tc", lastRebootDate);
	 *   String s = f.toString();
	 *   // -&gt; s == "Last reboot at Sat Jan 01 00:00:00 PST 2000"
	 * </pre></blockquote>
	 *
	 * <p> An invocation of this method behaves in exactly the same way as the
	 * invocation
	 *
	 * <pre>
	 *     out().toString() </pre>
	 *
	 * <p> Depending on the specification of {@code toString} for the {@link
	 * Appendable}, the returned string may or may not contain the characters
	 * written to the destination.  For instance, buffers typically return
	 * their contents in {@code toString()}, but streams cannot since the
	 * data is discarded.
	 *
	 * @return  The result of invoking {@code toString()} on the destination
	 *          for the output
	 *
	 * @throws  FormatterClosedException
	 *          If this formatter has been closed by invoking its {@link
	 *          #close()} method
	 */
	public String toString()
	{
//		ensureOpen();
//		return a.toString();
		return "";
	}

	/**
	 * Flushes this formatter.  If the destination implements the {@link
	 * java.io.Flushable} interface, its {@code flush} method will be invoked.
	 *
	 * <p> Flushing a formatter writes any buffered output in the destination
	 * to the underlying stream.
	 *
	 * @throws  FormatterClosedException
	 *          If this formatter has been closed by invoking its {@link
	 *          #close()} method
	 */
	public void flush()
	{
		ensureOpen();
		if (a instanceof Flushable)
		{
			try
			{
				((Flushable) a).flush();
			}
			catch (IOException ioe)
			{
				lastException= ioe;
			}
		}
	}

	/**
	 * Closes this formatter.  If the destination implements the {@link
	 * java.io.Closeable} interface, its {@code close} method will be invoked.
	 *
	 * <p> Closing a formatter allows it to release resources it may be holding
	 * (such as open files).  If the formatter is already closed, then invoking
	 * this method has no effect.
	 *
	 * <p> Attempting to invoke any methods except {@link #ioException()} in
	 * this formatter after it has been closed will result in a {@link
	 * FormatterClosedException}.
	 */
	public void close()
	{
		if (a == null)
			return;
		try
		{
			if (a instanceof Closeable)
				((Closeable) a).close();
		}
		catch (IOException ioe)
		{
			lastException= ioe;
		}
		finally
		{
			a= null;
		}
	}

	private void ensureOpen()
	{
		if (a == null)
			throw new FormatterClosedException();
	}

	/**
	 * Returns the {@code IOException} last thrown by this formatter's {@link
	 * Appendable}.
	 *
	 * <p> If the destination's {@code append()} method never throws
	 * {@code IOException}, then this method will always return {@code null}.
	 *
	 * @return  The last exception thrown by the Appendable or {@code null} if
	 *          no such exception exists.
	 */
	public IOException ioException()
	{
		return lastException;
	}

	/**
	 * Writes a formatted string to this object's destination using the
	 * specified format string and arguments.  The locale used is the one
	 * defined during the construction of this formatter.
	 *
	 * @param  format
	 *         A format string as described in <a href="#syntax">Format string
	 *         syntax</a>.
	 *
	 * @param  args
	 *         Arguments referenced by the format specifiers in the format
	 *         string.  If there are more arguments than format specifiers, the
	 *         extra arguments are ignored.  The maximum number of arguments is
	 *         limited by the maximum dimension of a Java array as defined by
	 *         <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *
	 * @throws  IllegalFormatException
	 *          If a format string contains an illegal syntax, a format
	 *          specifier that is incompatible with the given arguments,
	 *          insufficient arguments given the format string, or other
	 *          illegal conditions.  For specification of all possible
	 *          formatting errors, see the <a href="#detail">Details</a>
	 *          section of the formatter class specification.
	 *
	 * @throws  FormatterClosedException
	 *          If this formatter has been closed by invoking its {@link
	 *          #close()} method
	 *
	 * @return  This formatter
	 */
	public Formatter format(String format, Object... args)
	{
		return format(new Locale(""), format, args);
	}

	/**
	 * Writes a formatted string to this object's destination using the
	 * specified locale, format string, and arguments.
	 *
	 * @param  l
	 *         The {@linkplain java.util.Locale locale} to apply during
	 *         formatting.  If {@code l} is {@code null} then no localization
	 *         is applied.  This does not change this object's locale that was
	 *         set during construction.
	 *
	 * @param  format
	 *         A format string as described in <a href="#syntax">Format string
	 *         syntax</a>
	 *
	 * @param  args
	 *         Arguments referenced by the format specifiers in the format
	 *         string.  If there are more arguments than format specifiers, the
	 *         extra arguments are ignored.  The maximum number of arguments is
	 *         limited by the maximum dimension of a Java array as defined by
	 *         <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *
	 * @throws  IllegalFormatException
	 *          If a format string contains an illegal syntax, a format
	 *          specifier that is incompatible with the given arguments,
	 *          insufficient arguments given the format string, or other
	 *          illegal conditions.  For specification of all possible
	 *          formatting errors, see the <a href="#detail">Details</a>
	 *          section of the formatter class specification.
	 *
	 * @throws  FormatterClosedException
	 *          If this formatter has been closed by invoking its {@link
	 *          #close()} method
	 *
	 * @return  This formatter
	 */
	public Formatter format(Locale l, String format, Object... args)
	{
//		ensureOpen();
//
//		// index of last argument referenced
//		int last= -1;
//		// last ordinary index
//		int lasto= -1;
//
//		FormatString[] fsa= parse(format);
//		for (int i= 0; i < fsa.length; i++)
//		{
//			FormatString fs= fsa[i];
//			int index= fs.index();
//			try
//			{
//				switch (index)
//				{
//					case -2: // fixed string, "%n", or "%%"
//						fs.print(null, l);
//						break;
//					case -1: // relative index
//						if (last < 0 || (args != null && last > args.length - 1))
//							throw new MissingFormatArgumentException(fs.toString());
//						fs.print((args == null ? null : args[last]), l);
//						break;
//					case 0: // ordinary index
//						lasto++;
//						last= lasto;
//						if (args != null && lasto > args.length - 1)
//							throw new MissingFormatArgumentException(fs.toString());
//						fs.print((args == null ? null : args[lasto]), l);
//						break;
//					default: // explicit index
//						last= index - 1;
//						if (args != null && last > args.length - 1)
//							throw new MissingFormatArgumentException(fs.toString());
//						fs.print((args == null ? null : args[last]), l);
//						break;
//				}
//			}
//			catch (IOException x)
//			{
//				lastException= x;
//			}
//		}
		return this;
	}

	// %[argument_index$][flags][width][.precision][t]conversion
	private static final String formatSpecifier= "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";

	private static Pattern fsPattern= Pattern.compile(formatSpecifier);

	/**
	 * Finds format specifiers in the format string.
	 */
	private FormatString[] parse(String s)
	{
		return new FormatString[0];
	}

	private static void checkText(String s, int start, int end)
	{
		for (int i= start; i < end; i++)
		{
			// Any '%' found in the region starts an invalid format specifier.
			if (s.charAt(i) == '%')
			{
				char c= (i == end - 1) ? '%' : s.charAt(i + 1);
				throw new UnknownFormatConversionException(String.valueOf(c));
			}
		}
	}

	private interface FormatString
	{
		int index();
		void print(Object arg, Locale l) throws IOException;
		String toString();
	}

	private class FixedString implements FormatString
	{
		private String s;

		FixedString(String s)
		{
			this.s= s;
		}
		public int index()
		{
			return -2;
		}
		public void print(Object arg, Locale l) throws IOException
		{
			a.append(s);
		}
		public String toString()
		{
			return s;
		}
	}

	/**
	 * Enum for {@code BigDecimal} formatting.
	 */
	public enum BigDecimalLayoutForm
	{
		/**
		 * Format the {@code BigDecimal} in computerized scientific notation.
		 */
		SCIENTIFIC,

		/**
		 * Format the {@code BigDecimal} as a decimal number.
		 */
		DECIMAL_FLOAT
	};

	private static class Flags
	{
		private int flags;

		static final Flags NONE= new Flags(0); // ''

		// duplicate declarations from Formattable.java
		static final Flags LEFT_JUSTIFY= new Flags(1 << 0); // '-'
		static final Flags UPPERCASE= new Flags(1 << 1); // '^'
		static final Flags ALTERNATE= new Flags(1 << 2); // '#'

		// numerics
		static final Flags PLUS= new Flags(1 << 3); // '+'
		static final Flags LEADING_SPACE= new Flags(1 << 4); // ' '
		static final Flags ZERO_PAD= new Flags(1 << 5); // '0'
		static final Flags GROUP= new Flags(1 << 6); // ','
		static final Flags PARENTHESES= new Flags(1 << 7); // '('

		// indexing
		static final Flags PREVIOUS= new Flags(1 << 8); // '<'

		private Flags(int f)
		{
			flags= f;
		}

		public int valueOf()
		{
			return flags;
		}

		public boolean contains(Flags f)
		{
			return (flags & f.valueOf()) == f.valueOf();
		}

		public Flags dup()
		{
			return new Flags(flags);
		}

		private Flags add(Flags f)
		{
			flags|= f.valueOf();
			return this;
		}

		public Flags remove(Flags f)
		{
			flags&= ~f.valueOf();
			return this;
		}

		public static Flags parse(String s)
		{
			char[] ca= s.toCharArray();
			Flags f= new Flags(0);
			for (int i= 0; i < ca.length; i++)
			{
				Flags v= parse(ca[i]);
				if (f.contains(v))
					throw new DuplicateFormatFlagsException(v.toString());
				f.add(v);
			}
			return f;
		}

		// parse those flags which may be provided by users
		private static Flags parse(char c)
		{
			switch (c)
			{
				case '-':
					return LEFT_JUSTIFY;
				case '#':
					return ALTERNATE;
				case '+':
					return PLUS;
				case ' ':
					return LEADING_SPACE;
				case '0':
					return ZERO_PAD;
				case ',':
					return GROUP;
				case '(':
					return PARENTHESES;
				case '<':
					return PREVIOUS;
				default:
					throw new UnknownFormatFlagsException(String.valueOf(c));
			}
		}

		// Returns a string representation of the current {@code Flags}.
		public static String toString(Flags f)
		{
			return f.toString();
		}

		public String toString()
		{
			StringBuilder sb= new StringBuilder();
			if (contains(LEFT_JUSTIFY))
				sb.append('-');
			if (contains(UPPERCASE))
				sb.append('^');
			if (contains(ALTERNATE))
				sb.append('#');
			if (contains(PLUS))
				sb.append('+');
			if (contains(LEADING_SPACE))
				sb.append(' ');
			if (contains(ZERO_PAD))
				sb.append('0');
			if (contains(GROUP))
				sb.append(',');
			if (contains(PARENTHESES))
				sb.append('(');
			if (contains(PREVIOUS))
				sb.append('<');
			return sb.toString();
		}
	}

	private static class Conversion
	{
		// Byte, Short, Integer, Long, BigInteger
		// (and associated primitives due to autoboxing)
		static final char DECIMAL_INTEGER= 'd';
		static final char OCTAL_INTEGER= 'o';
		static final char HEXADECIMAL_INTEGER= 'x';
		static final char HEXADECIMAL_INTEGER_UPPER= 'X';

		// Float, Double, BigDecimal
		// (and associated primitives due to autoboxing)
		static final char SCIENTIFIC= 'e';
		static final char SCIENTIFIC_UPPER= 'E';
		static final char GENERAL= 'g';
		static final char GENERAL_UPPER= 'G';
		static final char DECIMAL_FLOAT= 'f';
		static final char HEXADECIMAL_FLOAT= 'a';
		static final char HEXADECIMAL_FLOAT_UPPER= 'A';

		// Character, Byte, Short, Integer
		// (and associated primitives due to autoboxing)
		static final char CHARACTER= 'c';
		static final char CHARACTER_UPPER= 'C';

		// java.util.Date, java.util.Calendar, long
		static final char DATE_TIME= 't';
		static final char DATE_TIME_UPPER= 'T';

		// if (arg.TYPE != boolean) return boolean
		// if (arg != null) return true; else return false;
		static final char BOOLEAN= 'b';
		static final char BOOLEAN_UPPER= 'B';
		// if (arg instanceof Formattable) arg.formatTo()
		// else arg.toString();
		static final char STRING= 's';
		static final char STRING_UPPER= 'S';
		// arg.hashCode()
		static final char HASHCODE= 'h';
		static final char HASHCODE_UPPER= 'H';

		static final char LINE_SEPARATOR= 'n';
		static final char PERCENT_SIGN= '%';

		static boolean isValid(char c)
		{
			return (isGeneral(c) || isInteger(c) || isFloat(c) || isText(c) || c == 't' || isCharacter(c));
		}

		// Returns true iff the Conversion is applicable to all objects.
		static boolean isGeneral(char c)
		{
			switch (c)
			{
				case BOOLEAN:
				case BOOLEAN_UPPER:
				case STRING:
				case STRING_UPPER:
				case HASHCODE:
				case HASHCODE_UPPER:
					return true;
				default:
					return false;
			}
		}

		// Returns true iff the Conversion is applicable to character.
		static boolean isCharacter(char c)
		{
			switch (c)
			{
				case CHARACTER:
				case CHARACTER_UPPER:
					return true;
				default:
					return false;
			}
		}

		// Returns true iff the Conversion is an integer type.
		static boolean isInteger(char c)
		{
			switch (c)
			{
				case DECIMAL_INTEGER:
				case OCTAL_INTEGER:
				case HEXADECIMAL_INTEGER:
				case HEXADECIMAL_INTEGER_UPPER:
					return true;
				default:
					return false;
			}
		}

		// Returns true iff the Conversion is a floating-point type.
		static boolean isFloat(char c)
		{
			switch (c)
			{
				case SCIENTIFIC:
				case SCIENTIFIC_UPPER:
				case GENERAL:
				case GENERAL_UPPER:
				case DECIMAL_FLOAT:
				case HEXADECIMAL_FLOAT:
				case HEXADECIMAL_FLOAT_UPPER:
					return true;
				default:
					return false;
			}
		}

		// Returns true iff the Conversion does not require an argument
		static boolean isText(char c)
		{
			switch (c)
			{
				case LINE_SEPARATOR:
				case PERCENT_SIGN:
					return true;
				default:
					return false;
			}
		}
	}

	private static class DateTime
	{
		static final char HOUR_OF_DAY_0= 'H'; // (00 - 23)
		static final char HOUR_0= 'I'; // (01 - 12)
		static final char HOUR_OF_DAY= 'k'; // (0 - 23) -- like H
		static final char HOUR= 'l'; // (1 - 12) -- like I
		static final char MINUTE= 'M'; // (00 - 59)
		static final char NANOSECOND= 'N'; // (000000000 - 999999999)
		static final char MILLISECOND= 'L'; // jdk, not in gnu (000 - 999)
		static final char MILLISECOND_SINCE_EPOCH= 'Q'; // (0 - 99...?)
		static final char AM_PM= 'p'; // (am or pm)
		static final char SECONDS_SINCE_EPOCH= 's'; // (0 - 99...?)
		static final char SECOND= 'S'; // (00 - 60 - leap second)
		static final char TIME= 'T'; // (24 hour hh:mm:ss)
		static final char ZONE_NUMERIC= 'z'; // (-1200 - +1200) - ls minus?
		static final char ZONE= 'Z'; // (symbol)

		// Date
		static final char NAME_OF_DAY_ABBREV= 'a'; // 'a'
		static final char NAME_OF_DAY= 'A'; // 'A'
		static final char NAME_OF_MONTH_ABBREV= 'b'; // 'b'
		static final char NAME_OF_MONTH= 'B'; // 'B'
		static final char CENTURY= 'C'; // (00 - 99)
		static final char DAY_OF_MONTH_0= 'd'; // (01 - 31)
		static final char DAY_OF_MONTH= 'e'; // (1 - 31) -- like d
		// *    static final char ISO_WEEK_OF_YEAR_2    = 'g'; // cross %y %V
		// *    static final char ISO_WEEK_OF_YEAR_4    = 'G'; // cross %Y %V
		static final char NAME_OF_MONTH_ABBREV_X= 'h'; // -- same b
		static final char DAY_OF_YEAR= 'j'; // (001 - 366)
		static final char MONTH= 'm'; // (01 - 12)
		// *    static final char DAY_OF_WEEK_1         = 'u'; // (1 - 7) Monday
		// *    static final char WEEK_OF_YEAR_SUNDAY   = 'U'; // (0 - 53) Sunday+
		// *    static final char WEEK_OF_YEAR_MONDAY_01 = 'V'; // (01 - 53) Monday+
		// *    static final char DAY_OF_WEEK_0         = 'w'; // (0 - 6) Sunday
		// *    static final char WEEK_OF_YEAR_MONDAY   = 'W'; // (00 - 53) Monday
		static final char YEAR_2= 'y'; // (00 - 99)
		static final char YEAR_4= 'Y'; // (0000 - 9999)

		// Composites
		static final char TIME_12_HOUR= 'r'; // (hh:mm:ss [AP]M)
		static final char TIME_24_HOUR= 'R'; // (hh:mm same as %H:%M)
		// *    static final char LOCALE_TIME   = 'X'; // (%H:%M:%S) - parse format?
		static final char DATE_TIME= 'c';
		// (Sat Nov 04 12:02:33 EST 1999)
		static final char DATE= 'D'; // (mm/dd/yy)
		static final char ISO_STANDARD_DATE= 'F'; // (%Y-%m-%d)
		// *    static final char LOCALE_DATE           = 'x'; // (mm/dd/yy)

		static boolean isValid(char c)
		{
			switch (c)
			{
				case HOUR_OF_DAY_0:
				case HOUR_0:
				case HOUR_OF_DAY:
				case HOUR:
				case MINUTE:
				case NANOSECOND:
				case MILLISECOND:
				case MILLISECOND_SINCE_EPOCH:
				case AM_PM:
				case SECONDS_SINCE_EPOCH:
				case SECOND:
				case TIME:
				case ZONE_NUMERIC:
				case ZONE:

					// Date
				case NAME_OF_DAY_ABBREV:
				case NAME_OF_DAY:
				case NAME_OF_MONTH_ABBREV:
				case NAME_OF_MONTH:
				case CENTURY:
				case DAY_OF_MONTH_0:
				case DAY_OF_MONTH:
					// *        case ISO_WEEK_OF_YEAR_2:
					// *        case ISO_WEEK_OF_YEAR_4:
				case NAME_OF_MONTH_ABBREV_X:
				case DAY_OF_YEAR:
				case MONTH:
					// *        case DAY_OF_WEEK_1:
					// *        case WEEK_OF_YEAR_SUNDAY:
					// *        case WEEK_OF_YEAR_MONDAY_01:
					// *        case DAY_OF_WEEK_0:
					// *        case WEEK_OF_YEAR_MONDAY:
				case YEAR_2:
				case YEAR_4:

					// Composites
				case TIME_12_HOUR:
				case TIME_24_HOUR:
					// *        case LOCALE_TIME:
				case DATE_TIME:
				case DATE:
				case ISO_STANDARD_DATE:
					// *        case LOCALE_DATE:
					return true;
				default:
					return false;
			}
		}
	}
}
