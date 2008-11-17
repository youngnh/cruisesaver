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

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.jdic.screensaver.ScreensaverSettings;
import org.jdesktop.jdic.screensaver.SimpleScreensaver;

import status.collisions.ParticleParty;
import status.concurrent.StatusExecutor;
import status.logging.LogUtil;

/**
 * CruiseControl status screen saver built from CCStatus screensaver by James
 * Wilson
 * 
 * @author James Wilson, Nate Young
 */
public class CCCollidingStatus extends SimpleScreensaver {
	Logger logger = LogUtil.getLogger(CCCollidingStatus.class);
	static final String CONFIG_ERROR_PREFIX = "configuration error:\n ";

	private Image offScreen;
	private Graphics offScreenGraphics;
	// private StatusTimerTask statusTimerTask;
	private Exchanger<List<Project>> exchanger;
	private List<Project> lastProjectsPainted;

	private CCSettings ccSettings;

	private ParticleParty simulation;
	private final ProjectRenderer projectRenderer = new ProjectRenderer();
	final MessageRenderer messageRenderer = new MessageRenderer();

	@Override
	public void init() {
		super.init();
		exchanger = new Exchanger<List<Project>>();

		ScreensaverSettings settings = getContext().getSettings();
		ccSettings = new CCSettings(settings);

		projectRenderer.setBallSize(ccSettings.getBallSize());

		lastProjectsPainted = Collections.emptyList();
		;
		List<Project> projects = Collections.emptyList();
		try {
			new StatusExecutor(ccSettings.getBuildStatus(), ccSettings.getUpdateDelaySeconds(),
					exchanger);
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "settings are messed up.", e);
			messageRenderer.setMessage(CONFIG_ERROR_PREFIX + e.getMessage());
		}

		int width = getContext().getComponent().getWidth();
		int height = getContext().getComponent().getHeight();
		simulation = new ParticleParty(projects.size(), width, height);
		simulation.setRadius(ccSettings.getBallSize());
	}

	@Override
	public void paint(Graphics g) {
		Component c = getContext().getComponent();

		// double buffered graphics
		if ((offScreen == null) || (offScreen.getWidth(null) != c.getWidth())
				|| (offScreen.getHeight(null) != c.getHeight())) {
			logger.fine("creating new offscreen graphic sized: " + c.getWidth() + ","
					+ c.getHeight());
			if (offScreen != null) {
				logger.fine("dispose of old offscreen graphic sized: " + c.getWidth() + ","
						+ c.getHeight());
				offScreenGraphics.dispose();
			}
			offScreen = c.createImage(c.getWidth(), c.getHeight());
			offScreenGraphics = offScreen.getGraphics();
			offScreenGraphics.setColor(c.getBackground());
			offScreenGraphics.fillRect(0, 0, c.getWidth(), c.getHeight());
			Font priorFont = offScreenGraphics.getFont();
			Font newFont = new Font(priorFont.getName(), priorFont.getStyle(), ccSettings
					.getFontSize());
			offScreenGraphics.setFont(newFont);
			messageRenderer.setHeight(offScreen.getHeight(null));
		}

		try {
			renderToGraphics((Graphics2D) offScreenGraphics);
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "exception during renderToGraphics: ", e);
		}

		g.drawImage(offScreen, 0, 0, null);
	}

	private void renderToGraphics(Graphics2D g) {
		Component c = getContext().getComponent();

		// Erase the old
		g.setColor(c.getBackground());
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
		simulation.next();
		lastProjectsPainted = getProjects();
		if (!lastProjectsPainted.isEmpty()) {
			for (int i = 0; i < lastProjectsPainted.size(); i++) {
				Project project = lastProjectsPainted.get(i);
				projectRenderer.render(g, project.getLabel(), project.getBuildStatus(), simulation
						.getOrb(i));
			}
		}

		// this code won't do anything if cruise control is working normally
		// otherwise it'll render an error message to the screen
		g.setColor(c.getBackground());
		if ((lastProjectsPainted == null || lastProjectsPainted.isEmpty())
				&& messageRenderer.getMessage() == null) {
			messageRenderer.setMessage("fetching projects from " + ccSettings.getHostName());
		}
		messageRenderer.render(g);
	}

	private List<Project> getProjects() {
		List<Project> updatedProjects = new ArrayList<Project>();
		List<Project> projects = lastProjectsPainted;
		try {
			projects = exchanger.exchange(updatedProjects, 1, TimeUnit.NANOSECONDS);
			logger.fine("exchanged project list of size " + projects.size());
			if (projects.size() != lastProjectsPainted.size()) {
				simulation.setNumberOfOrbs(projects.size());
				messageRenderer.setMessage(null);
			}
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "oops", e);
		} catch (TimeoutException ignore) {
			// logger.log(Level.FINE, "oops", e);
		}
		return projects;
	}
}