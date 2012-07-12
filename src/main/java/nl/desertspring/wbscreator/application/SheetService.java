/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import javax.annotation.Resource;
import nl.desertspring.wbscreator.domain.Sheet;
import nl.desertspring.wbscreator.domain.SheetRepository;
import nl.desertspring.wbscreator.domain.Task;

/**
 *
 * @author sihaya
 */
public class SheetService {
    @Resource
    private SheetRepository sheetRepository;

    void setSheetRepository(SheetRepository sheetRepository) {
        this.sheetRepository = sheetRepository;
    }

    void updateTasks(Integer id, Task unsavedRootTask) {
        Sheet sheet = sheetRepository.findById(id);
        
        sheet.replace(unsavedRootTask);
        
        sheetRepository.save(sheet);
    }
    
}
