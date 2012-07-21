/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 *
 * @author sihaya
 */
@Stateless
public class UserRepository {

    private Repository repository;

    public void save(User user) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            Node wbs = session.getRootNode().getNode("wbs");

            if (wbs.hasNode(user.getUsername())) {
                throw new IllegalStateException("User already exists");
            }

            Node userNode = wbs.addNode(user.getUsername());
            userNode.setProperty("email", user.getEmail());
            userNode.setProperty("password", user.getPassword());

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            session.logout();
        }
    }

    public User authenticate(String username, String password) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node wbs = session.getNode("/wbs");

            if (!wbs.hasNode(username)) {
                throw new IllegalStateException("No user by this name");
            }

            Node userNode = wbs.getNode(username);

            if (!userNode.getProperty("password").getString().equals(password)) {
                throw new IllegalStateException("Incorrect password");
            }

            User user = new User();
            user.setEmail(userNode.getProperty("email").getString());
            user.setUsername(username);

            return user;
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            session.logout();
        }
    }

    @Resource(name = ResourceConstants.REPOSITORY)
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
