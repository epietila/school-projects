package fi.tuni.prog3.sisuproject;

/**
 * A class representing a single student
 */
public class Student {
    private final String name;
    private final String studentNumber;
    private int startYear;
    private int endYear;

    /**
     * Constructs a Student object that is initialized with the given name,
     * student number, start year of their studies and the year in which the 
     * student aims to graduate.
     * @param name the name of the student whose information the new student 
     * object stores.
     * @param studentNumber the student number of thse student
     * @param startYear the year in which the student has started their studies
     * @param endYear the year in which the student has graduated/aims to graduate
     */
    public Student(String name, String studentNumber, int startYear, int endYear) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    /**
     * Returns the name of the Student
     * @return the name of the Student
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the student number of the student
     * @return the student number of the student
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * Returns the the year in which the student has started their studies
     * @return the year in which the student has started their studies
     */
    public int getStartYear() {
        return startYear;
    }

    /**
     * Returns the year in which the student has graduated/aims to graduate
     * @return the year in which the student has graduated/aims to graduate
     */
    public int getEndYear() {
        return endYear;
    }
    
    
    
}
