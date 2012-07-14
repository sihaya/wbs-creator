/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sihaya
 */
public class TaskTest {
    @Test
    public void given_a_task_adding_a_subtask_increases_its_list_of_subtasks() {
        Task task = new Task();
        
        Task subTask = task.createSubTask("subtask");
        
        assertEquals(1, task.getSubTasks().size());
    }
    
    @Test
    public void given_a_task_with_some_effort_and_no_subtasks_calcing_effort_returns_own_effort() {
        Task task = new Task();
        
        final Integer effort = 12;
        task.setEffort(effort);
        
        assertEquals(effort, task.getEffort());
    }
    
    @Test
    public void given_a_task_with_subtasks_calcing_effort_returns_sum_of_subtasks() {
        Task task = new Task();
        
        Task subTask1 = task.createSubTask("subtask1");
        subTask1.setEffort(12);
        
        Task subTask2 = task.createSubTask("subtask2");
        subTask2.setEffort(11);
        
        final Integer sum = 23;
        
        assertEquals(sum, task.getEffort());
    }
}
