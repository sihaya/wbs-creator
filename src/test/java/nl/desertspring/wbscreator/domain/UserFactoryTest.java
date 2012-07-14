/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author sihaya
 */
public class UserFactoryTest {
    @Test
    public void given_username_create_user_sets_properties() {
        String username = "username";
        String password = "1234";
        String email = "email";
        
        UserFactory userFactory = new UserFactory();
        User user = userFactory.createUser(username, password, email);
        
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
    }
    
    
}
