/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
/**
 *
 * @author sihaya
 */
public class UserRepositoryTest extends WbsIntegrationTest {
    @Test
    public void given_a_user_saving_it_persists() {
        User user = mock(User.class);
        
        final String email = "email@email.com";
        
        when(user.getUsername()).thenReturn("pete");
        when(user.getEmail()).thenReturn(email);
        when(user.getPassword()).thenReturn("abcde");
        
        UserRepository userRepository = new UserRepository();
        userRepository.setSession(session);
        
        userRepository.save(user);
        
        User actual = userRepository.authenticate("pete", "abcde");
        
        assertEquals("pete", actual.getUsername());
        assertEquals(email, actual.getEmail());
    }
}
