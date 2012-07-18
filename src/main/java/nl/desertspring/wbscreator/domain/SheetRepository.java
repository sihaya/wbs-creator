/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

/**
 *
 * @author sihaya
 */
public class SheetRepository {

    private NodeUtil nodeUtil;
    private Session session;

    public void save(Sheet sheet) {
        try {
            handleSave(sheet);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void handleSave(Sheet sheet) throws Exception {
        Node projectNode = nodeUtil.getProjectNode(sheet.getProject());

        Node sheetNode;
        if (!projectNode.hasNode(sheet.getName())) {
            sheetNode = projectNode.addNode(sheet.getName());
            sheet.setSheetId(sheetNode.getIdentifier());
        } else {
            sheetNode = projectNode.getNode(sheet.getName());

            if (sheetNode.hasNodes()) {
                NodeIterator iter = sheetNode.getNodes();
                while (iter.hasNext()) {
                    iter.next();
                    iter.remove();
                }
            }
        }
        
        createTaskNode(sheetNode, sheet.getRoot());
    }
    
    private void createTaskNode(Node parent, Task task) throws Exception {
        Node taskNode = parent.addNode("task");
        taskNode.setProperty("name", task.getName());
        
        if (task.getSubTasks().isEmpty()) {
            taskNode.setProperty("effort", task.getEffort());            
        } else {
            for(Task subTask : task.getSubTasks()) {
                createTaskNode(taskNode, subTask);
            }
        }        
    }

    public Sheet findById(String id) {
        try {
            return handleFindById(id);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Sheet handleFindById(String id) throws Exception {
        Sheet sheet = new Sheet();

        Node node = session.getNodeByIdentifier(id);

        sheet.setName(node.getName());

        if (node.hasNode("task")) {
            Task root = createTaskFromNode(node.getNode("task"));
            sheet.setRoot(root);
        }
        
        return sheet;
    }

    private Task createTaskFromNode(Node node) throws Exception {
        Task task = new Task();
        task.setName(node.getProperty("name").getString());
        
        if (!node.hasNodes()) {
            task.setEffort(((Long)node.getProperty("effort").getLong()).intValue());
        } else {
            NodeIterator iter = node.getNodes();
            List<Task> subTasks = new ArrayList<Task>();
            
            while(iter.hasNext()) {
                Node subTask = iter.nextNode();
                
                subTasks.add(createTaskFromNode(subTask));
            }
            
            task.setSubTasks(subTasks);
        }
        
        return task;
    }
}
