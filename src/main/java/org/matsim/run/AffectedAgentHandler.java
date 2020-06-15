package org.matsim.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AffectedAgentHandler implements PersonDepartureEventHandler, PersonArrivalEventHandler, LinkEnterEventHandler{
    public static double totalTimeTravelled = 0.0;
    public static double totalDistanceTravelled = 0.0;
    public Map<Id, Double> timeMap = new HashMap<Id, Double>();
    public Set<Id> agentIdList = new HashSet<Id>();
    public Map<Id, Double> distanceOfLinks = new HashMap<Id, Double>();
    public Set<Id> vehicleIdlist = new HashSet<Id>();

    public AffectedAgentHandler(String agentIDlistFile, String vehicleIDlistFile) throws IOException, ParserConfigurationException, SAXException {
        Scanner scanner = new Scanner(new File(agentIDlistFile));
        while(scanner.hasNextLine())
        {
            String id = scanner.nextLine();
            agentIdList.add(Id.createVehicleId(id));

        }
        scanner.close();
        Scanner scanner2 = new Scanner(new File(vehicleIDlistFile));
        while(scanner2.hasNextLine())
        {
            String id = scanner2.nextLine();
            vehicleIdlist.add(Id.createVehicleId(id));
        }
        System.out.println(agentIdList.size());
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

    public void handleEvent(PersonDepartureEvent event){
        Id person = event.getPersonId();
        if(agentIdList.contains(person)){
            timeMap.put(person, event.getTime());
        }
    }
    public void handleEvent(PersonArrivalEvent event){
        Id person = event.getPersonId();
        if(agentIdList.contains(person)){
            Double timeInTraffic = event.getTime() - timeMap.get(person);
            totalTimeTravelled += timeInTraffic;
        }

    }

    public void handleEvent(LinkEnterEvent event){
        Id vehicle = event.getVehicleId();
        if(vehicleIdlist.contains(vehicle)){
            Double linkLength = distanceOfLinks.get(event.getLinkId());
            totalDistanceTravelled += linkLength;
        }
    }

    public void printResult() throws IOException {
        System.out.println(totalTimeTravelled/3600);
        System.out.print(totalDistanceTravelled/1000);
    }


}

