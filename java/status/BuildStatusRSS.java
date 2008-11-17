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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSException;
import org.gnu.stealthp.rsslib.RSSHandler;
import org.gnu.stealthp.rsslib.RSSItem;
import org.gnu.stealthp.rsslib.RSSParser;

import status.logging.LogUtil;

public class BuildStatusRSS implements BuildStatus {
	private static final Logger logger = LogUtil.getLogger(BuildStatusRSS.class);

	private URL rssUrl;

	public BuildStatusRSS(String hostName, String httpPort) {
		try {
			rssUrl = new URL("http", hostName, Integer.parseInt(httpPort), "/rss");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("port " + httpPort + " is not an integer");
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("could not create URL: " + e.getMessage());
		}
	}

	public String[] getProjectNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Project> getProjects() {
		List<Project> result = new ArrayList<Project>();

		RSSHandler handler = new RSSHandler();
		try {
			RSSParser.parseXmlFile(rssUrl, handler, false);
		} catch (RSSException e) {
			logger.log(Level.SEVERE, "ut-oh", e);
			return result;
		}

		RSSChannel channel = handler.getRSSChannel();
		LinkedList<RSSItem> items = channel.getItems();
		for (RSSItem item : items) {
			Project project = parseProject(item);
			LogUtil.log(logger, Level.FINE, "RSS Project: ", project.getName(), ", desc:"
					+ item.getDescription(), ", title: ", item.getTitle());
			result.add(project);
		}
		return result;
	}

	private Project parseProject(RSSItem rssItem) {
		String[] strings = rssItem.getTitle().split(" ");
		if (strings.length <= 0) {
			logger.log(Level.SEVERE, "title is not as expected \"" + rssItem.getTitle() + "\"");
			return null;
		}
		Project project = new Project(strings[0]);
		project.setLastBuildSuccesful("passed".equals(strings[1]));
		// project.setBuildInterval((Long) rawAttrib);
		// project.setBuildStartTime((String) rawAttrib);
		project.setLastBuild(rssItem.getDate());
		// project.setLastSuccessfulBuild((String) rawAttrib);
		project.setStatus(rssItem.getDescription());
		return project;
	}

	public List<Project> getProjects(String[] names) {
		return getProjects();
	}

	@Override
	public String toString() {
		return "[BuildStatusRSS: " + rssUrl + "]";
	}

}
