/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sihaya
 */
public class Project {

    private List<Sheet> sheets = new ArrayList<Sheet>();
    private User user;
    private String name;

    public Sheet createSheet(String sheetName) {
        Sheet sheet = new Sheet();
        sheet.setName(sheetName);

        sheets.add(sheet);

        return sheet;
    }

    public List<Sheet> getSheets() {
        return sheets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
