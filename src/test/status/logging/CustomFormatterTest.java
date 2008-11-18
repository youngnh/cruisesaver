// Copyright (c) 2008 James A. Wilson All rights reserved. Use is
// subject to license terms.

// This file is part of CruiseSaver.
//
// CruiseSaver is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// CruiseSaver is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with CruiseSaver; if not, write to the Free Software
// Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
package status.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.Test;

public class CustomFormatterTest {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Test
	public void testFormatWithException() throws Exception {
		CustomFormatter formatter = new CustomFormatter();

		Exception ex = new Exception("test exception");
		LogRecord logRecord = new LogRecord(Level.INFO, "test message");
		logRecord.setThrown(ex);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(byteArrayOutputStream);
		ex.printStackTrace(stream);
		SimpleDateFormat format = new SimpleDateFormat(CustomFormatter.format);
		String expected = format.format(new Date(logRecord.getMillis()))
				+ " null INFO: test message" + LINE_SEPARATOR + byteArrayOutputStream.toString();

		String actual = formatter.format(logRecord);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testFormatUsesLoggerNameNotClassName() throws Exception {
		CustomFormatter testObject = new CustomFormatter();

		String loggerName = "loggername";
		String sourceClassName = "sourceclassname";
		String sourceMethodName = "sourcemethodname";
		String msg = "foo";
		LogRecord logRecord = new LogRecord(Level.FINE, msg);
		logRecord.setLoggerName(loggerName);
		logRecord.setSourceClassName(sourceClassName);
		logRecord.setSourceMethodName(sourceMethodName);

		String expected = loggerName + " (" + sourceMethodName + ") FINE: " + msg + LINE_SEPARATOR;
		String actual = testObject.format(logRecord);

		Assert.assertTrue(actual + " ends with " + expected, actual.endsWith(expected));
	}
}
