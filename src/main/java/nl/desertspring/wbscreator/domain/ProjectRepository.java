/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 *
 * @author sihaya
 */
public class ProjectRepository {

    private Session session;

    public void save(String username, Project project) {
        handleSave(username, project);
    }

    public List<Project> findProjectByUsername(String username) {
        List<Project> result = new ArrayList<Project>();
        
        try {
            NodeIterator nodes = session.getRootNode().getNode("wbs").getNode(username).getNodes();
            while(nodes.hasNext()) {
                Node projectNode = nodes.nextNode();
                
                Project project = new Project();
                project.setProjectId(projectNode.getIdentifier());
                project.setName(projectNode.getProperty("projectName").getName());
                
                result.add(project);
            }
            
            return result;
        } catch(RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }

    private void handleSave(String username, Project project) {
        try {
            Node projectNode = session.getRootNode().getNode("wbs").getNode(username).addNode("project");

            projectNode.setProperty("projectName", project.getName());
            project.setProjectId(projectNode.getIdentifier());

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
