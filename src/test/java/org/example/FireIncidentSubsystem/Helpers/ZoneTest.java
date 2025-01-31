package org.example.FireIncidentSubsystem.Helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoneTest {
    private Zone zone;
    private Coordinates start, end;

    @BeforeEach
    void setUp() {
        start = new Coordinates(100, 200);
        end = new Coordinates(400, 500);
        zone = new Zone(1, start, end);
    }

    @Test
    void testGetZoneId() {
        assertEquals(1, zone.getZoneId(), "Zone ID should be 1");
    }

    @Test
    void testGetZoneStart() {
        assertEquals(start, zone.getZoneStart(), "Zone start should be (100, 200)");
    }

    @Test
    void testGetZoneEnd() {
        assertEquals(end, zone.getZoneEnd(), "Zone end should be (400, 500)");
    }

    @Test
    void testZoneStartCoords(){
        assertEquals(start.getX(), zone.getZoneStart().getX(), "Zone start X coords should be 100");
        assertEquals(start.getY(), zone.getZoneStart().getY(), "Zone start Y coords should be 200");
    }

    @Test
    void testZoneEndCoords(){
        assertEquals(end.getX(), zone.getZoneEnd().getX(), "Zone end X coords should be 400");
        assertEquals(end.getY(), zone.getZoneEnd().getY(), "Zone end Y coords should be 500");
    }

}