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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class CustomFormatter extends SimpleFormatter {

	Date date = new Date();
	protected final static String format = "yyyy.MM.dd HH:mm:ss ";
	private SimpleDateFormat formatter;
	String lineSep = System.getProperty("line.separator");

	@Override
	public synchronized String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		// Minimize memory allocations here.
		date.setTime(record.getMillis());
		if (formatter == null) {
			formatter = new SimpleDateFormat(format);
		}
		sb.append(formatter.format(date));
		sb.append(" ");
		if (record.getLoggerName() != null) {
			sb.append(record.getLoggerName());
		} else {
			sb.append(record.getSourceClassName());
		}
		if (record.getSourceMethodName() != null) {
			sb.append(" (");
			sb.append(record.getSourceMethodName());
			sb.append(")");
		}
		sb.append(" ");
		String message = formatMessage(record);
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": ");
		sb.append(message);

		sb.append(lineSep);
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ignored) {
				// ignored
			}
		}
		return sb.toString();
	}
}
