/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import nl.desertspring.wbscreator.domain.*;

/**
 *
 * @author sihaya
 */
@Stateless
@LocalBean
public class WbsService {

    private UserRepository userRepository;
    private SheetRepository sheetRepository;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private RandomGenerator randomGenerator;
    private SecurityUtil securityUtil;

    public void createUser(String username, char[] password, String email) {
        User user = new User();

        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);

        userRepository.save(user);
    }

    public Project createProject(String projectName) {
        Project project = new Project();

        project.setName(projectName);

        projectRepository.save(securityUtil.getSubjectUsername(), project);

        return project;
    }

    public List<Project> findProjectsByUsername(String username) {
        return projectRepository.findProjectByUsername(username);
    }

    public List<Sheet> findSheetsByProjectId(String projectId) {
        return sheetRepository.findByProjectId(projectId);
    }

    public Sheet fetchSheetDetail(String sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId);

        Task root = taskRepository.findRootById(sheetId);
        sheet.setRoot(root);

        return sheet;
    }

    public Task createTask(String parentTaskId) {
        Task task = new Task();
        task.setName("");
        task.setEffort(0);

        taskRepository.save(parentTaskId, task);

        return task;
    }

    public void updateTask(String taskId, Integer effort, String name) {
        Task task = taskRepository.findById(taskId);

        task.setEffort(effort);
        task.setName(name);

        taskRepository.save(task);
    }

    public void deleteTask(String taskId) {
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

    public void addMemberToProject(String projectId, String username) {
        String userId = userRepository.getId(username);

        projectRepository.addMemberToProject(projectId, userId);
    }

    public void grantPublicRead(String sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId);
        
        if (sheet.getPublicSecret() != null) {
            return;
        }
        
        sheet.setPublicSecret(randomGenerator.generateSecret());
        sheetRepository.update(sheet);        
    }
    
    public Sheet findSheetByPublicSecret(String publicSecret) {
        return sheetRepository.findByPublicSecret(publicSecret);
    }

    @Inject
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Inject
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Inject
    public void setSheetRepository(SheetRepository sheetRepository) {
        this.sheetRepository = sheetRepository;
    }

    @Inject
    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Inject
    public void setRandomGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @Inject
    public void setSecurityUtil(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
}
