/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 *
 * @author sihaya
 */
public class InitRepository {

    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    public void init() {
        try {
            if (session.nodeExists("/wbs")) {
                return;
            }

            session.getRootNode().addNode("wbs");

            session.save();
        } catch (RepositoryException ex) {
            Logger.getLogger(InitRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
