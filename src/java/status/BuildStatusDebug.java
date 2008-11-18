package status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuildStatusDebug implements BuildStatus {

	private final List<Project> projects;
	private String[] projectNames;

	public BuildStatusDebug(String portNumber) {
		projects = debugData(Integer.parseInt(portNumber));
	}

	public String[] getProjectNames() {
		return projectNames;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public List<Project> getProjects(String[] names) {
		return projects;
	}


	private List<Project> debugData(int size) {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssZ");
		Date date = new Date();
		List<Project> result = new ArrayList<Project>(size);
		projectNames = new String[size];
		for (int i = 0; i < size; ++i) {
			projectNames[i] = "test project " + i;
			Project project = new Project(projectNames[i]);
			project.setLastBuild(format.format(date));
			project.setLastSuccessfulBuild(format.format(date));
			result.add(project);
		}
		return result;
	}

}
