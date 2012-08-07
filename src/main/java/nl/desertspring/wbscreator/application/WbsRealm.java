/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import nl.desertspring.wbscreator.domain.User;
import nl.desertspring.wbscreator.domain.UserRepository;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author sihaya
 */
public class WbsRealm extends AuthorizingRealm implements SessionStorageEvaluator {    
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
        
        return new SimpleAuthenticationInfo(user.getUsername(), usernamePasswordToken.getPassword(), getName());
    }
    
    private UserRepository getUserRepository() {
        return userRepository == null ? lookupUserRepository() : userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserRepository lookupUserRepository() {
        try {
            InitialContext ctx = new InitialContext();
            return (UserRepository) ctx.lookup("java:module/UserRepository");
        } catch (NamingException ex) {
            Logger.getLogger(WbsRealm.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public boolean isSessionStorageEnabled(Subject subject) {
        return false;
    }
}
