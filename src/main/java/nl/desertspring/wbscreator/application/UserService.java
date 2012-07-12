/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import javax.annotation.Resource;
import nl.desertspring.wbscreator.domain.User;
import nl.desertspring.wbscreator.domain.UserFactory;
import nl.desertspring.wbscreator.domain.UserRepository;

/**
 *
 * @author sihaya
 */
public class UserService {

    private UserRepository userRepository;
    private UserFactory userFactory;

    public User createUser(String username, String password, String email) {
        User user = userFactory.createUser(username, password, email);
        
        userRepository.save(user);
        
        return user;
    }

    @Resource
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Resource
    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }
}
