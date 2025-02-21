package org.example.FireIncidentSubsystem.Helpers;

/**
 * A helper class that manages Severity levels for fire incidents
 * and allocates the amount of water/agent needed to extinguish
 */
public class Severity {
    private final String level;
    private int waterAmount;

    private static final int LOW_SEVERITY = 10;
    private static final int MODERATE_SEVERITY = 20;
    private static final int HIGH_SEVERITY = 30;

    /**
     * Severity constructor
     * @param level severity level
     */
    public Severity(String level){
        this.level = level;
        setWaterAmount(level); // static value
    }

    /**
     * Set the water amount based on the severity level
     * @param level the severity level
     */
    public void setWaterAmount(String level){
        switch (level) {
            case "Low" -> waterAmount = LOW_SEVERITY;
            case "Moderate" -> waterAmount = MODERATE_SEVERITY;
            case "High" -> waterAmount = HIGH_SEVERITY;
            default -> waterAmount = 0; // invalid value
        }
    }

    /**
     * Get the severity level
     * @return the severity level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Get the amount of water needed for the severity
     * @return the amount of water
     */
    public int getWaterAmount() {
        return waterAmount;
    }

}
