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

package status.logging;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * Unlike the default ConsoleHandler from java.util.logging, this one will let
 * you apply a custom format.
 * 
 * @author James A. Wilson
 * 
 */
public class ConfigurableConsoleHandler extends StreamHandler {

	public ConfigurableConsoleHandler() {
		this(System.out, new CustomFormatter());
	}

	public ConfigurableConsoleHandler(OutputStream stream, CustomFormatter formatter) {
		setLevel(Level.FINE);
		setOutputStream(stream);
		setFormatter(formatter);
	}

	@Override
	public void publish(LogRecord record) {
		super.publish(record);
		flush();
	}

}
