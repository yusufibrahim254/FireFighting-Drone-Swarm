package org.example.FireIncidentSubsystem.Helpers;

public class Severity {
    private final String level;
    private int waterAmount;

    public Severity(String level){
        this.level = level;
        setWaterAmount(level);
    }

    public void setWaterAmount(String level){
        switch (level) {
            case "Low" -> waterAmount = 10;
            case "Moderate" -> waterAmount = 20;
            case "High" -> waterAmount = 30;
            default -> waterAmount = 0;
        }
    }

    public String getLevel() {
        return level;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

}
