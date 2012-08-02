/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import java.util.Collections;
import java.util.Set;
import nl.desertspring.wbscreator.domain.User;
import nl.desertspring.wbscreator.domain.UserRepository;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *
 * @author sihaya
 */
public class WbsRealm extends AuthorizingRealm {    
    private UserRepository userRepository;
    
    private static final Set<String> roles = Collections.singleton("user");
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo(roles);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
        
        User user = getUserRepository().authenticate(usernamePasswordToken.getUsername(), usernamePasswordToken.getPassword());
        
        return new SimpleAuthenticationInfo(user.getUsername(), token, getName());
    }
    
    private UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }    
}
