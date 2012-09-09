/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author sihaya
 */
@Stateless
public class SecurityUtil {

    public Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public String getSubjectUsername() {
        return (String) getSubject().getPrincipal();
    }

    public void login(String username, char[] password) {
        Subject currentUser = SecurityUtils.getSubject();

        currentUser.login(new UsernamePasswordToken(username, password));
    }
}
