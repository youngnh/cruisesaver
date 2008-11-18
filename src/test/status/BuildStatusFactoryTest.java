package status;

import org.junit.Assert;
import org.junit.Test;

import status.CCSettings.Protocol;


public class BuildStatusFactoryTest {

	@Test
	public void testCreateBuildStatusGivesJMXFlaverAsRequested() throws Exception {
		BuildStatus createBuildStatus = BuildStatusFactory.createBuildStatus(Protocol.JMX, "host", "1234");
		
		Assert.assertTrue(createBuildStatus instanceof BuildStatusJMX);
		
	}
	
	@Test
	public void testCreateBuildStatusGivesRSSFlaverAsRequested() throws Exception {
		BuildStatus createBuildStatus = BuildStatusFactory.createBuildStatus(Protocol.RSS, "host", "1234");
		
		Assert.assertTrue(createBuildStatus instanceof BuildStatusRSS);
		
	}

	@Test
	public void testCreateBuildStatusGivesDebugFlaverAsRequested() throws Exception {
		BuildStatus createBuildStatus = BuildStatusFactory.createBuildStatus(Protocol.RSS, "debug", "1234");
		
		Assert.assertTrue(createBuildStatus instanceof BuildStatusDebug);
		
	}
	
}
