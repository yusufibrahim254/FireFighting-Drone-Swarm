package org.example.FireIncidentSubsystem.Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Coordinates class.
 */
class CoordinatesTest {
    private Coordinates coordinates;

    /**
     * Set up the test environment before each test.
     * Initializes a Coordinates instance with sample x and y values.
     */
    @BeforeEach
    void setUp() {
        int x = 100;
        int y = 200;
        coordinates = new Coordinates(x, y);
    }

    /**
     * Test the getXCoords method.
     * Verifies that the correct x-coordinate value is returned.
     */
    @Test
    void testGetX() {
        assertEquals(100, coordinates.getXCoords());
    }

    /**
     * Test the getYCoords method.
     * Verifies that the correct y-coordinate value is returned.
     */
    @Test
    void testGetY() {
        assertEquals(200, coordinates.getYCoords());
    }
}
