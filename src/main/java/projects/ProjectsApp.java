package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Material;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	// @formatter:off
	private List<String> operations = List.of
	(

		"1) List all projects",
		"2)    Add a project",
		"3) Delete a project",
		"4) Select a project",
		"-- Select a project first for following options --",
		"5) Update a project",
		"6) Add materials to a project"
		
	);
	// @formatter:on
	
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
public static void main(String[] args) {

	new ProjectsApp().processUserSelections();
} //MAIN

private void processUserSelections() {
	boolean done = false;
	
	while(!done) 
	{
		try
		{
			int selection = getUserSelection();
			switch (selection)
			{
				case -1:
				{
					done = exitMenu();
					break;
				}
				case 1:
				{
					listProjects();
					break;
				}
				case 2:
				{
					createProject();
					break;
				}
				case 3:
				{
					deleteProject();
					break;
				}
				case 4:
				{

					selectProject();
					break;
				}
				case 5:
				{
					updateProjectDetails();
					break;
				}
				case 6:
				{
					addProjectMaterials();
					break;
				}
				default:
				{
					System.out.println("\n" + selection +  " isn't a valid number. Try again.");
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("\nError: " + e + " Try again.");
		}
	}
} //PUS

private void addProjectMaterials() {
	if(Objects.isNull(curProject))
	{
		System.out.println("\nYou haven't selected a project yet, please select a project first.");
		return;
	}
	System.out.println("\nMaterials");
	List <Material> materials = projectService.fetchAllMaterials();
	materials.forEach(material -> System.out.println("	" + material.getMaterialName()));
	String matName = getStringInput("Enter the name of the material you'd like to add");
	Integer matNum = getIntInput("Enter the number of " + matName + " needed");
	BigDecimal matCost = getDecimalInput("Enter the cost of approximately " + matNum + " " + matName);
	Material materiall = new Material();
	materiall.setProjectId(curProject.getProjectId());
	materiall.setMaterialName(matName);
	materiall.setNumRequired(matNum);
	materiall.setCost(matCost);
	projectService.addMaterialToProject(curProject.getProjectId(), materiall);
	curProject = projectService.fetchProjectById(materiall.getProjectId());
	System.out.println("Material Added.");
} //APM

private void deleteProject() {
	listProjects();
	Integer delProj = getIntInput("Enter the ID of the project you wish to delete");
	Integer confirm = getIntInput("Are you sure? Press enter to cancel, or Re-enter the project ID [" + delProj + "] to confirm");
	if( delProj == confirm)
	{
		projectService.deleteProject(delProj);
		curProject = null;
		System.out.println("Project successfully deleted.");
	}
	else
	{
		System.out.println("Deletion cancelled, Returning to menu.");
	}
	
} //DP

private void updateProjectDetails() {
	if(Objects.isNull(curProject))
		{
			System.out.println("\nYou haven't selected a project yet, please select a project first.");
			return; //ViRo: You can just return?? Wild
		}
	String projectName = getStringInput("Enter the updated project name [" + curProject.getProjectName() + "]");
	BigDecimal estimatedHours = getDecimalInput("Enter the updated estimated hours [" + curProject.getEstimatedHours() + "]");
	BigDecimal actualHours = getDecimalInput("Enter the updated actual hours [" + curProject.getActualHours() + "]");
	Integer difficulty = getIntInput("Enter the updated difficulty (1-5) [" +curProject.getDifficulty() + "]");
	String notes = getStringInput("Enter the updated notes [" + curProject.getNotes() + "]");
	Project project = new Project();
	project.setProjectId(curProject.getProjectId());
	project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
	project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
	project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
	project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
	project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
	projectService.modifyProjectDetails(project);
	projectService.fetchProjectById(curProject.getProjectId());
	System.out.println("Project updated.");
} //UPD

private void selectProject() {
	listProjects();
	Integer projectId = getIntInput("Enter a project ID to select a project");
	curProject = null;
	curProject = projectService.fetchProjectById(projectId);
} //SP

private void listProjects() {
	List<Project> projects = projectService.fetchAllProjects();
	System.out.println("\nProjects");
	projects.forEach(project -> System.out.println(" " + project.getProjectId() + ": " + project.getProjectName()));
} //LP

private void createProject() {
	String projectName = getStringInput("Enter the Project Name");
	BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
	BigDecimal actualHours = getDecimalInput("Enter the actual hours");
	Integer difficulty = getIntInput("Enter project difficulty (1-5)");
	String notes = getStringInput("Enter the project notes");

	Project project = new Project();
	
	project.setProjectName(projectName);
	project.setEstimatedHours(estimatedHours);
	project.setActualHours(actualHours);
	project.setDifficulty(difficulty);
	project.setNotes(notes);
	
	Project dbProject = projectService.addProject(project);
	System.out.println("You have succesfully created project: " + dbProject);
} //CrPr

private BigDecimal getDecimalInput(String prompt) {
	String input = getStringInput(prompt);
	if(Objects.isNull(input))
	{
		return null;
	}
	try
	{
		return new BigDecimal(input).setScale(2);
	}
	catch(NumberFormatException e)
	{
		throw new DbException(input + " is not a valid decimal.");
	}
} //GDI

private int getUserSelection() {
	printOperations();
	Integer input = getIntInput("\nEnter a menu selection");
	return Objects.isNull(input) ? -1 : input;
} //GUS

private boolean exitMenu() {
	System.out.println("Exiting the menu.");
	return true;
}

private Integer getIntInput(String prompt) {
	String input = getStringInput(prompt);
	if(Objects.isNull(input))
	{
		return null;
	}
	try
	{
		return Integer.valueOf(input);
	}
	catch(NumberFormatException e)
	{
		throw new DbException(input + " is not a valid number.");
	}
} //GUI

private String getStringInput(String prompt) {
System.out.print(prompt + ": ");
String input = scanner.nextLine();
return input.isBlank() ? null : input.trim();
} //GSI

private void printOperations() {
	System.out.println("\nThese are the available selections. Press the Enter Key to quit.");
	operations.forEach(line -> System.out.println(" " + line));
	if(Objects.isNull(curProject))
	{
		System.out.println("(!) You are not currently working with a project.");
	}
	else
	{
		System.out.println("You are working with project: " + curProject);
	}
} //PO

} //CLASS
