package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;
import org.example.FireIncidentSubsystem.Helpers.Zones;

import java.util.LinkedList;

public class ConsoleController {
    private Zones zonesModel;

    public ConsoleController(){
        String zonesFilePath = "docs/sample_zone_file.csv";
        this.zonesModel = new Zones(zonesFilePath);
    }

    public LinkedList<Zone> getZones(String filePath){
        return zonesModel.loadZonesFromCSV(filePath);
    }
}
