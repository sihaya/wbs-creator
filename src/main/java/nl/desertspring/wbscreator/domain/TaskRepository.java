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
import nl.desertspring.wbscreator.domain.Task;

/**
 *
 * @author sihaya
 */
@Stateless
public class TaskRepository {

    private Repository repository;

    public void save(String parentTaskId, Task newTask) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node parentNode = session.getNodeByIdentifier(parentTaskId);

            Node taskNode = parentNode.addNode("task");
            doSetProperties(taskNode, newTask);

            session.save();

            newTask.setTaskId(taskNode.getIdentifier());
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    public Task findById(String taskId) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node node = session.getNodeByIdentifier(taskId);

            return createTaskFromNode(node);
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    public void save(Task task) {
        Session session = null;
                
        if (task.getTaskId() == null) {
            throw new IllegalArgumentException("Cannot update unsaved task");
        }

        try {
            session = SessionUtil.login(repository);
            
            Node node = session.getNodeByIdentifier(task.getTaskId());

            doSetProperties(node, task);

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    public void delete(String taskId) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node node = session.getNodeByIdentifier(taskId);
            node.remove();

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    private Task createTaskFromNode(Node node) throws RepositoryException {
        Task task = new Task();
        task.setName(node.getProperty("name").getString());
        task.setTaskId(node.getIdentifier());

        if (!node.hasNodes()) {
            task.setEffort(((Long) node.getProperty("effort").getLong()).intValue());
        } else {
            NodeIterator iter = node.getNodes();
            List<Task> subTasks = new ArrayList<Task>();

            while (iter.hasNext()) {
                Node subTask = iter.nextNode();

                subTasks.add(createTaskFromNode(subTask));
            }

            task.setSubTasks(subTasks);
        }

        return task;
    }

    private void doSetProperties(Node node, Task task) throws RepositoryException {
        node.setProperty("name", task.getName());
        node.setProperty("effort", task.getEffort());
    }

    public Task findRootById(String sheetId) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node sheetNode = session.getNodeByIdentifier(sheetId);

            if (!sheetNode.hasNode("task")) {
                throw new IllegalStateException("No root for this node");
            }

            Node node = sheetNode.getNode("task");

            return findById(node.getIdentifier());
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    @Resource(name = ResourceConstants.REPOSITORY)
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
