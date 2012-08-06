/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import java.util.Arrays;
import java.util.List;
import nl.desertspring.wbscreator.domain.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

/**
 *
 * @author sihaya
 */
public class WbsServiceTest {

    final String username = "username";
    private WbsService wbsService;
    private SecurityUtil securityUtil;
    private AuthorizationService authorizationService;

    @Before
    public void setUp() {
        wbsService = new WbsService();

        securityUtil = mock(SecurityUtil.class);
        wbsService.setSecurityUtil(securityUtil);
        
        securityUtil = mock(SecurityUtil.class);
        when(securityUtil.getSubjectUsername()).thenReturn(username);
        
        authorizationService = mock(AuthorizationService.class);

        wbsService.setSecurityUtil(securityUtil);
        wbsService.setAuthorizationService(authorizationService);        
    }

    @Test
    public void givenAUsernameEmailAndPasswordCreateSavesAccount() {
        final String password = "1234";
        final String email = "username@email.com";

        UserRepository userRepository = mock(UserRepository.class);

        wbsService.setUserRepository(userRepository);
        wbsService.createUser(username, password.toCharArray(), email);

        ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(user.capture());

        assertEquals(username, user.getValue().getUsername());
    }

    @Test
    public void givenAProjectCreateSavesNewProject() {
        final String projectName = "projectname";

        ProjectRepository projectRepository = mock(ProjectRepository.class);
        wbsService.setProjectRepository(projectRepository);

        Project projectActual = wbsService.createProject(projectName);

        ArgumentCaptor<Project> project = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(eq(username), project.capture());

        assertEquals(projectActual, project.getValue());
        assertEquals(projectName, projectActual.getName());
    }

    @Test
    public void givenAUsernameListReturnsAllProjects() {
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        wbsService.setProjectRepository(projectRepository);

        List<Project> expected = Arrays.asList(mock(Project.class));
        when(projectRepository.findProjectByUsername(username)).thenReturn(expected);

        List<Project> actual = wbsService.findProjectsByCurrentUser();

        assertEquals(expected, actual);
    }

    @Test
    public void givenAProjectIdListReturnsAllSheets() {
        final String projectId = "42342";

        SheetRepository sheetRepository = mock(SheetRepository.class);
        wbsService.setSheetRepository(sheetRepository);

        List<Sheet> expected = Arrays.asList(mock(Sheet.class));
        when(sheetRepository.findByProjectId(projectId)).thenReturn(expected);

        List<Sheet> actual = wbsService.findSheetsByProjectId(projectId);

        assertEquals(expected, actual);
        
        verify(authorizationService).checkPermissionByProjectId(projectId);
    }

    @Test
    public void givenASheetIdFetchDetailReturnsSheetDetail() {
        final String sheetId = "3423423423";

        TaskRepository taskRepository = mock(TaskRepository.class);
        SheetRepository sheetRepository = mock(SheetRepository.class);
        
        wbsService.setSheetRepository(sheetRepository);
        wbsService.setTaskRepository(taskRepository);

        Sheet expected = mock(Sheet.class);
        Task rootExpected = mock(Task.class);

        when(sheetRepository.findById(sheetId)).thenReturn(expected);
        when(taskRepository.findRootById(sheetId)).thenReturn(rootExpected);

        Sheet actual = wbsService.fetchSheetDetail(sheetId);

        assertEquals(expected, actual);

        verify(actual).setRoot(rootExpected);
        
        verify(authorizationService).checkPermissionBySheetId(sheetId);
    }

    @Test
    public void givenASheetNameAndProjectIdCreateSheetSaves() {
        final String projectId = "423423";
        final String sheetName = "sheetName";

        TaskRepository taskRepository = mock(TaskRepository.class);
        SheetRepository sheetRepository = mock(SheetRepository.class);
        wbsService.setSheetRepository(sheetRepository);
        wbsService.setTaskRepository(taskRepository);

        Sheet sheetActual = wbsService.createSheet(projectId, sheetName);

        ArgumentCaptor<Sheet> sheetExpected = ArgumentCaptor.forClass(Sheet.class);

        verify(sheetRepository).save(eq(projectId), sheetExpected.capture());
        verify(taskRepository).save(anyString(), eq(sheetActual.getRoot()));

        assertEquals(sheetExpected.getValue(), sheetActual);
        assertEquals(sheetName, sheetActual.getName());
        assertNotNull(sheetActual.getRoot());
        
        verify(authorizationService).checkPermissionByProjectId(projectId);
    }

    @Test
    public void givenATaskParentIdCreateSavesTask() {
        final String parentTaskId = "42423423";

        TaskRepository taskRepository = mock(TaskRepository.class);
        wbsService.setTaskRepository(taskRepository);

        final ArgumentCaptor<Task> actual = ArgumentCaptor.forClass(Task.class);

        Task task = wbsService.createTask(parentTaskId);

        verify(taskRepository).save(eq(parentTaskId), actual.capture());

        assertEquals(task, actual.getValue());
        
        verify(authorizationService).checkPermissionByTaskId(parentTaskId);
    }

    @Test
    public void givenATaskIdEffortAndNameUpdateSaves() {
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
        
        verify(authorizationService).checkPermissionByTaskId(taskId);
    }

    @Test
    public void givenATaskIdDeleteCallsDeleteTask() {
        final String taskId = "434234";

        TaskRepository taskRepository = mock(TaskRepository.class);
        wbsService.setTaskRepository(taskRepository);

        wbsService.deleteTask(taskId);

        verify(taskRepository).delete(taskId);
        
        verify(authorizationService).checkPermissionByTaskId(taskId);
    }

    @Test
    public void givenAUsernameAndProjectAddingMembershipCallsRepository() {
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        wbsService.setProjectRepository(projectRepository);
        wbsService.setUserRepository(userRepository);

        final String projectId = "423424";
        final String userId = "423423";

        when(userRepository.getId(username)).thenReturn(userId);

        wbsService.addMemberToProject(projectId, username);

        verify(projectRepository).addMemberToProject(projectId, userId);
        
        verify(authorizationService).checkPermissionByProjectId(projectId);
    }

    @Test
    public void givenASheetIdGrantPublicSetsKey() {
        final String sheetId = "423422";
        final String publicSecret = "secretkey";
        final RandomGenerator randomGenerator = mock(RandomGenerator.class);
        final SheetRepository sheetRepository = mock(SheetRepository.class);

        wbsService.setRandomGenerator(randomGenerator);
        wbsService.setSheetRepository(sheetRepository);

        Sheet sheet = new Sheet();

        when(randomGenerator.generateSecret()).thenReturn(publicSecret);
        when(sheetRepository.findById(sheetId)).thenReturn(sheet);

        wbsService.grantPublicRead(sheetId);

        verify(sheetRepository).update(sheet);

        assertEquals(publicSecret, sheet.getPublicSecret());
        
        verify(authorizationService).checkPermissionBySheetId(sheetId);
    }

    @Test
    public void givenAPublicKeyReturnsSheet() {
        final String publicSecret = "42342423";
        final SheetRepository sheetRepository = mock(SheetRepository.class);

        wbsService.setSheetRepository(sheetRepository);

        Sheet sheet = new Sheet();
        when(sheetRepository.findByPublicSecret(publicSecret)).thenReturn(sheet);

        Sheet actual = wbsService.findSheetByPublicSecret(publicSecret);

        assertEquals(sheet, actual);
    }
}
