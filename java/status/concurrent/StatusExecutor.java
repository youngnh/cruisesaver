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

package status.concurrent;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import status.BuildStatus;
import status.Project;
import status.logging.LogUtil;

public class StatusExecutor {
	private static final Logger logger = LogUtil.getLogger(StatusExecutor.class);
	private final ScheduledExecutorService scheduler;

	public StatusExecutor(List<BuildStatus> buildStatusList, long periodSeconds,
			Exchanger<List<Project>> exchanger) {
		this.scheduler = Executors.newScheduledThreadPool(1);

		Runnable projectUpdater = new BuildStatusRunnable(buildStatusList, exchanger);
		scheduler.scheduleAtFixedRate(projectUpdater, 1L, periodSeconds, SECONDS);
		LogUtil.log(logger, Level.FINE, "scheduled @ ", periodSeconds);
	}

	private static class BuildStatusRunnable implements Runnable {

		private final List<BuildStatus> buildStatusList;
		private final Exchanger<List<Project>> exchanger;

		public BuildStatusRunnable(List<BuildStatus> buildStatusList,
				Exchanger<List<Project>> exchanger) {
			this.buildStatusList = buildStatusList;
			this.exchanger = exchanger;
		}

		public void run() {
			List<Project> updatedProjectList = new ArrayList<Project>();
			for (BuildStatus buildStatus : buildStatusList) {
				updatedProjectList.addAll(buildStatus.getProjects());
			}

			try {
				logger.fine("entering exchange with projectList: " + updatedProjectList);
				exchanger.exchange(updatedProjectList);
			} catch (InterruptedException e) {
				logger.warning("hmm, interupted during exchange");
			}
		}

	}
}
