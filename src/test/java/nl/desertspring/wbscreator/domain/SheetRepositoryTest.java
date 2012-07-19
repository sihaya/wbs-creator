/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Specs for the SheetRepository.
 * 
 * <p>
 * This is an integration tests since it hits the JCR repository.
 * </p>
 *
 * @author sihaya
 */
public class SheetRepositoryTest extends WbsIntegrationTest {
    private UserRepository userRepository = new UserRepository();
    private ProjectRepository projectRepository = new ProjectRepository();
    private SheetRepository sheetRepository = new SheetRepository();
    
    private User user;
    private Project project;    
    private static int number = 0;
    
    @Before
    public void setUp() throws Exception {                
        userRepository.setSession(session);
        
        user = new User();
        user.setUsername("username" + number++);
        user.setPassword("222");
        user.setEmail("user@email.com");
        userRepository.save(user);
        
        project = user.createProject("myproject");
    }
    
    @Test
    public void given_an_unsaved_sheet_saving_sets_pk() {        
        Sheet sheet = project.createSheet("mysheet");
        sheetRepository.save(sheet);
        
        assertNotNull(sheet.getSheetId());
    }
    
    @Test
    public void given_a_saved_sheet_find_retrieves_it_correctly() {
       Sheet sheet = project.createSheet("anothersheet");
       sheetRepository.save(sheet);
       
       Sheet sheetActual = sheetRepository.findById(sheet.getSheetId());
       
       assertEquals("anothersheet", sheetActual.getName());
    }
    
    @Test
    public void given_a_new_sheet_root_is_not_empty() {
        Sheet sheet = project.createSheet("anothersheet2");
        
        assertNotNull(sheet.getRoot());
    }
}
