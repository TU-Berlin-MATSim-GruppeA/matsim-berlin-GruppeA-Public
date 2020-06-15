package org.matsim.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LinkEventHandler implements LinkEnterEventHandler{
    public static List<Id> linkIdsStreet = new ArrayList<Id>();
    public static List<Id> linkIdsIntersection = new ArrayList<Id>();
    //Two maps to store the times at which each agent enters/leaves the street:
    public static Map<Id, Double> agentHasEnteredStreet = new HashMap<Id, Double>();
    public static Map<Id, Double> agentHasLeftStreet = new HashMap<Id, Double>();
    //Two maps to store through which links does each agent enter/leave the street:
    public static Map<Id, Id> agentHasEnteredStreetLinkId = new HashMap<Id, Id>();
    public static Map<Id, Id> agentHasLeftStreetLinkId = new HashMap<Id, Id>();
    //Map that stores the amount of time agents spend in the street:
    public static Map<Id, Double> timeInStreet = new HashMap<Id, Double>();

    public LinkEventHandler(String linkIDfile)throws IOException{
        int j = 0;

        /*I open the file using a scanner, to read the lines*/
        Scanner scanner = new Scanner(new File(linkIDfile));
        /*I add each link id to the list of ids.*/
        while(scanner.hasNextInt())
        {
            j++;
            int id = scanner.nextInt();
            if(j < 8){
                linkIdsIntersection.add(Id.createLinkId(id));
            }
            else{
                linkIdsStreet.add(Id.createLinkId(id));
            }

        }
        System.out.println(linkIdsStreet.toString());
        System.out.println(linkIdsIntersection.toString());
    }

    public void handleEvent(LinkEnterEvent event){
        double timeOfEvent = event.getTime();
        Id linkId = event.getLinkId();
        Id vehicleId = event.getVehicleId();
        if (linkIdsStreet.contains(linkId)){
            if(!agentHasEnteredStreet.containsKey(vehicleId)){
                //This is the first time the person has entered a link belonging to the street
                agentHasEnteredStreet.put(vehicleId, timeOfEvent);
                agentHasEnteredStreetLinkId.put(vehicleId, event.getLinkId());
                //Initializes timeInStreet to 0 seconds:
                timeInStreet.putIfAbsent(vehicleId, 0.0);
            }
        }
        else if (agentHasEnteredStreet.containsKey(vehicleId)){
            //Has entered the street previously, but now is entering a new link not belonging to the street, so it is leaving the street.
            agentHasLeftStreet.putIfAbsent(vehicleId, timeOfEvent);
            agentHasLeftStreetLinkId.putIfAbsent(vehicleId, event.getLinkId());

            //Update time spent in the street:
            timeInStreet.put(vehicleId, timeInStreet.get(vehicleId) + (agentHasLeftStreet.get(vehicleId) - agentHasEnteredStreet.get(vehicleId)));

            System.out.print("Vehicle with id: " + vehicleId + " has entered through link " + agentHasEnteredStreetLinkId.get(vehicleId) + " at time: ");
            System.out.print(formatTime(agentHasEnteredStreet.get(vehicleId)));
            System.out.print(", and has left through link " + agentHasLeftStreetLinkId.get(vehicleId) + " at time: ");
            System.out.println(formatTime(agentHasLeftStreet.get(vehicleId)));
            agentHasEnteredStreet.remove(vehicleId);
            agentHasEnteredStreetLinkId.remove(vehicleId);
            agentHasLeftStreet.remove(vehicleId);
            agentHasLeftStreetLinkId.remove(vehicleId);
        }

    }
    //278102301 important agent

    public String formatTime(Double time){
        //Given the time in seconds, it returns said time in the format hh/mm/ss.
        int timeInSeconds = (int)Math.floor(time);
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }

    public void printResult() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("timeInStreet.txt"));
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("vehicleIDStreet.txt"));
        System.out.println("**************************");
        System.out.println("Total agents afected: " + timeInStreet.keySet().size());
        System.out.println("**************************");

        for (Id key : timeInStreet.keySet()) {
            System.out.println("Vehicle with id: " + key + " has been " + timeInStreet.get(key) + " seconds in the street.");
            writer.write(timeInStreet.get(key).toString() + "\n");
            writer2.write(key.toString() + "\n");
        }
        writer.close();
        writer2.close();


    }


}
