/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.jcr.Node;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author sihaya
 */
public class TaskRepositoryTest extends WbsIntegrationTest {
    
    String taskParentId;
    TaskRepository taskRepository;
    static int counter = 1;
    
    @Before
    public void setUp() throws Exception {
        taskRepository = new TaskRepository();
        
        taskRepository.setSession(session);
        
        String username = "username" + counter++;
        
        taskParentId = session.getRootNode().getNode("wbs").addNode(username).addNode("project").addNode("worksheet").addNode("task").getIdentifier();
        session.save();
    }
    
    @Test
    public void givenATaskAndParentIdSavingSetsPk() {
        Task task = new Task();
        task.setEffort(100);
        task.setName("name");
        
        taskRepository.save(taskParentId, task);
        
        assertNotNull(task.getTaskId());
    }
    
    @Test
    public void givenATaskSavingStoresProperties() {
        Task expected = new Task();
        expected.setEffort(100);
        expected.setName("name");
        
        taskRepository.save(taskParentId, expected);
        
        Task actual = taskRepository.findById(expected.getTaskId());
        
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEffort(), actual.getEffort());
    }
    
    @Test
    public void givenAExistingTaskSavingUpdatesProperties() {
        Task expected = new Task();
        expected.setEffort(100);
        expected.setName("name");
        
        taskRepository.save(taskParentId, expected);
        
        expected.setEffort(99);
        expected.setName("naam");
        
        taskRepository.save(expected);
        
        Task actual = taskRepository.findById(expected.getTaskId());
        
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEffort(), actual.getEffort());
    }
    
    @Test
    public void givenASubTaskSavingIncreasesParentsChildren() {
        Task root = new Task();
        root.setEffort(100);
        root.setName("name");
        
        taskRepository.save(taskParentId, root);
        
        Task sub = new Task();
        sub.setEffort(200);
        sub.setName("name");
        
        taskRepository.save(root.getTaskId(), sub);
        
        Task actual = taskRepository.findById(root.getTaskId());
        
        assertEquals(1, actual.getSubTasks().size());
    }
    
    @Test
    public void givenASheetIdFindRootTaskReturnsRoot() {
        Task root = new Task();
        root.setEffort(100);
        root.setName("name");
        
        taskRepository.save(taskParentId, root);
        
        Task actual = taskRepository.findRootById(taskParentId);
        
        assertEquals(root.getName(), actual.getName());
    }
}
