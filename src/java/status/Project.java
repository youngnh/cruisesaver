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

public class Project {
	private String lastSuccessfulBuild;
	private String lastBuild;
	private final String name;
	private String buildStartTime;
	private long buildInterval;
	private String paused;
	private String status;
	private boolean isLastBuildSuccesful;

	public Project(String name) {
		this.name = name;
	}

	public String getLastBuild() {
		return lastBuild;
	}

	public void setLastBuild(String lastBuild) {
		this.lastBuild = lastBuild;
	}

	public String getLastSuccessfulBuild() {
		return lastSuccessfulBuild;
	}

	public void setLastSuccessfulBuild(String lastSuccessfullBuild) {
		this.lastSuccessfulBuild = lastSuccessfullBuild;
	}

	public StatusEnum getBuildStatus() {
		StatusEnum result = StatusEnum.BAD;
		if (lastSuccessfulBuild == null && lastBuild == null) {
			result = StatusEnum.UNKNOWN;
		} else if (isLastBuildSuccesful) {
			result = StatusEnum.GOOD;
		}

		return result;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "[" + name + ": " + lastBuild + ":" + lastSuccessfulBuild + "]";
	}

	static enum StatusEnum {
		GOOD, BAD, UNKNOWN
	}

	public long getBuildInterval() {
		return buildInterval;
	}

	public void setBuildInterval(long buildInterval) {
		this.buildInterval = buildInterval;
	}

	public String getBuildStartTime() {
		return buildStartTime;
	}

	public void setBuildStartTime(String buildStartTime) {
		this.buildStartTime = buildStartTime;
	}

	public boolean isLastBuildSuccesful() {
		return isLastBuildSuccesful;
	}

	public void setLastBuildSuccesful(boolean isLastBuildSuccesful) {
		this.isLastBuildSuccesful = isLastBuildSuccesful;
	}

	public String getPaused() {
		return paused;
	}

	public void setPaused(String paused) {
		this.paused = paused;
	}

	public void setStatus(String status) {
		this.status = status;
		if (this.status.startsWith("waiting") || this.status.equalsIgnoreCase("Build passed"))
			this.status = "waiting";
	}

	public boolean isBuilding() {
		if (status == null) {
			return false;
		}
		return this.status.startsWith("building");
	}

	public boolean isWaiting() {
		if (status == null) {
			return false;
		}
		return this.status.startsWith("waiting");
	}

	public String getStatus() {
		return this.status;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Project)) {
			return false;
		}
		Project other = (Project) obj;
		if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public String getLabel() {
		String label = getStatus();
		if (label == null || isWaiting()) {
			label = getName();
		} else if (label != null && label.contains("queue")) {
			int offset = label.indexOf('[');
			if (offset > 0) {
				label = getName() + ":queued " + label.substring(offset);
			} else {
				label = getName() + ":queued";
			}
		} else if (label != null && label.contains("building")) {
			label = getName() + "-" + label;
		}
		return label;
	}
}
