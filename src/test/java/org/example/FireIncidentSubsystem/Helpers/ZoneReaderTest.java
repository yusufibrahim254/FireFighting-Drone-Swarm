package org.example.FireIncidentSubsystem.Helpers;

import org.example.FireIncidentSubsystem.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoneReaderTest {
    private Event event;
    private static final String testFile = "docs/sample_zone_file.csv";

    @BeforeEach
    void setUp(){
        event = new Event(8, "12:12:12", 2, EventType.DRONE_REQUEST, "High", fault);

    }

    @Test
    void readZone() {
        Zone zone = ZoneReader.readZone(event.getZoneId(), testFile);

        assertNotNull(zone);
        assertEquals(2, zone.getZoneId());
        assertEquals(200, zone.getZoneStart().getX());
        assertEquals(0, zone.getZoneStart().getY());
        assertEquals(400, zone.getZoneEnd().getX());
        assertEquals(200, zone.getZoneEnd().getY());

        assertEquals(new Coordinates(200, 0), zone.getZoneStart());
    }
}