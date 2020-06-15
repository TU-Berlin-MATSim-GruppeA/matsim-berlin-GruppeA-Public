package org.matsim.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class networkModification_LaneReducedAndSpeedLimitedWithNoCarZone_ptSpeedLimited {
    public static void main(String[] args) throws IOException {
        /*I have created an array list to store the link ids read from the txt file. Not an array as we may not know how many
        * we are going to read.*/
        int j = 0;
        List<Integer> linkIdsStreet = new ArrayList<Integer>();
        List<Integer> linkIdsIntersection = new ArrayList<Integer>();
        List<Integer> linkIdsPublicTransportation = new ArrayList<Integer>();
        /*I open the file using a scanner, to read the lines*/
        Scanner scanner = new Scanner(new File("src/test/java/org/matsim/run/linkIDs_ptSpeedLimited.txt"));
        /*I add each link id to the list of ids.*/
        while(scanner.hasNextInt())
        {
            j++;
            int id = scanner.nextInt();
            if(j < 8){
                linkIdsIntersection.add(id);
            }
            else if(j<79){
                linkIdsStreet.add(id);
            }
            else{
                linkIdsPublicTransportation.add(id);
        }
        }
        System.out.println(linkIdsStreet.toString());
        System.out.println(linkIdsIntersection.toString());

        Path inputNetwork = Paths.get(args[0]);
        Path outputNetwork = Paths.get(args[1]);
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputNetwork.toString());
        for(int i=0; i< linkIdsStreet.size(); i++){
            network.getLinks().get(Id.createLinkId(linkIdsStreet.get(i))).setNumberOfLanes(1);
            //network.getLinks().get(Id.createLinkId(linkIdsStreet.get(i))).setFreespeed(5.5556);
            network.getLinks().get(Id.createLinkId(linkIdsStreet.get(i))).setFreespeed(20/3.6);
        }
        Set<String> allowedModes = new HashSet<String>();
        //allowedModes.add("ride");
        allowedModes.add("walk");
        //allowedModes.add("bicycle");
        for(int i=0; i < linkIdsIntersection.size(); i++){
            System.out.println(i);
            network.getLinks().get(Id.createLinkId(linkIdsIntersection.get(i))).setAllowedModes(allowedModes);
        }
        for(int i=0; i < linkIdsPublicTransportation.size(); i++){
            System.out.println(i);
            //network.getLinks().get(Id.createLinkId(linkIdsStreet.get(i))).setNumberOfLanes(1);
            //network.getLinks().get(Id.createLinkId(linkIdsStreet.get(i))).setFreespeed(5.5556);
            network.getLinks().get(Id.createLinkId(linkIdsPublicTransportation.get(i))).setFreespeed(20/3.6);
        }
        new NetworkWriter(network).write(outputNetwork.toString());

    }
}
