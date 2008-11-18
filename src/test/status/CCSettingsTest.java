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

import java.util.List;

import org.jdesktop.jdic.screensaver.ScreensaverSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CCSettingsTest {

	private ScreensaverSettings screenSaverSettings;

	@Before
	public void setUp() throws Exception {
		screenSaverSettings = new ScreensaverSettings();
	}

	@Test
	public void testConstructorForJMX() throws Exception {
		String commandLine = "-HOST localHost -PORT 109 -PROTOCOL JMX -SIZE 100 -DEBUG false -UPDATEDELAY 20 -FONT_SIZE 12";
		screenSaverSettings.loadFromCommandline(commandLine);

		CCSettings settings = new CCSettings(screenSaverSettings);

		Assert.assertEquals("localHost", settings.getHostName());
		Assert.assertEquals("109", settings.getPortNumber());
		Assert.assertEquals(20, settings.getUpdateDelaySeconds());
		Assert.assertEquals(12, settings.getFontSize());
		Assert.assertTrue(settings.getBuildStatus().getClass().getSimpleName(), settings
				.getBuildStatus().get(0) instanceof BuildStatusJMX);

		commandLine = "-HOST localHost -PORT 109 -SIZE 100 -DEBUG false -UPDATEDELAY 20 -FONT_SIZE 12";
		screenSaverSettings.loadFromCommandline(commandLine);
		settings = new CCSettings(screenSaverSettings);
		Assert.assertTrue(settings.getBuildStatus().get(0) instanceof BuildStatusJMX);
	}

	@Test
	public void testConstructorForRSS() throws Exception {
		String commandLine = "-HOST localHost -PORT 109 -PROTOCOL RSS -SIZE 100 -DEBUG false -UPDATEDELAY 20 -FONT_SIZE 12";
		screenSaverSettings.loadFromCommandline(commandLine);

		CCSettings settings = new CCSettings(screenSaverSettings);

		Assert.assertEquals("localHost", settings.getHostName());
		Assert.assertEquals("109", settings.getPortNumber());
		Assert.assertEquals(20, settings.getUpdateDelaySeconds());
		Assert.assertEquals(12, settings.getFontSize());
		Assert.assertTrue(settings.getBuildStatus().get(0) instanceof BuildStatusRSS);
	}

	@Test
	public void testConstructorMultipleSettings() throws Exception {
		String commandLine = "-HOST localHost,debug,johost -PORT 109,110,111 -SIZE 100 -DEBUG false -UPDATEDELAY 20 -FONT_SIZE 12";
		screenSaverSettings.loadFromCommandline(commandLine);

		CCSettings settings = new CCSettings(screenSaverSettings);

		Assert.assertEquals("localHost,debug,johost", settings.getHostName());
		Assert.assertEquals("109,110,111", settings.getPortNumber());
		Assert.assertEquals(12, settings.getFontSize());

		List<BuildStatus> status = settings.getBuildStatus();
		Assert.assertEquals(3, status.size());
		Assert.assertTrue(status.get(1) instanceof BuildStatusDebug);
	}

	@Test
	public void testConstructorAppliesDefaults() throws Exception {
		screenSaverSettings.loadFromCommandline("");

		CCSettings settings = new CCSettings(screenSaverSettings);

		Assert.assertEquals(30, settings.getBallSize());
		Assert.assertEquals(10, settings.getFontSize());
		Assert.assertEquals(CCSettings.DEBUG_HOST, settings.getHostName());
		Assert.assertEquals("5", settings.getPortNumber());
		Assert.assertEquals(10, settings.getUpdateDelaySeconds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testHostPortNotEqualNumberOfEntries() throws Exception {
		String commandLine = "-HOST localHost,foo,debug,johost -PORT 109,110,111 -SIZE 100 -DEBUG false -UPDATEDELAY 20 -FONT_SIZE 12";
		screenSaverSettings.loadFromCommandline(commandLine);

		CCSettings settings = new CCSettings(screenSaverSettings);

		settings.getBuildStatus();

	}

	@Test
	public void testGetDelayIsSetInSecondsReturnedInMilliseconds() throws Exception {
		String commandLine = "-HOST debug -PORT 10 -SIZE 100 -DEBUG false -UPDATEDELAY 20 -FONT_SIZE 12";
		screenSaverSettings.loadFromCommandline(commandLine);

		CCSettings settings = new CCSettings(screenSaverSettings);

		Assert.assertEquals(20, settings.getUpdateDelaySeconds());

	}
}
