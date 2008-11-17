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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

public class LogUtilTest {

	@Test
	public void testStaticInitializer() throws Exception {
		Logger logger = Logger.getLogger(LogUtil.BASE_NAME);
		Handler[] handlers = logger.getHandlers();

		LogUtil.getLogger(this.getClass());
		handlers = logger.getHandlers();
		Assert.assertEquals(1, handlers.length);
		Assert.assertTrue(handlers[0] instanceof ConfigurableConsoleHandler);
		
		Logger rootLogger = Logger.getLogger("");
		handlers = rootLogger.getHandlers();
		Assert.assertEquals(0, rootLogger.getHandlers().length);
	}

	@Test
	public void testSetDebug() throws Exception {
		Logger logger = Logger.getLogger(LogUtil.BASE_NAME);
		Assert.assertEquals(LogUtil.DEFAULT_LEVEL, logger.getLevel());

		LogUtil.setDebug(true);
		Assert.assertEquals(Level.FINE, logger.getLevel());
		Handler[] handlers = logger.getHandlers();
		Assert.assertEquals(2, handlers.length);
		Assert.assertTrue(handlers[0] instanceof ConfigurableConsoleHandler);
		Assert.assertTrue(handlers[1] instanceof FileHandler);

		LogUtil.setDebug(false);

		Assert.assertEquals(LogUtil.DEFAULT_LEVEL, logger.getLevel());
		handlers = logger.getHandlers();
		Assert.assertEquals(1, handlers.length);
		Assert.assertTrue(handlers[0] instanceof ConfigurableConsoleHandler);
	}
	
	@Test
	public void testLog() throws Exception {
		Logger logger = LogUtil.getLogger(this.getClass());
		MockHandler mockHandler = new MockHandler();
		
		logger.addHandler(mockHandler);
		LogUtil.log(logger, Level.SEVERE, "string", 1, new Double(3.4),new Exception("foo"));
		Thread.sleep(5);
		Assert.assertEquals("string13.4java.lang.Exception: foo", mockHandler.logList.get(0).getMessage());
	}
	
	static class MockHandler extends Handler {
		List<LogRecord> logList = new ArrayList<LogRecord>();
		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(LogRecord record) {
			logList.add(record);
		}
		
	}
}
