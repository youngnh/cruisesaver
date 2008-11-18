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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import status.Project.StatusEnum;
import status.collisions.Orb;
import vector.Vector2D;

/**
 * Class responsible for drawing a project to the graphics environment
 * 
 * @author Nate Young, James Wilson
 */
public class ProjectRenderer {

	private static final Color COLOR_GOOD = Color.GREEN;
	private static final Color COLOR_UNKNOWN = Color.BLUE;
	private static final Color COLOR_BAD = Color.RED;

	private int ballSize;

	public void setBallSize(int size) {
		this.ballSize = size;
	}

	public void render(Graphics2D g, String label, StatusEnum buildStatus, Orb orb) {
		int pulseOffset = orb.pulsePosition;

		render(g, orb.x, orb.y, pulseOffset, label, buildStatus);
	}

	public void render(Graphics2D g, double x, double y, int pulseOffset, String label,
			StatusEnum buildStatus) {
		Vector2D center = new Vector2D(x, y);

		// get status for color of ball
		Color statusColor = getStatusColor(buildStatus);
		g.setColor(statusColor);

		// setup the shaded pulse
		g.setPaint(new GradientPaint((float) center.x - ballSize - pulseOffset, (float) center.y
				- ballSize - pulseOffset, Color.WHITE, (float) center.x, (float) center.y,
				statusColor));
		// rectangles are drawn from the top left corner, so we adjust from our
		// center
		g.fillRoundRect((int) center.x - ballSize, (int) center.y - ballSize, ballSize, ballSize,
				ballSize, ballSize);

		// draw the label beneath the ball
		renderLabel(g, label, center);
	}

	private void renderLabel(Graphics2D g, String label, Vector2D center) {
		FontMetrics metrics = g.getFontMetrics();

		// TODO possible performance optimization here to keep track of
		// label and offset to only recalc offset if label changes.
		// but they seem to render just fine so ignore for now
		Rectangle2D bounds = metrics.getStringBounds(label, g);
		int x = (int) Math.ceil((center.x + (ballSize / 2) - (bounds.getWidth() / 2)));

		g.drawString(label, x - ballSize, (int) center.y + g.getFontMetrics().getHeight());
	}

	private Color getStatusColor(StatusEnum statusEnum) {
		Color result;
		switch (statusEnum) {
		case BAD:
			result = COLOR_BAD;
			break;
		case GOOD:
			result = COLOR_GOOD;
			break;
		case UNKNOWN:
			result = COLOR_UNKNOWN;
			break;
		default:
			result = Color.GRAY;
			break;
		}
		return result;
	}
}
