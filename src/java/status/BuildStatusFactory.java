package status;

import status.CCSettings.Protocol;

public class BuildStatusFactory {

	public static BuildStatus createBuildStatus(Protocol protocol, String hostName, String port) {
		if (hostName.toLowerCase().equals("debug")) {
			return new BuildStatusDebug(port);
		}
		switch (protocol) {
		case JMX:
			return new BuildStatusJMX(hostName, port);
		case RSS:
			return new BuildStatusRSS(hostName, port);
		}
		return null;
	}

}
