/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.Arrays;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
    private UserFactory userFactory;
    private Repository repository;

    public void save(User user) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            Node wbs = getWbsNode(session);

            if (wbs.hasNode(user.getUsername())) {
                throw new IllegalStateException("User already exists");
            }

            Node userNode = wbs.addNode(user.getUsername());
            userNode.addMixin("mix:referenceable");
            userNode.setProperty("email", user.getEmail());
            userNode.setProperty("password", user.getPassword().toString());

            session.save();
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            session.logout();
        }
    }
    
    public String getId(String username) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            Node wbs = getWbsNode(session);
            
            return wbs.getNode(username).getIdentifier();
        } catch(RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            session.logout();
        }
    }
    
    private Node getWbsNode(Session session) throws RepositoryException {
        return session.getRootNode().getNode("wbs");
    }

    public User authenticate(String username, char[] password) {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            Node wbs = getWbsNode(session);

            if (!wbs.hasNode(username)) {
                throw new IllegalStateException("No user by this name");
            }

            Node userNode = wbs.getNode(username);

            if (!Arrays.equals(userNode.getProperty("password").getString().toCharArray(), password)) {
                throw new IllegalStateException("Incorrect password");
            }

            return userFactory.create(userNode);
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

    @Inject
    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }
}
