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

package status.collisions;

import java.util.logging.Logger;

import status.logging.LogUtil;
import vector.Vector2D;

/**
 * 
 * @author Nate Young, James Wilson
 * 
 */
public class ParticleParty {

	private static final Logger logger = LogUtil.getLogger(ParticleParty.class);
	private static final double TIME_DIVISION = 3.0;
	private static final int MAX_SPEED = 7;
	private static final double MASS = 1.0;

	private double radius; // radius of particles

	private Orb[] balls;
	private final int height;
	private final int width;

	/**
	 * 
	 * @param width
	 *            width of the "box" that particles are in
	 * @param height
	 *            height of the "box" that particles are in
	 */
	public ParticleParty(int numorbs, int width, int height) {
		this.width = width;
		this.height = height;

		setNumberOfOrbs(numorbs);
	}

	public Orb getOrb(int i) {
		return balls[i];
	}

	// update simulation
	public void next() {
		for (int i = 0; i < balls.length; i++) {
			Orb orb = balls[i];
			move(orb);
		}
		// reset all collided flags
		for (int i = 0; i < balls.length; i++) {
			balls[i].collided = false;
		}
	}

	public void move(Orb orb) {
		// velocity moves orb
		orb.x += orb.v.x / TIME_DIVISION;
		orb.y += orb.v.y / TIME_DIVISION;

		// check for collisions with other orbs (or wall)
		for (int i = 0; !orb.collided && i < balls.length; i++) {
			if (orb == balls[i])
				continue;

			if (orb.isIntersecting(balls[i], radius)) { // touching
				orb.collide(balls[i]);
			}

			while (orb.isIntersecting(balls[i], radius)) {
				move(orb);
				move(balls[i]);
			}
		}

		// check if we've run into a wall
		if ((orb.x - radius < 0) || ((orb.x) > width)) {
			orb.x = (orb.x - radius) < 0 ? radius : width;
			orb.v.x *= -1;
		}

		if ((orb.y - radius < 0) || ((orb.y) > height)) {
			orb.y = (orb.y - radius) < 0 ? radius : height;
			orb.v.y *= -1;
		}
	}

	private Vector2D randomPoint(int width, int height) {
		return new Vector2D((Math.random() * width) - radius, (Math.random() * height) - radius);
	}

	/**
	 * Returns a random number between -MAX_SPEED and MAX_SPEED, inclusive, but
	 * excluding 0.
	 */
	private Vector2D randomVelocity() {
		double x = (Math.random() * MAX_SPEED) + 1;
		if (Math.random() > 0.5) {
			x = -x;
		}
		double y = (Math.random() * MAX_SPEED) + 1;
		if (Math.random() > 0.5) {
			y = -y;
		}
		return new Vector2D(x, y);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setNumberOfOrbs(int size) {
		logger.info("set number of orbs to " + size);
		balls = new Orb[size];
		for (int i = 0; i < balls.length; ++i) {
			Vector2D center = randomPoint(width, height);
			balls[i] = new Orb(center.x, center.y, randomVelocity(), MASS);
		}

	}
}
