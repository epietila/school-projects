package fi.tuni.prog3.sisuproject;

import com.google.gson.JsonObject;
import java.util.TreeMap;
import javafx.util.Pair;

/**
 * A class representing the progress of one student in one degree 
 */
public class Progress {

    private final TreeMap<String, Integer> courses; 
    private int totalCredits;
    private final int requiredCredits;
    
    public Progress(Degree degree){
        this.courses = new TreeMap<>();
        this.totalCredits = 0;
        if (degree.getMinCredits() != null ) {
            this.requiredCredits = Integer.parseInt(degree.getMinCredits());
        } else {
            this.requiredCredits = 0;
        }
    }
    
    public void addCompletedCourse(JsonObject course) {

        this.totalCredits += course.getAsJsonObject("credits").get("min").getAsInt();
        
        if (course.getAsJsonObject("name").get("fi") != null) {
            this.courses.put(course.getAsJsonObject("name").get("fi").getAsString(),
                course.getAsJsonObject("credits").get("min").getAsInt());
        } else {
            this.courses.put(course.getAsJsonObject("name").get("en").getAsString(),
                course.getAsJsonObject("credits").get("min").getAsInt());
        }
    }
    
    public void removeCompletedCourse(JsonObject course) {
        if (course.getAsJsonObject("name").get("fi") != null) {
            this.courses.remove(course.getAsJsonObject("name").get("fi")
                    .getAsString());
        } else {
            this.courses.remove(course.getAsJsonObject("name").get("en")
                    .getAsString());
        }
        this.totalCredits -= course.getAsJsonObject("credits").get("min").getAsInt();
    }
    
    public Pair<Integer, Integer> getProgress(){
        Pair prog = new Pair(this.totalCredits, this.requiredCredits);
        return prog;
    }
    
    public TreeMap<String, Integer> getCompletedCourses() {
        TreeMap<String, Integer> courseMap;
        courseMap = (TreeMap) courses.clone();
        return courseMap;
    }
    
}
