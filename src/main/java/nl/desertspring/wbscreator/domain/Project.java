/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sihaya
 */
@XmlRootElement
public class Project implements Serializable {
    private List<Sheet> sheets;
    private List<User> members;
    private String name;
    private String projectId;
    private User owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }    
    
    public boolean hasPermission(String username) {
        if (owner.getUsername().equals(username)) {
            return true;
        }
        
        for(User user : members) {
            if (username.equals(user.getUsername())) {
                return true;
            }
        }
        
        return false;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public List<Sheet> getSheets() {
        return sheets;
    }

    public void setSheets(List<Sheet> sheets) {
        this.sheets = sheets;
    }    
}
