/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
/**
 *
 * @author sihaya
 */
public class UserRepositoryTest extends WbsIntegrationTest {
    @Test
    public void given_a_user_saving_it_persists() throws RepositoryException {
        User user = mock(User.class);
        UserFactory userFactory = mock(UserFactory.class);
                
        final String email = "email@email.com";
        
        when(user.getUsername()).thenReturn("pete");
        when(user.getEmail()).thenReturn(email);
        when(user.getPassword()).thenReturn("abcde".toCharArray());
        
        UserRepository userRepository = new UserRepository();
        userRepository.setRepository(repository);
        
        userRepository.save(user);
                
        User expected = mock(User.class);
        when(userFactory.create(any(Node.class))).thenReturn(expected);
        userRepository.setUserFactory(userFactory);
                        
        User actual = userRepository.authenticate("pete", "abcde".toCharArray());
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void given_an_exisiting_username_get_by_id_returns_an_id() {
        UserRepository userRepository = new UserRepository();
        userRepository.setRepository(repository);
        
        User user = mock(User.class);
        
        final String username = "petee";
        
        when(user.getUsername()).thenReturn(username);
        when(user.getEmail()).thenReturn("email@email.com");
        when(user.getPassword()).thenReturn("abcde".toCharArray());
        
        userRepository.save(user);
        
        String id = userRepository.getId(username);
        
        assertNotNull(id);
        assertTrue(id.length() > 12);
    }
}
