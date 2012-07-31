/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sihaya
 */
public class RandomGeneratorTest {
    @Test
    public void generateReturnsARandomNumberOfLength128() {
        RandomGenerator randomGenerator = new RandomGenerator();
        String number = randomGenerator.generateSecret();
        
        assertEquals(128, number.length());
    }
}
