/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.List;
import javax.jcr.Session;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Specs for the SheetRepository.
 *
 * <p> This is an integration tests since it hits the JCR repository. </p>
 *
 * @author sihaya
 */
public class SheetRepositoryTest extends WbsIntegrationTest {

    private SheetRepository sheetRepository = new SheetRepository();
    private static int number = 0;
    private String username;
    private String projectId;

    @Before
    public void setUp() throws Exception {
        username = "username1" + number++;
        
        Session session = SessionUtil.login(repository);

        projectId = session.getRootNode().getNode("wbs").addNode(username).addNode("projectName").getIdentifier();
        session.save();
        session.logout();

        sheetRepository.setRepository(repository);
    }

    @Test
    public void given_an_unsaved_sheet_saving_sets_pk() {
        final String name = "sheet1";

        Sheet sheet = new Sheet();
        sheet.setName(name);

        sheetRepository.save(projectId, sheet);

        assertNotNull(sheet.getSheetId());
    }

    @Test
    public void given_a_saved_sheet_find_retrieves_it_correctly() {
        final String name = "anothersheet";

        Sheet sheet = new Sheet();
        sheet.setName(name);

        sheetRepository.save(projectId, sheet);

        Sheet sheetActual = sheetRepository.findById(sheet.getSheetId());

        assertEquals(name, sheetActual.getName());
    }

    @Test
    public void given_a_new_sheet_saving_adds_list_of_sheets() {
        final String name = "anothersheet";

        Sheet sheet = new Sheet();
        sheet.setName(name);

        sheetRepository.save(projectId, sheet);

        List<Sheet> sheets = sheetRepository.findByProjectId(projectId);
        
        assertEquals(1, sheets.size());
    }   
    
    @Test
    public void given_an_existing_task_update_saves() {
        final String name = "anothersheet";

        Sheet sheet = new Sheet();
        sheet.setName(name);
        
        sheetRepository.save(projectId, sheet);
        
        final String publicSecret = "secret";
        sheet.setPublicSecret(publicSecret);
        
        sheetRepository.update(sheet);
        
        Sheet actual = sheetRepository.findById(sheet.getSheetId());
        
        assertEquals(publicSecret, actual.getPublicSecret());
    }
    
    @Test
    public void given_a_public_secret_find_returns() {
        final String name = "anothersheet";

        Sheet sheet = new Sheet();
        sheet.setName(name);
        
        sheetRepository.save(projectId, sheet);
        
        final String publicSecret = "secret534";
        sheet.setPublicSecret(publicSecret);
        
        sheetRepository.update(sheet);
        
        Sheet actual = sheetRepository.findByPublicSecret(sheet.getPublicSecret());
        
        assertEquals(sheet.getSheetId(), actual.getSheetId());
    }
}
