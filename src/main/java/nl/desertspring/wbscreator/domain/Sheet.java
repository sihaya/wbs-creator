/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

/**
 * A sheet containing a work breakdown structure with a single root.
 *
 * @author sihaya
 */
public class Sheet {

    private String sheetId;
    private String name;    
    private Task root;

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }

    public String getSheetId() {
        return sheetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Task getRoot() {
        return root;
    }
    
    public void setRoot(Task root) {
        this.root = root;
    }
}
