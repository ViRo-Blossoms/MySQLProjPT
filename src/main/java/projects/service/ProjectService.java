package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import projects.entity.Material;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {

ProjectDao projectDao = new ProjectDao();
	
public Project addProject(Project project) {
	return projectDao.insertProject(project);
} //AP

public List<Project> fetchAllProjects() {
	return projectDao.fetchAllProjects()
			.stream()
			.sorted((p1, p2) -> p1.getProjectId() - p2.getProjectId())
			.collect(Collectors.toList());
} //FAP

public Project fetchProjectById(Integer projectId) {
	return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID = " + projectId + " does not exist.")); 
} //FPBI

public void modifyProjectDetails(Project project) {
	if(!projectDao.modifyProjectDetails(project)) 
	{
		throw new DbException("Project with project ID = " + project.getProjectId() + " does not exist.");
	}
} //MPD

public void deleteProject(Integer intInput) {
	if(!projectDao.deleteProject(intInput)) 
	{
		throw new DbException("Project with project ID = " + intInput + " does not exist.");
	}
} //DP

public List<Material> fetchAllMaterials() {
	return projectDao.fetchAllMaterials();
} //FAM

public void addMaterialToProject(Integer projectId, Material material) {
	projectDao.AddMaterialToProject(projectId, material);
}

} //CLASS
