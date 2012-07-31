/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A sheet containing a work breakdown structure with a single root.
 *
 * @author sihaya
 */
@XmlRootElement
public class Sheet {

    private String sheetId;
    private String name;    
    private Task root;
    private String publicSecret;

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

    @XmlTransient
    public String getPublicSecret() {
        return publicSecret;
    }

    public void setPublicSecret(String publicSecret) {
        this.publicSecret = publicSecret;
    }
}
