package org.example.FireIncidentSubsystem.Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Severity class to validate its behavior for different levels of severity.
 */
class SeverityTest {
    private Severity lowSeverity;
    private Severity moderateSeverity;
    private Severity highSeverity;
    private Severity undefinedSeverity;

    /**
     * Initializes test instances of Severity with different levels.
     */
    @BeforeEach
    void setUp() {
        lowSeverity = new Severity("Low");
        moderateSeverity = new Severity("Moderate");
        highSeverity = new Severity("High");
        undefinedSeverity = new Severity("Undefined");
    }

    /**
     * Test case to validate the water amount based on severity level.
     * The water amount should be updated when set to "High".
     */
    @Test
    void testSetWaterAmount() {
        assertEquals(10, lowSeverity.getWaterAmount());
        lowSeverity.setWaterAmount("High");
        assertEquals(30, lowSeverity.getWaterAmount());
    }

    /**
     * Test case to check the severity level for different severity instances.
     * Verifies that the level corresponds to the correct string value.
     */
    @Test
    void testGetLevel() {
        assertEquals("Low", lowSeverity.getLevel(), "The level should be 'Low'");
        assertEquals("Moderate", moderateSeverity.getLevel(), "The level should be 'Moderate'");
        assertEquals("High", highSeverity.getLevel(), "The level should be 'High'");
        assertEquals("Undefined", undefinedSeverity.getLevel(), "The level should be 'Undefined'");
    }

    /**
     * Test case to validate the water amount for different severity levels.
     * Verifies that each severity level has the expected water amount.
     */
    @Test
    void testGetWaterAmount() {
        assertEquals(10, lowSeverity.getWaterAmount(), "Low severity should have water amount of 10");
        assertEquals(20, moderateSeverity.getWaterAmount(), "Moderate severity should have water amount of 20");
        assertEquals(30, highSeverity.getWaterAmount(), "High severity should have water amount of 30");
        assertEquals(0, undefinedSeverity.getWaterAmount(), "Undefined severity should default to water amount of 0");
    }
}
