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
import javax.inject.Inject;
import javax.jcr.*;
import javax.jcr.query.Query;

/**
 *
 * @author sihaya
 */
@Stateless
public class ProjectRepository {
    
    private Repository repository;
    private UserFactory userFactory;

    public void save(String username, Project project) {
        handleSave(username, project);
    }
    
    public Project fetchByProjectId(String projectId) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            return createProject(session.getNodeByIdentifier(projectId));
        } catch(RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }
    
    public Project fetchBySheetId(String sheetId) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node sheetNode = session.getNodeByIdentifier(sheetId);
            
            return createProject(sheetNode.getParent());
        } catch(RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }
    
    public Project fetchByTaskId(String taskId) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node node = session.getNodeByIdentifier(taskId);
            
            do {
                node = node.getParent();
            } while (!node.hasProperty("projectName"));
            
            return createProject(node);
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    public List<Project> findProjectByUsername(String username) {
        Session session = null;

        List<Project> result = new ArrayList<Project>();        
        try {
            session = SessionUtil.login(repository);
            
            Node usernode = session.getRootNode().getNode("wbs").getNode(username);
            NodeIterator nodes = usernode.getNodes();
            while (nodes.hasNext()) {
                Node projectNode = nodes.nextNode();
                Project project = createProject(projectNode);

                result.add(project);
            }
            
            
            NodeIterator otherProjects = session.getWorkspace().getQueryManager().createQuery(
                    "//members/member[userId='" + usernode.getIdentifier() + "']", 
                    Query.XPATH).execute().getNodes();
            while(otherProjects.hasNext()) {
                Node projectNode = otherProjects.nextNode().getParent().getParent();
                
                Project project = createProject(projectNode);
                result.add(project);
            }
            
            return result;
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally { 
            SessionUtil.logout(session);
        }
    }

    private Project createProject(Node projectNode) throws RepositoryException {
        Project project = new Project();
        project.setProjectId(projectNode.getIdentifier());
        project.setName(projectNode.getProperty("projectName").getString());
        List<User> membersResult = new ArrayList<User>();
        NodeIterator members = projectNode.getNode("members").getNodes();
        while(members.hasNext()) {
            Node memberNode = members.nextNode();                    
            Node userNode = memberNode.getProperty("userId").getNode();
            
            membersResult.add(userFactory.create(userNode));
        }
        project.setMembers(membersResult);
        return project;
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

            projectNode.addNode("members");
            projectNode.setProperty("projectName", project.getName());
            project.setProjectId(projectNode.getIdentifier());

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }
    
    public void addMemberToProject(String projectId, String userId) {
        Session session = null;        
        try {
            session = SessionUtil.login(repository);
                                    
            Node projectNode = session.getNodeByIdentifier(projectId);
                        
            Node membersNode = projectNode.getNode("members");
            Node memberNode = membersNode.addNode("member");
            
            memberNode.setProperty("userId", session.getNodeByIdentifier(userId));
            
            session.save();
        } catch(RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    @Inject
    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }
}
