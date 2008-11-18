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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jdesktop.jdic.screensaver.ScreensaverSettings;

import status.logging.LogUtil;

public class CCSettings {
	private static final Logger logger = LogUtil.getLogger(CCSettings.class);
	static final String DEBUG_HOST = "DEBUG";

	private int ballSize = 30;
	private String hostName = DEBUG_HOST;
	private String portNumber = "5";
	private int fontSize = 10;
	private int updateDelaySeconds = 10;
	private Protocol protocol;

	public static enum Protocol {
		RSS, JMX
	}

	public CCSettings(ScreensaverSettings settings) {

		String tmp = settings.getProperty("HOST");
		if (tmp != null && tmp.length() > 0) {
			hostName = tmp;
		}
		tmp = settings.getProperty("PORT");
		if (tmp != null && tmp.length() > 0) {
			portNumber = tmp;
		}
		tmp = settings.getProperty("SIZE");
		if (tmp != null && tmp.length() > 0) {
			ballSize = Integer.parseInt(tmp);
		}
		tmp = settings.getProperty("UPDATEDELAY");
		if (tmp != null && tmp.length() > 0) {
			updateDelaySeconds = Integer.parseInt(tmp);
		}
		tmp = settings.getProperty("FONT_SIZE");
		if (tmp != null && tmp.length() > 0) {
			fontSize = Integer.parseInt(tmp);
		}
		tmp = settings.getProperty("DEBUG");
		if (tmp != null && tmp.length() > 0) {
			LogUtil.setDebug(Boolean.parseBoolean(tmp));
			logger.info("debug mode is requested");
		}

		tmp = settings.getProperty("PROTOCOL");
		if (tmp != null && tmp.length() > 0) {
			protocol = Protocol.valueOf(tmp);
		} else {
			protocol = Protocol.JMX;
		}

		if (hostName.equalsIgnoreCase(DEBUG_HOST)) {
			LogUtil.setDebug(true);
		}

	}

	public int getBallSize() {
		return ballSize;
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getHostName() {
		return hostName;
	}

	public String getPortNumber() {
		return portNumber;
	}

	public int getUpdateDelaySeconds() {
		return updateDelaySeconds;
	}

	public List<BuildStatus> getBuildStatus() {
		List<BuildStatus> result = new ArrayList<BuildStatus>(5);
		String[] hosts = getHostName().split(",");
		String[] ports = getPortNumber().split(",");
		if (hosts.length != ports.length) {
			String message = "hosts and ports not of equal length comma separated strings.";
			logger.severe(message);
			throw new IllegalArgumentException(message);
		}
		for (int i = 0; i < hosts.length; ++i) {
			result.add(BuildStatusFactory.createBuildStatus(protocol, hosts[i], ports[i]));
		}

		return result;
	}

}
