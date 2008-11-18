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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import status.logging.LogUtil;

public class BuildStatusHttp {
	private static final Logger logger = LogUtil.getLogger(BuildStatusHttp.class);

	private final String host;

	private final String port;

	public BuildStatusHttp() {
		host = "throttle";
		port = "8000";
	}

	public BuildStatusHttp(String host, String port) {
		this.host = host;
		this.port = port;
	}

	public List<Project> debugData(int size) {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssZ");
		Date date = new Date();
		List<Project> result = new ArrayList<Project>(size);
		for (int i = 0; i < size; ++i) {
			Project project = new Project("test project " + i);
			project.setLastBuild(format.format(date));
			project.setLastSuccessfulBuild(format.format(date));
			result.add(project);
		}
		return result;
	}

	public static void main(String[] args) {

		BuildStatusHttp bs = new BuildStatusHttp();
		bs.getProjects();

	}

	public List<Project> getProjects() {
		List<String> projectsNames = getProjectNames();
		List<Project> projects = new ArrayList<Project>();

		for (String name : projectsNames) {
			logger.fine("project: " + name);
			Project p = getProjectStatus(name);
			logger.fine("project status: " + p);
			projects.add(p);
		}

		return projects;
	}

	public List<String> getProjectNames() {
		URL url;
		try {
			url = new URL("http://" + host + ":" + port + "/serverbydomain");
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, "failed to define URL.", e);
			return new ArrayList<String>();
		}

		StringBuffer content = fetchContents(url);

		List<String> result = new ArrayList<String>();
		Pattern pattern = Pattern.compile("CruiseControl Project:name=([\\w -]*)");
		Matcher matcher = pattern.matcher(content.toString());
		while (matcher.find()) {
			result.add(matcher.group(1));
		}

		return result;
	}

	private Project getProjectStatus(String projectName) {
		// http://throttle:8000/mbean?objectname=CruiseControl+Project:name=mako-client
		Project project = new Project(projectName);
		URL url;
		try {
			url = new URL("http://" + host + ":" + port
					+ "/mbean?objectname=CruiseControl+Project:name=" + projectName);
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, "failed to define URL.", e);
			return project;
		}

		StringBuffer content = fetchContents(url);

		Pattern pattern = Pattern
				.compile(">Time of the last build, using the format 'yyyyMMddHHmmss'[^\\d]*([\\d]*)");
		project.setLastBuild(extractAttribute(content, pattern, 1));

		pattern = Pattern
				.compile(">Time of the last successful build, using the format 'yyyyMMddHHmmss'[^\\d]*([\\d]*)");
		project.setLastSuccessfulBuild(extractAttribute(content, pattern, 1));

		return project;

	}

	private String extractAttribute(StringBuffer content, Pattern pattern, int groupOffset) {
		Matcher matcher = pattern.matcher(content.toString());
		String result = "";
		while (matcher.find()) {
			// there should be only one match
			logger.fine(matcher.group(0));
			result = matcher.group(groupOffset);
		}
		return result;
	}

	private StringBuffer fetchContents(URL url) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String line = null;

			while ((line = in.readLine()) != null) {
				sb.append(line);
			}

			// System.out.println(content.toString());
			in.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "failed to read url " + url, e);
			return sb;
		}

		return sb;
	}

}
