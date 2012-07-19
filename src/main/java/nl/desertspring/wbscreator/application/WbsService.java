/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import java.util.List;
import javax.ejb.Stateless;
import nl.desertspring.wbscreator.domain.*;

/**
 *
 * @author sihaya
 */
@Stateless
public class WbsService {

    private UserRepository userRepository;
    private SheetRepository sheetRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;

    public void createUser(String username, String password, String email) {
        User user = new User();

        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);

        userRepository.save(user);
    }

    public Project createProject(String username, String projectName) {
        Project project = new Project();

        project.setName(projectName);

        projectRepository.save(username, project);

        return project;
    }

    List<Project> findProjectsByUsername(String username) {
        return projectRepository.findProjectByUsername(username);
    }

    List<Sheet> findSheetsByProjectId(String projectId) {
        return sheetRepository.findByProjectId(projectId);
    }

    Sheet fetchSheetDetail(String sheetId) {
        return sheetRepository.findById(sheetId);
    }

    Task createTask(String parentTaskId) {
        Task task = new Task();

        taskRepository.save(parentTaskId, task);

        return task;
    }

    void updateTask(String taskId, Integer effort, String name) {
        Task task = taskRepository.findById(taskId);

        task.setEffort(effort);
        task.setName(name);

        taskRepository.save(task);
    }

    void deleteTask(String taskId) {
        taskRepository.delete(taskId);
    }

    public Sheet createSheet(String projectId, String sheetName) {
        Sheet sheet = new Sheet();

        sheet.setName(sheetName);

        sheetRepository.save(projectId, sheet);
        
        Task task = createTask(sheet.getSheetId());
        sheet.setRoot(task);

        return sheet;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void setSheetRepository(SheetRepository sheetRepository) {
        this.sheetRepository = sheetRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
