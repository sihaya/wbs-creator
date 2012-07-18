/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test specs for project.
 *
 * @author sihaya
 */
public class ProjectTest {
    private User user = new User();
    
    @Test
    public void newly_created_sheet_is_present_in_list_of_worksheets() {
        Project project = user.createProject("new");
        
        Sheet sheet = project.createSheet("mysheet");
        
        assertTrue(project.getSheets().contains(sheet));
    }
    
    @Test
    public void newly_created_sheet_contains_correct_attributes() {        
        Project project = user.createProject("new");
        
        final String name = "mysheet";
        Sheet sheet = project.createSheet(name);
        
        assertEquals(name, sheet.getName());        
    }
    
    @Test
    public void adding_a_user_to_a_project_gives_it_write_access() {
        
    }
}
