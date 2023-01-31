/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisuproject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
    
    /**
     * Test of getName method, of class Student.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Student instance = new Student("Ronja Koivisto", "testi", 2000, 2001);
        String expResult = "Ronja Koivisto";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        Student instance2 = new Student("Essi Pietilä", "testi", 2000, 2001);
        expResult = "Essi Pietilä";
        result = instance2.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStudentNumber method, of class Student.
     */
    @Test
    public void testGetStudentNumber() {
        System.out.println("getStudentNumber");
        Student instance = new Student("testi", "2e89hfw", 2000, 2001);
        String expResult = "2e89hfw";
        String result = instance.getStudentNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStartYear method, of class Student.
     */
    @Test
    public void testGetStartYear() {
        System.out.println("getStartYear");
        Student instance = new Student("testi", "testi", 2000, 2001);
        int expResult = 2000;
        int result = instance.getStartYear();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEndYear method, of class Student.
     */
    @Test
    public void testGetEndYear() {
        System.out.println("getEndYear");
        Student instance = new Student("testi", "testi", 2000, 2001);
        int expResult = 2001;
        int result = instance.getEndYear();
        assertEquals(expResult, result);
    }
    
}
