/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 *
 * @author sihaya
 */
public class NodeUtil {
    private Session session;

    Node getProjectNode(Project project) {
        try {
            return getUserNode(project.getUser()).getNode(project.getName());
        } catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
    public Node getUserNode(User user) {
        try {
            return session.getNode("/wbs/" + user.getUsername());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        
    }
}
