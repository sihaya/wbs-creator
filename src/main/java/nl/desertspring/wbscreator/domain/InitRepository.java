/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 *
 * @author sihaya
 */
@Singleton
@Startup
public class InitRepository {

    private Repository repository;

    @Resource(name = ResourceConstants.REPOSITORY)
    public void setRepository(Repository session) {
        this.repository = session;
    }

    @PostConstruct
    public void init() {
        Session session = null;
        
        try {
            session = SessionUtil.login(repository);
            
            if (session.nodeExists("/wbs")) {
                return;
            }

            session.getRootNode().addNode("wbs");

            session.save();            
        } catch (RepositoryException ex) {
            Logger.getLogger(InitRepository.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            session.logout();
        }
    }
}
