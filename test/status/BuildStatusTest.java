package status;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import status.Project.StatusEnum;

public class BuildStatusTest {

	@Test
	public void testProjectsCreatedWithProperAttributesForOrbLabelingFromJMX() throws Exception {
		BuildStatus testObject = new BuildStatusJMX("localhost", "1099");
		assertConnectFourProject(testObject, 300000, "waiting");
	}

	@Test
	public void testProjectsCreatedWithProperAttributesForOrbLabelingFromRSS() throws Exception {
		BuildStatus testObject = new BuildStatusRSS("localhost", "8080");
		assertConnectFourProject(testObject, 0, "waiting");
	}

	private void assertConnectFourProject(BuildStatus testObject, int buildInterval, String status) {
		List<Project> projects = testObject.getProjects();

		Project actual = projects.get(0);

		Assert.assertEquals("connectfour", actual.getName());
		Assert.assertEquals(buildInterval, actual.getBuildInterval());
		Assert.assertEquals(StatusEnum.GOOD, actual.getBuildStatus());
		Assert.assertEquals(null, actual.getPaused());
		Assert.assertEquals(status, actual.getStatus());
		Assert.assertTrue(actual.isWaiting());
	}
}
