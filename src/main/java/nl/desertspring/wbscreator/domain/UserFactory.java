/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

/**
 *
 * @author sihaya
 */
public class UserFactory {
    public User createUser(String username, String password, String email) {
         User user = new User();
         
         user.setEmail(email);
         user.setPassword(password);
         user.setUsername(username);
         
         return user;
    }
}
