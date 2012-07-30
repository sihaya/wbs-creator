/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.ejb.Stateless;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Responsible for constructing a User based on a node.
 *
 * @author sihaya
 */
@Stateless
public class UserFactory {

    public User create(Node userNode) throws RepositoryException {
        User user = new User();
        
        user.setEmail(userNode.getProperty("email").getString());
        user.setUsername(userNode.getName());
        
        return user;
    }
}
