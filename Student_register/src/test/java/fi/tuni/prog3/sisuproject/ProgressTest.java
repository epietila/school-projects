/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisuproject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProgressTest {
   
    /**
     * Test of addCompletedCourse method, of class Progress.
     */
    private static String readUrl(String urlString) throws Exception {
        
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read); 
            }

        return buffer.toString();
        } 
    
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    @Test
    public void testAddCompletedCourse() throws Exception {
        System.out.println("addCompletedCourse");
        
        Student student = new Student("testi", "testi", 2000, 2001);
        Degree degree = new Degree("name", "0", "50", "id");
        
        String json = readUrl("https://sis-tuni.funidata.fi/kori/api/"
                + "course-units/by-group-id?groupId=uta-ykoodi-47926&university"
                + "Id=tuni-university-root-id");
        JsonObject course = JsonParser.parseString(json).getAsJsonArray().get(0)
                        .getAsJsonObject();
        
        Progress instance = new Progress(degree);
        instance.addCompletedCourse(course);
        
        Pair<Integer, Integer> totalCredits = instance.getProgress();
        Integer expCredits = 5;
        assertEquals(expCredits, totalCredits.getKey());
        
        int expSize = 1;
        String expName = "Johdatus analyysiin";
        TreeMap<String, Integer> resultMap = instance.getCompletedCourses();
        assertEquals(expSize, resultMap.size());
        assertEquals(expName, resultMap.firstKey());
        assertEquals(expCredits, resultMap.firstEntry().getValue());
        
        // Test with second course
        json = readUrl("https://sis-tuni.funidata.fi/kori/api/course-units/"
                + "by-group-id?groupId=uta-ykoodi-39947"
                + "&universityId=tuni-university-root-id");
        course = JsonParser.parseString(json).getAsJsonArray().get(0)
                        .getAsJsonObject();
        instance.addCompletedCourse(course);
        
        ++expSize;
        resultMap = instance.getCompletedCourses();
        assertEquals(expSize, resultMap.size());
        
        totalCredits = instance.getProgress();
        expCredits = 10;
        assertEquals(expCredits, totalCredits.getKey());
                
    }

    /**
     * Test of removeCompletedCourse method, of class Progress.
     */
    @Test
    public void testRemoveCompletedCourse() throws Exception {
        System.out.println("removeCompletedCourse");
        Student student = new Student("testi", "testi", 2000, 2001);
        Degree degree = new Degree("name", "0", "50", "id");
        
        String json = readUrl("https://sis-tuni.funidata.fi/kori/api/"
                + "course-units/by-group-id?groupId=uta-ykoodi-47926&university"
                + "Id=tuni-university-root-id");
        JsonObject course = JsonParser.parseString(json).getAsJsonArray().get(0)
                        .getAsJsonObject();
        
        Progress instance = new Progress(degree);
        instance.addCompletedCourse(course);
        instance.removeCompletedCourse(course);
        
        Pair<Integer, Integer> totalCredits = instance.getProgress();
        Integer expCredits = 0;
        assertEquals(expCredits, totalCredits.getKey());
        
        int expSize = 0;
        TreeMap<String, Integer> resultMap = instance.getCompletedCourses();
        assertEquals(expSize, resultMap.size());
        
        // Test with two courses
        instance.addCompletedCourse(course);
        
        json = readUrl("https://sis-tuni.funidata.fi/kori/api/course-units/"
                + "by-group-id?groupId=uta-ykoodi-39947"
                + "&universityId=tuni-university-root-id");
        course = JsonParser.parseString(json).getAsJsonArray().get(0)
                        .getAsJsonObject();
        instance.addCompletedCourse(course);
        instance.removeCompletedCourse(course);
        
        expSize = 1;
        resultMap = instance.getCompletedCourses();
        assertEquals(expSize, resultMap.size());
        
        totalCredits = instance.getProgress();
        expCredits = 5;
        assertEquals(expCredits, totalCredits.getKey());
    }

    /**
     * Test of getProgress method, of class Progress.
     */
    /*
    TARVIIKO NÄITÄ KAHTA?
    @Test
    public void testGetProgress() {
        System.out.println("getProgress");
        Progress instance = null;
        Pair<Integer, Integer> expResult = null;
        Pair<Integer, Integer> result = instance.getProgress();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getCompletedCourses method, of class Progress.
     */
    /*
    @Test
    public void testGetCompletedCourses() {
        System.out.println("getCompletedCourses");
        Progress instance = null;
        TreeSet<String> expResult = null;
        TreeSet<String> result = instance.getCompletedCourses();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
}
