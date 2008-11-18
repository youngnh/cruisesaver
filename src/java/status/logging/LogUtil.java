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

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
	protected static final String BASE_NAME = "status";// LogUtil.class.getPackage().getName();

	protected static final Level DEFAULT_LEVEL = Level.WARNING;

	protected static FileHandler fileHandler;

	static {
		Logger logger = Logger.getLogger(BASE_NAME);
		logger.setLevel(DEFAULT_LEVEL);
		logger.addHandler(new ConfigurableConsoleHandler());

		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			Handler handler = handlers[i];
			if (handler instanceof ConsoleHandler) {
				rootLogger.removeHandler(handler);
			}
		}

		try {
			fileHandler = new FileHandler("%t/cruisesaver%g.log", 100 * 1024, 3, true);
			fileHandler.setFormatter(new CustomFormatter());
		} catch (Exception e) {
			System.err.println("could not add file handler: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public static void setDebug(boolean debug) {
		Logger logger = Logger.getLogger(BASE_NAME);
		if (debug) {
			logger.setLevel(Level.FINE);
			if (fileHandler != null)
				logger.addHandler(fileHandler);
		} else {
			logger.setLevel(DEFAULT_LEVEL);
			if (fileHandler != null)
				logger.removeHandler(fileHandler);
		}
	}

	public static Logger getLogger(Class<?> c) {
		return Logger.getLogger(c.getName());
	}

	public static void log(Logger logger, Level level, Object... objects) {
		if (logger.isLoggable(level)) {
			StringBuilder sb = new StringBuilder();
			for (Object object : objects) {
				sb.append(object.toString());
			}
			logger.log(level, sb.toString());
		}
	}
}
