/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.List;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sihaya
 */
public class ProjectRepositoryTest extends WbsIntegrationTest {

    private String username;
    private ProjectRepository projectRepository;
    private static int number;

    @Before
    public void setUp() throws Exception {
        projectRepository = new ProjectRepository();
        username = "username1" + number++;

        keepaliveSession.getRootNode().getNode("wbs").addNode(username);
        keepaliveSession.save();

        projectRepository.setRepository(repository);

        NodeIterator iter = keepaliveSession.getRootNode().getNodes();
        while (iter.hasNext()) {
            Node node = iter.nextNode();
            String name = node.getPath();
            
            System.out.println(name);
        }
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
}
