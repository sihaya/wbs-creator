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
public class SheetRepositoryTest {
    private UserFactory userFactory = new UserFactory();
    private UserRepository userRepository = new UserRepository();
    private ProjectRepository projectRepository = new ProjectRepository();
    private SheetRepository sheetRepository = new SheetRepository();
    
    private User user;
    private Project project;    
    
    @Before
    public void setUp() throws Exception {                
        user = userFactory.createUser("username", "password", "email");
        userRepository.save(user);
        
        project = user.createProject("myproject");
    }
    
    @Test
    public void given_an_unsaved_sheet_saving_sets_pk() {        
        Sheet sheet = project.createSheet("mysheet");
        sheetRepository.save(sheet);
        
        assertNotNull(sheet.getId());
    }
    
    @Test
    public void given_a_saved_sheet_find_retrieves_it_correctly() {
       Sheet sheet = project.createSheet("anothersheet");
       sheetRepository.save(sheet);
       
       Sheet sheetActual = sheetRepository.findById(sheet.getId());
       
       assertEquals("anothersheet", sheetActual.getName());
    }
    
    @Test
    public void given_a_new_sheet_root_is_not_empty() {
        Sheet sheet = project.createSheet("anothersheet2");
        
        assertNotNull(sheet.getRoot());
    }
}
