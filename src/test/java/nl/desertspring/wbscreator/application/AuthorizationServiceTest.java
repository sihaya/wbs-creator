/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import nl.desertspring.wbscreator.domain.Project;
import static org.mockito.Mockito.*;
import nl.desertspring.wbscreator.domain.ProjectRepository;
import nl.desertspring.wbscreator.domain.SecurityUtil;
import org.apache.shiro.authz.AuthorizationException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 *
 * @author sihaya
 */
public class AuthorizationServiceTest {

    final String username = "username1";
    Project project;
    AuthorizationService service;
    ProjectRepository projectRepository;

    @Before
    public void setUp() {
        project = mock(Project.class);
        service = new AuthorizationService();

        SecurityUtil securityUtil = mock(SecurityUtil.class);
        when(securityUtil.getSubjectUsername()).thenReturn(username);
        service.setSecurityUtil(securityUtil);

        projectRepository = mock(ProjectRepository.class);
        service.setProjectRepository(projectRepository);
    }

    @Test(expected = AuthorizationException.class)
    public void givenAProjectWithoutPermissionCheckPermissionThrowsException() {
        final String projectId = "423424323242";
        when(projectRepository.fetchByProjectId(projectId)).thenReturn(project);
        when(project.hasPermission(username)).thenReturn(false);

        service.checkPermissionByProjectId(projectId);
    }
    
    @Test
    public void givenAProjectWithPermissionCheckPermissionDoesNotThrowException() {
        final String projectId = "423424323242";
        when(projectRepository.fetchByProjectId(projectId)).thenReturn(project);
        when(project.hasPermission(username)).thenReturn(true);

        service.checkPermissionByProjectId(projectId);
    }

    @Test(expected = AuthorizationException.class)
    public void givenASheetWithoutPermissionCheckPermissionThrowsException() {
        final String sheetId = "423424323242";

        when(projectRepository.fetchBySheetId(sheetId)).thenReturn(project);
        when(project.hasPermission(username)).thenReturn(false);

        service.checkPermissionBySheetId(sheetId);
    }
    
    @Test
    public void givenASheetWithPermissionCheckPermissionDoesNotThrowException() {
        final String sheetId = "423424323242";

        when(projectRepository.fetchBySheetId(sheetId)).thenReturn(project);
        when(project.hasPermission(username)).thenReturn(true);

        service.checkPermissionBySheetId(sheetId);
    }
}
