/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.io.File;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.core.TransientRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author sihaya
 */
public abstract class WbsIntegrationTest {
    protected static Repository repository;
    protected static Session keepaliveSession;
    protected static InitRepository initRepository;
    
    @BeforeClass
    public static void setupRepos() throws Exception {
        repository = new TransientRepository(new File("./src/test/resources/repository.xml"), new File("./target/repositorytest"));
        keepaliveSession = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        
        initRepository = new InitRepository();
        initRepository.setRepository(repository);
        initRepository.init();
    }
    
    @AfterClass
    public static void shutdownRepos() {
        if (keepaliveSession != null) {
            keepaliveSession.logout();
            keepaliveSession = null;            
        }
        ((TransientRepository)repository).shutdown();
    }
}
