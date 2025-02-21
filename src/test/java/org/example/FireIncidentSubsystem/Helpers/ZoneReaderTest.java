package org.example.FireIncidentSubsystem.Helpers;

import org.example.FireIncidentSubsystem.Event;
import org.example.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoneReaderTest {
    private Event event;
    private static final String testFile = "docs/sample_zone_file.csv";

    @BeforeEach
    void setUp(){
        event = new Event("12:12:12", 2, EventType.DRONE_REQUEST, "High");

    }

    @Test
    void readZone() {
        Zone zone = ZoneReader.readZone(event.getZoneId(), testFile);

        assertNotNull(zone);
        assertEquals(2, zone.getZoneId());
        assertEquals(0, zone.getZoneStart().getX());
        assertEquals(600, zone.getZoneStart().getY());
        assertEquals(650, zone.getZoneEnd().getX());
        assertEquals(1500, zone.getZoneEnd().getY());

        assertEquals(new Coordinates(0, 600), zone.getZoneStart());
    }
}