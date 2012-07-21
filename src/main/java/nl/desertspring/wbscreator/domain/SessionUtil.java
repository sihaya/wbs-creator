/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.jcr.*;

/**
 *
 * @author sihaya
 */
public class SessionUtil {

    static Session login(Repository repository) throws LoginException, RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }

    static void logout(Session session) {
        session.logout();
    }
    
}
