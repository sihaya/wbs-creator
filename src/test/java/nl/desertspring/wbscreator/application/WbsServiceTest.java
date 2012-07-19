/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import nl.desertspring.wbscreator.domain.TaskRepository;
import java.util.Arrays;
import java.util.List;
import javax.jcr.Node;
import nl.desertspring.wbscreator.domain.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

/**
 *
 * @author sihaya
 */
public class WbsServiceTest {
    @Test
    public void givenAUsernameEmailAndPasswordCreateSavesAccount() {
        WbsService wbsService = new WbsService();
        final String username = "username";
        final String password = "1234";
        final String email = "username@email.com";
        
        UserRepository userRepository = mock(UserRepository.class);
        
        wbsService.setUserRepository(userRepository);
        wbsService.createUser(username, password, email);
        
        ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);
        
        verify(userRepository).save(user.capture());
        
        assertEquals(username, user.getValue().getUsername());
    }
    
    @Test
    public void givenAProjectNameAndUsernameCreateSavesNewProject() {
        WbsService wbsService = new WbsService();
        final String username = "username";
        final String projectName = "projectname";
        
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        wbsService.setProjectRepository(projectRepository);
        
        Project projectActual = wbsService.createProject(username, projectName);
                
        ArgumentCaptor<Project> project = ArgumentCaptor.forClass(Project.class);        
        verify(projectRepository).save(eq(username), project.capture());
                
        assertEquals(projectActual, project.getValue());        
        assertEquals(projectName, projectActual.getName());
    }
    
    @Test
    public void givenAUsernameListReturnsAllProjects() {
        WbsService wbsService = new WbsService();
        final String username = "username";
        
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        wbsService.setProjectRepository(projectRepository);
        
        List<Project> expected = Arrays.asList(mock(Project.class));
        when(projectRepository.findProjectByUsername(username)).thenReturn(expected);
                        
        List<Project> actual = wbsService.findProjectsByUsername(username);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void givenAProjectIdListReturnsAllSheets() {        
        WbsService wbsService = new WbsService();
        
        final String projectId = "42342";
        
        SheetRepository sheetRepository = mock(SheetRepository.class);
        wbsService.setSheetRepository(sheetRepository);
        
        List<Sheet> expected = Arrays.asList(mock(Sheet.class));        
        when(sheetRepository.findByProjectId(projectId)).thenReturn(expected);
        
        List<Sheet> actual = wbsService.findSheetsByProjectId(projectId);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void givenASheetIdFetchDetailReturnsSheetDetail() {
        WbsService wbsService = new WbsService();
        
        final String sheetId = "3423423423";
        
        SheetRepository sheetRepository = mock(SheetRepository.class);
        wbsService.setSheetRepository(sheetRepository);
        
        Sheet expected = mock(Sheet.class);
        when(sheetRepository.findById(sheetId)).thenReturn(expected);
        
        Sheet actual = wbsService.fetchSheetDetail(sheetId);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void givenASheetNameAndProjectIdCreateSheetSaves() {
        WbsService wbsService = new WbsService();
        
        final String projectId = "423423";
        final String sheetName = "sheetName";
        
        TaskRepository taskRepository = mock(TaskRepository.class);
        SheetRepository sheetRepository = mock(SheetRepository.class);
        wbsService.setSheetRepository(sheetRepository);
        wbsService.setTaskRepository(taskRepository);
        
        Sheet sheetActual = wbsService.createSheet(projectId, sheetName);
        
        ArgumentCaptor<Sheet> sheetExpected = ArgumentCaptor.forClass(Sheet.class);
        
        verify(sheetRepository).save(sheetExpected.capture());        
        verify(taskRepository).save(anyString(), eq(sheetActual.getRoot()));
        
        assertEquals(sheetExpected.getValue(), sheetActual);
        assertEquals(sheetName, sheetActual.getName());
        assertNotNull(sheetActual.getRoot());
    }
    
    @Test
    public void givenATaskParentIdCreateSavesTask() {
        WbsService wbsService = new WbsService();
        
        final String parentTaskId = "42423423";
        
        TaskRepository taskRepository = mock(TaskRepository.class);
        wbsService.setTaskRepository(taskRepository);
        
        final ArgumentCaptor<Task> actual = ArgumentCaptor.forClass(Task.class);
                
        Task task = wbsService.createTask(parentTaskId);
        
        verify(taskRepository).save(eq(parentTaskId), actual.capture());        
        
        assertEquals(task, actual.getValue());
    }
    
    @Test
    public void givenATaskIdEffortAndNameUpdateSaves() {
        WbsService wbsService = new WbsService();
        
        final String taskId = "434234";
        final Integer effort = 43;
        final String name = "name";
        
        TaskRepository taskRepository = mock(TaskRepository.class);
        wbsService.setTaskRepository(taskRepository);
                
        Task task = mock(Task.class);
        ArgumentCaptor<Task> taskActual = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.findById(taskId)).thenReturn(task);
        
        wbsService.updateTask(taskId, effort, name);
        
        verify(taskRepository).save(taskActual.capture());
        
        assertEquals(task, taskActual.getValue());
        
        verify(task).setEffort(effort);
        verify(task).setName(name);
    }
    
    @Test
    public void givenATaskIdDeleteCallsDeleteTask() {
         WbsService wbsService = new WbsService();
        
        final String taskId = "434234";
        
        TaskRepository taskRepository = mock(TaskRepository.class);
        wbsService.setTaskRepository(taskRepository);
        
        wbsService.deleteTask(taskId);
        
        verify(taskRepository).delete(taskId);
    }
   
}
