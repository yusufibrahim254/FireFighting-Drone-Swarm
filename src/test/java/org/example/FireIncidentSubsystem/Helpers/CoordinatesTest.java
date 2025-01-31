package org.example.FireIncidentSubsystem.Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {
    private int x;
    private int y;
    private Coordinates coordinates;
    @BeforeEach
    void setUp() {
        x = 100;
        y = 200;
        coordinates = new Coordinates(x, y);
    }

    @Test
    void getX() {
        assertEquals(100, coordinates.getX());
    }

    @Test
    void getY() {
        assertEquals(200, coordinates.getY());

       // this is my change
    }
}