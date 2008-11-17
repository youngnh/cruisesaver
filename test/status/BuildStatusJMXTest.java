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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;

import status.logging.LogUtil;

public class BuildStatusJMXTest {

	@Test
	public void testDefaultConstructor() throws Exception {
		BuildStatusJMX status = new BuildStatusJMX();
		String expected = "[BuildStatusJMX: localhost:1099]";
		Assert.assertEquals(expected, status.toString());
	}

	@Test
	public void testConstructor() throws Exception {
		BuildStatusJMX status = new BuildStatusJMX("LocalHost", "123");
		String expected = "[BuildStatusJMX: LocalHost:123]";
		Assert.assertEquals(expected, status.toString());
	}

	@Test
	public void testSuccessfullJMXCall() throws Exception {
		LogUtil.setDebug(true);
		LogUtil.getLogger(BuildStatusJMX.class).setLevel(Level.FINEST);
		BuildStatusJMX status = new BuildStatusJMX("localhost", "1099");

		List<Project> actual = status.getProjects();
		List<Project> expected = Arrays.asList(new Project("connectfour"));

		Assert
				.assertEquals(
						"if failed, is CruiseControl running with -rmiport 1099 and patched with Jira CC493?",
						expected, actual);
		Assert.assertEquals(true, actual.get(0).isLastBuildSuccesful());
		Assert.assertEquals(false, actual.get(0).isBuilding());

	}

}
