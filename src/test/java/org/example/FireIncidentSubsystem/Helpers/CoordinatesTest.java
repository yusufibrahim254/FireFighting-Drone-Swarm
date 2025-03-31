package org.example.FireIncidentSubsystem.Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {
    private Coordinates coordinates;
    @BeforeEach
    void setUp() {
        int x = 100;
        int y = 200;
        coordinates = new Coordinates(x, y);
    }

    @Test
    void testGetX() {
        assertEquals(100, coordinates.getXCoords());
    }

    @Test
    void testGetY() {
        assertEquals(200, coordinates.getYCoords());
    }

}