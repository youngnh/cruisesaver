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

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import status.logging.LogUtil;

import com.sun.jndi.rmi.registry.RegistryContextFactory;

public class BuildStatusJMX implements BuildStatus {
	private static final Logger logger = LogUtil.getLogger(BuildStatusJMX.class);

	private final String rmiHost;
	private final String rmiPort;

	public BuildStatusJMX() {
		rmiHost = "localhost";
		rmiPort = "1099";
	}

	public BuildStatusJMX(String rmiHost, String rmiPort) {
		this.rmiHost = rmiHost;
		this.rmiPort = rmiPort;
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		String server = "localhost";
		String port = "1099";
		if (args != null && args.length == 2) {
			server = args[0];
			port = args[1];
		}
		BuildStatus build = new BuildStatusJMX(server, port);
		LogUtil.setDebug(true);
		// build.getProjects(new String[]{"connectfour"});
		for (int i = 0; i < 1; ++i) {
			build.getProjects();
			Thread.sleep(1000);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see status.BuildStatus#getProjects()
	 */
	public List<Project> getProjects() {
		String[] projectNames = getProjectNames();
		return getProjects(projectNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see status.BuildStatus#getProjectNames()
	 */
	@SuppressWarnings("unchecked")
	public String[] getProjectNames() {
		String[] result = new String[0];

		MBeanServerConnection connection = getJmxConn();
		if (connection == null) {
			return result;
		}
		// using this mbean requires some cruisecontrol source code so I wont
		ObjectName objName = null;
		try {
			objName = new ObjectName("CruiseControl Manager:id=unique");
			Object rawAttrib;
			rawAttrib = connection.getAttribute(objName, "AllProjectsStatus");
			Map<String, String> allProjectStatus = (Map<String, String>) rawAttrib;
			result = new String[allProjectStatus.size()];
			result = allProjectStatus.keySet().toArray(result);

		} catch (Throwable t) {
			logger.log(Level.SEVERE, "couldn't create controller objectname", t);
			return result;
		}
		dumpMBeanInfo(connection, objName);

		return result;
	}

	private List<Project> getProjects(String[] names) {
		List<Project> result = new ArrayList<Project>(names.length);

		MBeanServerConnection connection = getJmxConn();
		if (connection == null) {
			return result;
		}

		for (int i = 0; i < names.length; i++) {
			String projectName = names[i];
			Project project = checkBuild(connection, projectName);
			result.add(project);
		}

		return result;
	}

	private Project checkBuild(MBeanServerConnection connection, String projectName) {

		ObjectName mbeanObj;
		try {
			mbeanObj = ObjectName.getInstance("CruiseControl Project:name=" + projectName);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}

		dumpMBeanInfo(connection, mbeanObj);
		/*
		 * Status=waiting for next time to build LastBuildSuccessful=true
		 * LogDir=logs/connectfour ProjectName=connectfour
		 * LastBuild=20070120073627 LastSuccessfulBuild=20070120073627
		 * BuildStartTime=
		 * LabelIncrementer=net.sourceforge.cruisecontrol.labelincrementers
		 * .DefaultLabelIncrementer Label=build.3 BuildInterval=300000
		 * Paused=false
		 */

		Project project = new Project(projectName);
		try {
			Object rawAttrib;
			//LastBuildSuccessful is still in the patch
			rawAttrib = connection.getAttribute(mbeanObj, "LastBuildSuccessful");
			project.setLastBuildSuccesful((Boolean) rawAttrib);

			rawAttrib = connection.getAttribute(mbeanObj, "BuildInterval");
			project.setBuildInterval((Long) rawAttrib);

			rawAttrib = connection.getAttribute(mbeanObj, "BuildStartTime");
			project.setBuildStartTime((String) rawAttrib);

			rawAttrib = connection.getAttribute(mbeanObj, "LastBuild");
			project.setLastBuild((String) rawAttrib);

			rawAttrib = connection.getAttribute(mbeanObj, "LastSuccessfulBuild");
			project.setLastSuccessfulBuild((String) rawAttrib);

			rawAttrib = connection.getAttribute(mbeanObj, "Status");
			project.setStatus((String) rawAttrib);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "error getting attributes.", e);
		}

		return project;
	}

	private void dumpMBeanInfo(MBeanServerConnection connection, ObjectName mbeanObj) {
		Level level = Level.FINEST;
		if (logger.isLoggable(level)) {
			MBeanInfo beanInfo = null;
			try {
				beanInfo = connection.getMBeanInfo(mbeanObj);
				MBeanAttributeInfo[] attributes = beanInfo.getAttributes();
				for (int i = 0; i < attributes.length; i++) {
					MBeanAttributeInfo attribute = attributes[i];
					try {
						LogUtil.log(logger, level, attribute.getName(), "=", connection
								.getAttribute(mbeanObj, attribute.getName()));
					} catch (UnmarshalException e) {
						LogUtil.log(logger, level, attribute.getName(), "= [UnmarshalException: ",
								e.getMessage(), "]");
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private MBeanServerConnection getJmxConn() {
		// The address of the connector server
		JMXServiceURL url;
		String address = "service:jmx:rmi://" + rmiHost + ":" + rmiPort + "/jndi/jrmp";
		try {
			url = new JMXServiceURL(address);
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, "failed to connect to server: " + address, e);
			throw new RuntimeException(e.getMessage());
		}

		Map<String, String> environment = new HashMap<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, RegistryContextFactory.class.getName());
		environment.put(Context.PROVIDER_URL, "rmi://" + rmiHost + ":" + rmiPort);
		// environment.put(Context.SECURITY_PRINCIPAL, "user");
		// environment.put(Context.SECURITY_CREDENTIALS, "password");

		// Create the JMXCconnectorServer
		MBeanServerConnection mbeanServerConnection = null;
		try {
			JMXConnector cntor = JMXConnectorFactory.connect(url, environment);
			mbeanServerConnection = cntor.getMBeanServerConnection();
			String domain = mbeanServerConnection.getDefaultDomain();
			LogUtil.log(logger, Level.FINEST, "found default domain as: ", domain);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "connection failed.", e);
		}

		return mbeanServerConnection;
	}

	@Override
	public String toString() {
		return "[BuildStatusJMX: " + rmiHost + ":" + rmiPort + "]";
	}

}
