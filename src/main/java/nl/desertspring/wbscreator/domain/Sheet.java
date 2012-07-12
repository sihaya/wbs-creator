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
    private Integer sheetId;
    private String name;

    public Integer getId() {
        return sheetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Task getRoot() {
        return null;
    }

    public void replace(Task unsavedRootTask) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
