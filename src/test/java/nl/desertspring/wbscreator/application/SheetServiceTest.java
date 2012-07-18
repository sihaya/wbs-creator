/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import nl.desertspring.wbscreator.domain.Sheet;
import nl.desertspring.wbscreator.domain.SheetRepository;
import nl.desertspring.wbscreator.domain.Task;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author sihaya
 */
public class SheetServiceTest {
    
    @Test
    public void given_a_sheet_and_task_structure_calling_update_deletes_old_task_and_replaces_tree_with_new() {
        Sheet sheet = mock(Sheet.class);
        Task unsavedRootTask = mock(Task.class);
        
        SheetService sheetService = new SheetService();
        SheetRepository sheetRepository = mock(SheetRepository.class);
        sheetService.setSheetRepository(sheetRepository);

        when(sheetRepository.findById(sheet.getSheetId())).thenReturn(sheet);
        
        sheetService.updateTasks(sheet.getSheetId(), unsavedRootTask);
        
        verify(sheet).replace(unsavedRootTask);        
        verify(sheetRepository).save(sheet);
    }
}
