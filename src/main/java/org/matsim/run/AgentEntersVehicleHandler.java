package org.matsim.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AgentEntersVehicleHandler implements PersonEntersVehicleEventHandler, PersonLeavesVehicleEventHandler, LinkEnterEventHandler {
    public static Set<Id> vehicleIDSet = new HashSet<Id>();
    public static Set<Id> agentIDSet = new HashSet<Id>();
    public static Map<Id, Double> timeSpentTraffic = new HashMap<Id, Double>();
    //Stores the distance of all links
    public static Map<Id, Double> distanceOfLinks = new HashMap<Id, Double>();
    //Stores the distance travelled by the agents
    public static Map<Id, Double> distanceTravelled = new HashMap<Id, Double>();
    //Stores time the agent gets into a vehicle for later use in PersonLeavesVehicleEvent
    public static Map<Id, Double> timeMap = new HashMap<Id, Double>();
    //stores which agents use which vehicles
    public static Map<Id,Id> agentUsingVehicle = new HashMap<Id, Id>();

    public AgentEntersVehicleHandler(String vehiclIDlist) throws IOException, ParserConfigurationException, SAXException {
        Scanner scanner = new Scanner(new File(vehiclIDlist));
        while(scanner.hasNextLine())
        {
            String id = scanner.nextLine();
            vehicleIDSet.add(Id.createVehicleId(id));

        }
        System.out.println(vehiclIDlist.toString());
        //Now we are going to store all the link distances in the map:
        File network = new File("C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/scenarios/equil/networkBerlinModified.xml");
        Scanner sc = new Scanner(network);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dct = builder.parse(network);
        dct.getDocumentElement().normalize();
        NodeList nodeList = dct.getElementsByTagName("link");
        for(int i=0; i<nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element e = (Element) node;
                Double linkDist = Double.parseDouble(e.getAttribute("length"));
                distanceOfLinks.put(Id.createLinkId(e.getAttribute("id")), linkDist);
            }
        }
    }

    public void handleEvent(LinkEnterEvent event){
        Id linkID = event.getLinkId();
        Id vehicle = event.getVehicleId();
        if(vehicleIDSet.contains(vehicle)) {
            Id person = agentUsingVehicle.get(vehicle);
            Double linkDistance = distanceOfLinks.get(linkID);
            distanceTravelled.put(person, distanceTravelled.get(person) + linkDistance);
        }
    }

    public void handleEvent(PersonEntersVehicleEvent event){
        Id vehicle = event.getVehicleId();
        Id person = event.getPersonId();
        if (vehicleIDSet.contains(vehicle)){
            agentIDSet.add(person);
            timeMap.put(person, event.getTime());
            timeSpentTraffic.put(person, 0.0);
            distanceTravelled.put(person, 0.0);
            agentUsingVehicle.put(vehicle, person);

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
        BufferedWriter writer3 = new BufferedWriter(new FileWriter("agentIDTotalDistanceTravelled.txt"));

        for (Id agent : agentIDSet) {
            writer.write(agent.toString() + "\n");
            writer2.write(timeSpentTraffic.get(agent).toString()+"\n");
            writer3.write(distanceTravelled.get(agent).toString()+"\n");
        }
        writer.close();
        writer2.close();
        writer3.close();
    }
}
