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
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import nl.desertspring.wbscreator.domain.Task;

/**
 *
 * @author sihaya
 */
@Stateless
public class TaskRepository {

    private Session session;

    public void save(String parentTaskId, Task newTask) {
        try {
            Node parentNode = session.getNodeByIdentifier(parentTaskId);

            Node taskNode = parentNode.addNode("task");
            doSetProperties(taskNode, newTask);

            session.save();

            newTask.setTaskId(taskNode.getIdentifier());
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Task findById(String taskId) {
        try {
            Node node = session.getNodeByIdentifier(taskId);

            return createTaskFromNode(node);
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void save(Task task) {
        if (task.getTaskId() == null) {
            throw new IllegalArgumentException("Cannot update unsaved task");
        }

        try {
            Node node = session.getNodeByIdentifier(task.getTaskId());

            doSetProperties(node, task);

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void delete(String taskId) {
        try {
            Node node = session.getNodeByIdentifier(taskId);
            node.remove();

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
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
        try {
            Node sheetNode = session.getNodeByIdentifier(sheetId);

            if (!sheetNode.hasNode("task")) {
                throw new IllegalStateException("No root for this node");
            }

            Node node = sheetNode.getNode("task");

            return findById(node.getIdentifier());
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Resource(name = ResourceConstants.REPOSITORY)
    public void setSession(Session session) {
        this.session = session;
    }
}
