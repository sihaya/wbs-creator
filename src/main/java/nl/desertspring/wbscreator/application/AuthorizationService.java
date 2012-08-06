/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import javax.ejb.Stateless;
import javax.inject.Inject;
import nl.desertspring.wbscreator.domain.Project;
import nl.desertspring.wbscreator.domain.ProjectRepository;
import nl.desertspring.wbscreator.domain.SecurityUtil;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sihaya
 */
@Stateless
public class AuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    private ProjectRepository projectRepository;
    private SecurityUtil securityUtil;

    public void checkPermissionByProjectId(String projectId) {
        checkPermission(projectRepository.fetchByProjectId(projectId));
    }

    public void checkPermissionBySheetId(String sheetId) {
        checkPermission(projectRepository.fetchBySheetId(sheetId));
    }
    
    public void checkPermissionByTaskId(String parentTaskId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void checkPermission(Project project) {
        String username = securityUtil.getSubjectUsername();

        if (!project.hasPermission(username)) {
            logger.warn("User {} does not have access to project with id {}", username, project.getProjectId());
            throw new UnauthorizedException("User is not authorized to access project");
        }
    }

    @Inject
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Inject
    public void setSecurityUtil(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
}
