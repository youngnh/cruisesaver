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
import java.awt.Font;
import java.awt.Graphics2D;

import vector.Vector2D;

/**
 * Class responsible for drawing a message to the graphics environment
 * 
 * @author Nate Young
 */
public class MessageRenderer {

	private final int messageFrequency = 30;
	private int currentMessageFrequency = 0;
	private String message;
	private Vector2D location;
	private int height;
	private Color msgColor;

	public MessageRenderer() {
		msgColor = Color.RED;
	}

	public void render(Graphics2D g) {
		currentMessageFrequency++;
		if (message != null && message.length() > 0) {
			Font f = g.getFont();
			Font font = new Font(f.getName(), f.getStyle(), 20);
			g.setFont(font);
			if (location == null) {
				location = new Vector2D(10, g.getFontMetrics().getHeight());
			}
			if ((currentMessageFrequency % messageFrequency) == 0) {
				int move = g.getFontMetrics().getHeight() / 5;
				location.y += move;
				if (location.y + g.getFontMetrics().getHeight() > height) {
					location.y = 5;
				}
			}
			Color c = g.getColor();
			g.setColor(msgColor);
			g.drawString(message, (int) location.x, (int) location.y);
			g.setColor(c);
			g.setFont(f);
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;

	}

	public Vector2D getLocation() {
		return location;
	}

	public void setLocation(Vector2D location) {
		this.location = location;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getMsgColor() {
		return msgColor;
	}

	public void setMsgColor(Color msgColor) {
		this.msgColor = msgColor;
	}
}
