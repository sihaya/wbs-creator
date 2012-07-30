/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author sihaya
 */
public class UserFactoryTest {
    
    @Test
    public void given_a_node_constructs_creates_user() throws RepositoryException {
        Node node = mock(Node.class, RETURNS_DEEP_STUBS);
        
        final String username = "pete";
        final String email = "email@dot.com";
        
        when(node.getName()).thenReturn(username);
        when(node.getProperty("email").getString()).thenReturn(email);
        
        UserFactory userFactory = new UserFactory();
        
        User actual = userFactory.create(node);
        
        assertEquals(username, actual.getUsername());
        assertEquals(email, actual.getEmail());
    }
}
