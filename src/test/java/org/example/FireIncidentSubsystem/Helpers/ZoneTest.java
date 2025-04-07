package org.example.FireIncidentSubsystem.Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Zone class to validate its functionality and correct handling of coordinates.
 */
class ZoneTest {
    private Zone zone;
    private Coordinates start, end;

    /**
     * Initializes the test instances of Zone and Coordinates for testing.
     */
    @BeforeEach
    void setUp() {
        start = new Coordinates(100, 200);
        end = new Coordinates(400, 500);
        zone = new Zone(1, start, end);
    }

    /**
     * Test case to validate the zone ID is correctly returned.
     * Verifies that the Zone ID matches the expected value of 1.
     */
    @Test
    void testGetZoneId() {
        assertEquals(1, zone.getZoneId(), "Zone ID should be 1");
    }

    /**
     * Test case to validate the start coordinates of the zone.
     * Verifies that the start coordinates match the expected values.
     */
    @Test
    void testGetZoneStart() {
        assertEquals(start, zone.getZoneStart(), "Zone start should be (100, 200)");
    }

    /**
     * Test case to validate the end coordinates of the zone.
     * Verifies that the end coordinates match the expected values.
     */
    @Test
    void testGetZoneEnd() {
        assertEquals(end, zone.getZoneEnd(), "Zone end should be (400, 500)");
    }

    /**
     * Test case to validate the X-coordinate of the zone's start position.
     * Verifies that the X-coordinate of the zone start is correct.
     */
    @Test
    void testZoneStartCoords() {
        assertEquals(start.getXCoords(), zone.getZoneStart().getXCoords(), "Zone start X coords should be 100");
        assertEquals(start.getYCoords(), zone.getZoneStart().getYCoords(), "Zone start Y coords should be 200");
    }

    /**
     * Test case to validate the X-coordinate of the zone's end position.
     * Verifies that the X-coordinate of the zone end is correct.
     */
    @Test
    void testZoneEndCoords() {
        assertEquals(end.getXCoords(), zone.getZoneEnd().getXCoords(), "Zone end X coords should be 400");
        assertEquals(end.getYCoords(), zone.getZoneEnd().getYCoords(), "Zone end Y coords should be 500");
    }
}
