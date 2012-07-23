/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation of a task, represents both leafs and composite tasks.
 *
 * @author sihaya
 */
@XmlRootElement
public class Task implements Serializable {

    private List<Task> subTasks = new ArrayList<Task>();
    private Integer effort;
    private String name;
    private String taskId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEffort() {
        if (subTasks.isEmpty()) {
            return effort;
        } else {
            int sum = 0;

            for (Task subTask : subTasks) {
                sum += subTask.getEffort();
            }

            return sum;
        }
    }

    public void setEffort(Integer effort) {
        this.effort = effort;
    }

    public Task createSubTask(String taskName) {
        Task task = new Task();

        task.setName(taskName);

        subTasks.add(task);

        return task;
    }

    public List<Task> getSubTasks() {
        return Collections.unmodifiableList(subTasks);
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
