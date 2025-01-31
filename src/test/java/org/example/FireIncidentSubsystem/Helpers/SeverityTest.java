package org.example.FireIncidentSubsystem.Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeverityTest {
    private Severity lowSeverity;
    private Severity moderateSeverity;
    private Severity highSeverity;
    private Severity undefinedSeverity;

    @BeforeEach
    void setUp() {
        lowSeverity = new Severity("Low");
        moderateSeverity = new Severity("Moderate");
        highSeverity = new Severity("High");
        undefinedSeverity = new Severity("Undefined");
    }

    @Test
    void testSetWaterAmount() {
        assertEquals(10, lowSeverity.getWaterAmount());
        lowSeverity.setWaterAmount("High");
        assertEquals(30, lowSeverity.getWaterAmount());
    }

    @Test
    void testGetLevel() {
        assertEquals("Low", lowSeverity.getLevel(), "The level should be 'Low'");
        assertEquals("Moderate", moderateSeverity.getLevel(), "The level should be 'Moderate'");
        assertEquals("High", highSeverity.getLevel(), "The level should be 'High'");
        assertEquals("Undefined", undefinedSeverity.getLevel(), "The level should be 'Undefined'");
    }

    @Test
    void testGetWaterAmount() {
        assertEquals(10, lowSeverity.getWaterAmount(), "Low severity should have water amount of 10");
        assertEquals(20, moderateSeverity.getWaterAmount(), "Moderate severity should have water amount of 20");
        assertEquals(30, highSeverity.getWaterAmount(), "High severity should have water amount of 30");
        assertEquals(0, undefinedSeverity.getWaterAmount(), "Undefined severity should default to water amount of 0");
    }
}