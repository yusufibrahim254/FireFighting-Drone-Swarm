package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;
import org.example.FireIncidentSubsystem.Helpers.Zones;

import java.util.LinkedList;

public class ConsoleController {
    private Zones zonesModel;

    /**
     * Constructor for the console controller
     */
    public ConsoleController(){
        String zonesFilePath = "docs/sample_zone_file.csv";
        this.zonesModel = new Zones(zonesFilePath);
    }

    /**
     * Load the zones into a linked list
     * @param filePath zone CSV
     * @return linked list of all zones
     */
    public LinkedList<Zone> getZones(String filePath){
        return zonesModel.loadZonesFromCSV(filePath);
    }
}
