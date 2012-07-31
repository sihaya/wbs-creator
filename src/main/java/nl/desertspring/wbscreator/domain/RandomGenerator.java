/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Generate random secrets
 *
 * @author sihaya
 */
@Stateless
public class RandomGenerator {    
    public String generateSecret() {
        return RandomStringUtils.randomAlphabetic(128);
    }
}
