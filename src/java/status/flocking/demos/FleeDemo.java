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

package status.flocking.demos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.jdesktop.jdic.screensaver.ScreensaverSettings;
import org.jdesktop.jdic.screensaver.SimpleScreensaver;

import status.CCCollidingStatus;
import status.CCSettings;
import status.logging.LogUtil;
import steering.ActionSelection;
import steering.Locomotion;
import steering.SimpleLocomotion;
import steering.actionselection.KeepDistance;
import vector.Vector2D;

/**
 * Demo of screensaver exhibiting Steering Behavior
 * 
 * @author Nate Young
 */
public class FleeDemo extends SimpleScreensaver {
	Logger logger = LogUtil.getLogger(CCCollidingStatus.class);

	private Image offScreen;
	private Graphics offScreenGraphics;

	private CCSettings ccSettings;

	private Vector2D center;
	private Map<Locomotion, ActionSelection> flock;

	private int radius;	

	@Override
	public void init() {
		super.init();
		ScreensaverSettings settings = getContext().getSettings();
		ccSettings = new CCSettings(settings);

		radius = ccSettings.getBallSize();
		
		Vector2D initialPos = new Vector2D(0.0, 0.0);
		Vector2D initialVelo = new Vector2D(1.0, 1.0);
		SimpleLocomotion boid = new SimpleLocomotion(1.0, initialPos, initialVelo);
		boid.setMaxForce(5.0);
		boid.setMaxSpeed(50.0);

		int width = getContext().getComponent().getWidth();
		double middlex = width / 2;
		int height = getContext().getComponent().getHeight();
		double middley = height / 2;
		center = new Vector2D(middlex, middley);
		
		ActionSelection keepDistance = new KeepDistance(boid, center, ccSettings.getBallSize() * 2);
		
		flock = new Hashtable<Locomotion, ActionSelection>();
		flock.put(boid, keepDistance);
		
		//start simulation thread
		Thread simulationThread = new SimulationThread(flock, width, height, radius);
		simulationThread.start();
	}

	@Override
	public void paint(Graphics g) {
		Component c = getContext().getComponent();

		//double buffered graphics
		if ((offScreen == null) 
				|| (offScreen.getWidth(null) != c.getWidth())
				|| (offScreen.getHeight(null) != c.getHeight())) {
			logger.fine("creating new offscreen graphic sized: " + c.getWidth() + "," + c.getHeight());
			if (offScreen != null) {
				logger.fine("dispose of old offscreen graphic sized: " + c.getWidth() + ","	+ c.getHeight());
				offScreenGraphics.dispose();
			}
			offScreen = c.createImage(c.getWidth(), c.getHeight());
			offScreenGraphics = offScreen.getGraphics();
			offScreenGraphics.setColor(c.getBackground());
			offScreenGraphics.fillRect(0, 0, c.getWidth(), c.getHeight());
			Font priorFont = offScreenGraphics.getFont();
			Font newFont = new Font(priorFont.getName(), priorFont.getStyle(), ccSettings.getFontSize());
			offScreenGraphics.setFont(newFont);
		}

		try {
			renderToGraphics((Graphics2D) offScreenGraphics);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		g.drawImage(offScreen, 0, 0, null);
	}

	private void renderToGraphics(Graphics2D g) {
		Component c = getContext().getComponent();

		// Erase the old
		g.setColor(c.getBackground());
		g.fillRect(0, 0, c.getWidth(), c.getHeight());

		//draw the boid
		synchronized(flock) {
			for(Locomotion boid : flock.keySet()) {
				g.setColor(Color.green);
				g.fillRoundRect((int)boid.position().x - radius, (int)boid.position().y - radius, radius, radius, radius, radius);
			}
		}

		//draw an "x" over the sought position
		g.setColor(Color.red);
		g.drawString("x", (int) center.x, (int) center.y);
		
		//draw a circle around the position
		g.drawRoundRect((int) center.x - radius * 2, (int) center.y - radius * 2, radius * 4, radius * 4, radius * 4, radius * 4);
	}
	
}