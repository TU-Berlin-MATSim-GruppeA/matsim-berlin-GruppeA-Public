package org.matsim.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;

import java.io.*;
import java.util.*;

public class AgentEntersVehicleHandler implements PersonEntersVehicleEventHandler, PersonLeavesVehicleEventHandler {
    public static Set<Id> vehicleIDSet = new HashSet<Id>();
    public static Set<Id> agentIDSet = new HashSet<Id>();
    public static Map<Id, Double> timeSpentTraffic = new HashMap<Id, Double>();
    //Stores time the agent gets into a vehicle for later use in PersonLeavesVehicleEvent
    public static Map<Id, Double> timeMap = new HashMap<Id, Double>();

    public AgentEntersVehicleHandler(String vehiclIDlist) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(vehiclIDlist));
        while(scanner.hasNextLine())
        {
            String id = scanner.nextLine();
            vehicleIDSet.add(Id.createVehicleId(id));

        }
        System.out.println(vehiclIDlist.toString());
    }

    public void handleEvent(PersonEntersVehicleEvent event){
        Id vehicle = event.getVehicleId();
        Id person = event.getPersonId();
        if (vehicleIDSet.contains(vehicle)){
            agentIDSet.add(person);
            timeMap.put(person, event.getTime());
            timeSpentTraffic.put(person, 0.0);
        }
    }

    public void handleEvent(PersonLeavesVehicleEvent event){
        Id person = event.getPersonId();
        if(agentIDSet.contains(person)){
            Double timeInTraffic = timeSpentTraffic.get(person)+(event.getTime()-timeMap.get(person));
            timeSpentTraffic.put(person, timeInTraffic);
        }
    }

    public void printResult() throws IOException {
        System.out.println(agentIDSet.toString());
        System.out.println("Number of agents going through the street: " + agentIDSet.size());

        BufferedWriter writer = new BufferedWriter(new FileWriter("agentIDStreet.txt"));
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("agentIDTotalTimeInTraffic.txt"));
        for (Id agent : agentIDSet) {
            writer.write(agent.toString() + "\n");
            writer2.write(timeSpentTraffic.get(agent).toString()+"\n");
        }
        writer.close();
        writer2.close();
    }
}
