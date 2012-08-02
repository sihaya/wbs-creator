/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.ejb.Stateless;
import org.apache.shiro.SecurityUtils;
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
        return (String)getSubject().getPrincipal();
    }
}
