/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.List;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author sihaya
 */
public class ProjectRepositoryTest extends WbsIntegrationTest {

    private String username;
    private ProjectRepository projectRepository;
    private static int number;
    private String otherUserId;
    private String otherUsername;
    private Node otherUserNode;

    @Before
    public void setUp() throws Exception {
        projectRepository = new ProjectRepository();
        username = "username1" + number++;
        otherUsername = "otherusername1" + number++;

        keepaliveSession.getRootNode().getNode("wbs").addNode(username);

        otherUserNode = keepaliveSession.getRootNode().getNode("wbs").addNode(otherUsername);
        otherUserNode.addMixin("mix:referenceable");
        otherUserId = otherUserNode.getIdentifier();

        keepaliveSession.save();

        projectRepository.setRepository(repository);

        NodeIterator iter = keepaliveSession.getRootNode().getNodes();
        while (iter.hasNext()) {
            Node node = iter.nextNode();
            String name = node.getPath();

            System.out.println(name);
        }

        keepaliveSession.save();

        Session other = SessionUtil.login(repository);
        keepaliveSession.logout();
        keepaliveSession = other;
    }

    @Test
    public void given_a_new_project_saving_sets_pk() throws RepositoryException {
        final String projectName = "42423";

        Project project = new Project();
        project.setName(projectName);

        projectRepository.save(username, project);

        assertNotNull(project.getProjectId());
    }

    @Test
    public void given_a_username_find_returns_projects() {
        final String projectName = "423423";

        Project project = new Project();
        project.setName(projectName);

        projectRepository.save(username, project);

        List<Project> result = projectRepository.findProjectByUsername(username);

        assertEquals(1, result.size());
    }

    @Test
    public void given_a_userid_add_membership_adds_user_to_members() throws RepositoryException {
        final String projectName = "423423";

        UserFactory userFactory = mock(UserFactory.class);
        Project project = new Project();
        project.setName(projectName);

        projectRepository.save(username, project);
        projectRepository.setUserFactory(userFactory);
        projectRepository.addMemberToProject(project.getProjectId(), otherUserId);

        ArgumentCaptor<Node> node = ArgumentCaptor.forClass(Node.class);
        User expected = mock(User.class);
        when(userFactory.create(node.capture())).thenReturn(expected);

        List<User> members = projectRepository.findProjectByUsername(username).get(0).getMembers();

        assertEquals(1, members.size());
        assertEquals(expected, members.get(0));
        assertEquals(otherUserId, node.getValue().getIdentifier());
    }

    @Test
    public void given_a_username_find_returns_member_projects() {
        projectRepository.setUserFactory(mock(UserFactory.class));
        
        final String projectName = "423423";

        Project project = new Project();
        project.setName(projectName);

        projectRepository.save(username, project);        
        projectRepository.addMemberToProject(project.getProjectId(), otherUserId);
        
        List<Project> projects = projectRepository.findProjectByUsername(otherUsername);
        
        assertEquals(1, projects.size());        
    }
    
    @Test
    public void given_a_project_id_fetch_by_project_id_returns_project() {
        final String projectName = "42423";

        Project project = new Project();
        project.setName(projectName);

        projectRepository.save(username, project);
        Project actual = projectRepository.fetchByProjectId(project.getProjectId());
        
        assertEquals(projectName, actual.getName());
    }
    
    @Test
    public void given_a_sheet_id_fetch_by_sheet_id_returns_project() throws Throwable {
        final String projectName = "42423";

        Project project = new Project();
        project.setName(projectName);

        projectRepository.save(username, project);
        String sheetId = createFakeSheetNode(project);
        
        Project actual = projectRepository.fetchBySheetId(sheetId);
        
        assertEquals(projectName, actual.getName());
    }
    
    @Test
    public void given_a_task_id_fetch_by_task_id_returns_project() throws Throwable {
        final String projectName = "42423";

        Project project = new Project();
        project.setName(projectName);

        projectRepository.save(username, project);
        String sheetId = createFakeSheetNode(project);
        String taskId = createFakeTaskNode(sheetId);
        
        Project actual = projectRepository.fetchByTaskId(sheetId);
        
        assertEquals(projectName, actual.getName());
    }

    private String createFakeSheetNode(Project project) throws RepositoryException {
        Session session = SessionUtil.login(repository);
        try {
            Node node = session.getNodeByIdentifier(project.getProjectId());
            Node fakeSheetNode = node.addNode("sheet");
            session.save();
            
            return fakeSheetNode.getIdentifier();
        } finally {
            SessionUtil.logout(session);
        }
    }

    private String createFakeTaskNode(String sheetId) throws RepositoryException {
        Session session = SessionUtil.login(repository);
        try {
            Node node = session.getNodeByIdentifier(sheetId);
            
            Node deepTaskNode = node.addNode("task").addNode("task").addNode("task");
            
            session.save();
            
            return deepTaskNode.getIdentifier();
        } finally {
            SessionUtil.logout(session);
        }
    }
}
