/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import nl.desertspring.wbscreator.domain.Task;

/**
 *
 * @author sihaya
 */
public class TaskRepository {

    public void save(String parentTaskId, Task capture) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Task findById(String taskId) {
        return null;
    }

    public void save(Task capture) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void delete(String taskId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createTaskNode(Node parent, Task task) throws Exception {
        Node taskNode = parent.addNode("task");
        taskNode.setProperty("name", task.getName());

        if (task.getSubTasks().isEmpty()) {
            taskNode.setProperty("effort", task.getEffort());
        } else {
            for (Task subTask : task.getSubTasks()) {
                createTaskNode(taskNode, subTask);
            }
        }
    }

    private Task createTaskFromNode(Node node) throws Exception {
        Task task = new Task();
        task.setName(node.getProperty("name").getString());

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
    
    
}
