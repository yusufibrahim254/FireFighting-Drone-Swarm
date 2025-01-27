package org.example.FireIncidentSubsystem.Helpers;

public class Severity {
    private final String level;
    private final int waterAmount;

    public Severity(String level, int waterAmount){
        this.level = level;
        this.waterAmount = waterAmount;
    }

    public String getLevel() {
        return level;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

}
