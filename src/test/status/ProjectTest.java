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
package status;

import org.junit.Assert;
import org.junit.Test;

public class ProjectTest {

	@Test
	public void testGetBuildStatus() {
		Project p = new Project("test");

		Assert.assertFalse(p.isLastBuildSuccesful());
		Assert.assertEquals(Project.StatusEnum.UNKNOWN, p.getBuildStatus());

		p.setLastBuild("now");
		p.setLastSuccessfulBuild("then");

		Assert.assertEquals(Project.StatusEnum.BAD, p.getBuildStatus());

		p.setLastBuildSuccesful(true);
		Assert.assertEquals(Project.StatusEnum.GOOD, p.getBuildStatus());

	}

	@Test
	public void testIsWaitingReturnsTrueForValueFromRSS() throws Exception {
		Project testObject = new Project("test");
		testObject.setStatus("Build passed");

		Assert.assertTrue(testObject.isWaiting());
	}

	@Test
	public void testIsWaitingReturnsTrueForValueFromJMX() throws Exception {
		Project testObject = new Project("test");
		testObject.setStatus("waiting");

		Assert.assertTrue(testObject.isWaiting());
	}

	@Test
	public void testGetLabelIsNameWhenIsWaiting() throws Exception {
		String projectName = "test";
		Project testObject = new Project(projectName);
		testObject.setStatus("waiting");

		Assert.assertEquals(projectName, testObject.getLabel());

		testObject.setStatus("Build passed");
		Assert.assertEquals(projectName, testObject.getLabel());
	}

	@Test
	public void testGetLabelIsNameAndQueueDepthWhenStatusIsQueued() throws Exception {
		String projectName = "test";
		String queuedStatusAsFromJMX = "in build queue - IDLE[ 1 / 1 ]";
		Project testObject = new Project(projectName);
		testObject.setStatus(queuedStatusAsFromJMX);

		Assert.assertEquals("test:queued [ 1 / 1 ]", testObject.getLabel());
	}

	@Test
	public void testGetLabelIsNameAndBuildingWhenStatusIsBuilding() throws Exception {
		String projectName = "test";
		Project testObject = new Project(projectName);
		testObject.setStatus("building");

		Assert.assertEquals("test-building", testObject.getLabel());
	}
}
