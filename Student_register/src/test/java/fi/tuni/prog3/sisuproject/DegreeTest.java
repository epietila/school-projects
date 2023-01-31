/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisuproject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author essipi
 */
public class DegreeTest {
   
    /**
     * Test of getDegreeName method, of class Degree.
     */
    @Test
    public void testGetDegreeName() {
        System.out.println("getDegreeName");
        String expResult = "nimi";
        Degree instance = new Degree(expResult, "0", "50", expResult);
        String result = instance.getDegreeName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinCredits method, of class Degree.
     */
    @Test
    public void testGetMinCredits() {
        System.out.println("getMinCredits");
        Degree instance = new Degree("name", "0", "50", "id");
        String expResult = "0";
        String result = instance.getMinCredits();
        assertEquals(expResult, result);
        
        Degree instance2 = new Degree("name", null, "50", "id");
        expResult = null;
        result = instance2.getMinCredits();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxCredits method, of class Degree.
     */
    @Test
    public void testGetMaxCredits() {
        System.out.println("getMaxCredits");
        Degree instance = new Degree("name", "0", "50", "id");
        String expResult = "50";
        String result = instance.getMaxCredits();
        assertEquals(expResult, result);
        
        Degree instance2 = new Degree("name", "0", null, "id");
        expResult = null;
        result = instance2.getMaxCredits();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDegreeId method, of class Degree.
     */
    @Test
    public void testGetDegreeId() {
        System.out.println("getDegreeId");
        String expResult = "tamapa-hyva-100";
        Degree instance = new Degree("name", "0", "50", expResult);
        String result = instance.getDegreeId();
        assertEquals(expResult, result);
    }
    
}
