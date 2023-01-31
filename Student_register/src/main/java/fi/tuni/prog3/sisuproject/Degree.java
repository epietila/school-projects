package fi.tuni.prog3.sisuproject;

/**
 * A class representing a single degree of university
 */
public class Degree {
    private final String name;
    private final String minCredits;
    private final String maxCredits;
    private final String degreeId;
    
    /**
     * Constructs a Degree object that is initialized with the given name,
     * minimum credits, maximum credits and the ID of the degree.
     * @param name the name of the degree whose information the new degree object stores.
     * @param minCredits the number of required credits of the degree
     * @param maxCredits the number of maximum credtis of the degree
     * @param degreeId the ID of the degree
     */
    public Degree (String name, String minCredits, String maxCredits, 
            String degreeId) {
        this.name = name;
        this.minCredits = minCredits;
        this.maxCredits = maxCredits;
        this.degreeId = degreeId;
    }
    
    /**
     * Returns the name of the degree
     * @return the name of the degree
     */
    public String getDegreeName(){
        return this.name;
    }
    
    /**
     * Returns the number of the required credits of the degree
     * @return the number of the required credits of the degree
     */
    public String getMinCredits(){
        return this.minCredits; 
    }
    
    /**
     * Returns the number of the maximum credits of the degree
     * @return the number of the maximum credits of the degree
     */
    public String getMaxCredits(){
        return this.maxCredits;
    }
    
    /**
     * Returns the ID of the degree
     * @return the ID of the degree
     */
    public String getDegreeId(){
        return this.degreeId;
    }
    
}
