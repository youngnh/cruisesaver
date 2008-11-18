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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSException;
import org.gnu.stealthp.rsslib.RSSHandler;
import org.gnu.stealthp.rsslib.RSSItem;
import org.gnu.stealthp.rsslib.RSSParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BuildStatusRSSTest {

	private URL rssUrl;

	@Before
	public void setup() throws MalformedURLException {
		rssUrl = new URL("http://localhost:8080/rss");
	}

	
	@Test
	public void testBuildStatusRSSConstructor() {
		BuildStatus status = new BuildStatusRSS("localhost", "8080");
		Assert.assertEquals("[BuildStatusRSS: http://localhost:8080/rss]", status.toString());
		
		
	}
	
	@Test
	public void testGetProjects() {
		BuildStatus status = new BuildStatusRSS("localhost", "8080");
		List<Project> projects = status.getProjects();
		
		Project expected = new Project("connectfour");
		Assert.assertEquals("is cruilsecontrol running?", Arrays.asList(expected), projects);
		
		Project actual = projects.get(0);
		Assert.assertEquals(Project.StatusEnum.GOOD, actual.getBuildStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRSSChannel() throws RSSException {
		RSSHandler handler = new RSSHandler();
		RSSParser.parseXmlFile(rssUrl, handler, false);

		RSSChannel channel = handler.getRSSChannel();
		LinkedList items = channel.getItems();

		Assert.assertEquals(1, items.size());
		RSSItem item = (RSSItem) items.getFirst();
		System.out.println(item);

		Assert.assertTrue(item.getTitle().startsWith("connectfour passed"));
		Assert.assertEquals("http://localhost:8080/buildresults/connectfour", item.getLink());
		Assert.assertEquals("Build passed", item.getDescription());

	}

	@Test
	public void testConnectRSS() throws Exception {

		InputStream stream = rssUrl.openStream();
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader in = new BufferedReader(reader);
		String line = null;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}

		in.close();
	}

}
