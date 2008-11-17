package status.logging;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurableConsoleHandlerTest {

	
	@Test
	public void testRecordsAreLogged() throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CustomFormatter formatter = new CustomFormatter();
		ConfigurableConsoleHandler handler = new ConfigurableConsoleHandler(stream, formatter);
		
		Logger logger = LogUtil.getLogger(this.getClass());
		logger.addHandler(handler);
		
		LogRecord logRecord = new LogRecord(Level.WARNING, "warning message");
		
		logger.log(logRecord);
		
		Thread.yield();
		
		Assert.assertEquals(formatter.format(logRecord), stream.toString());
	}
}
