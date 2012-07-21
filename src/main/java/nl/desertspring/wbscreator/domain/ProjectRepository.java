/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jcr.*;

/**
 *
 * @author sihaya
 */
@Stateless
public class ProjectRepository {

    private Repository repository;

    public void save(String username, Project project) {
        handleSave(username, project);
    }

    public List<Project> findProjectByUsername(String username) {
        Session session = null;

        List<Project> result = new ArrayList<Project>();
        try {
            session = SessionUtil.login(repository);
            NodeIterator nodes = session.getRootNode().getNode("wbs").getNode(username).getNodes();
            while (nodes.hasNext()) {
                Node projectNode = nodes.nextNode();

                Project project = new Project();
                project.setProjectId(projectNode.getIdentifier());
                project.setName(projectNode.getProperty("projectName").getName());

                result.add(project);
            }

            return result;
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally { 
            
        }
    }

    @Resource(name = ResourceConstants.REPOSITORY)
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    private void handleSave(String username, Project project) {
        Session session = null;
        try {
            session = SessionUtil.login(repository);
            Node projectNode = session.getRootNode().getNode("wbs").getNode(username).addNode("project");

            projectNode.setProperty("projectName", project.getName());
            project.setProjectId(projectNode.getIdentifier());

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }
}
