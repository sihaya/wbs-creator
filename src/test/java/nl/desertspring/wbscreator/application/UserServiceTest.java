/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import nl.desertspring.wbscreator.domain.User;
import nl.desertspring.wbscreator.domain.UserFactory;
import nl.desertspring.wbscreator.domain.UserRepository;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author sihaya
 */
public class UserServiceTest {
    @Test
    public void given_username_create_user_constructs_user_and_saves() {
        UserRepository userRepository = mock(UserRepository.class);
        UserFactory userFactory = mock(UserFactory.class);
        
        UserService userService = new UserService();
        userService.setUserRepository(userRepository);
        userService.setUserFactory(userFactory);
        
        String username = "123";
        String password = "432423432";
        String email = "remail@ffew.com";
        
        User expected = mock(User.class);
        
        when(userFactory.createUser(username, password, email)).thenReturn(expected);
        
        User user = userService.createUser(username, password, email);
        
        assertEquals(expected, user);        
        verify(userRepository).save(expected);
    }
}
