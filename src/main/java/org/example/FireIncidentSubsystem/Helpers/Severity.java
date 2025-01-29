package org.example.FireIncidentSubsystem.Helpers;

public class Severity {
    private final String level;
    private int waterAmount;

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
            case "Low" -> waterAmount = 10;
            case "Moderate" -> waterAmount = 20;
            case "High" -> waterAmount = 30;
            default -> waterAmount = 0; // invalid value
        }
    }

    /**
     * Get the severity level
     * @return the serverity level
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
