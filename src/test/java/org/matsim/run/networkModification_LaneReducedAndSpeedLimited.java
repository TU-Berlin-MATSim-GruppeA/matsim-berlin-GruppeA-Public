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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class networkModification_LaneReducedAndSpeedLimited {
    public static void main(String[] args) throws IOException {
        /*I have created an array list to store the link ids read from the txt file. Not an array as we may not know how many
        * we are going to read.*/
        //List<Integer> linkIds = new ArrayList<Integer>();
        List<Integer> linkIds = new ArrayList<Integer>();
        /*I open the file using a scanner, to read the lines*/
        Scanner scanner = new Scanner(new File("src/test/java/org/matsim/run/linkIDs.txt"));
        /*I add each link id to the list of ids.*/
        while(scanner.hasNextInt())
        {
            int id = scanner.nextInt();
            linkIds.add(id);
        }
        System.out.println(linkIds.toString());

        //Path inputNetwork = Paths.get(args[0]);
        Path inputNetwork = Paths.get("/Users/haowu/Workspace/git/matsim-berlin-GruppeA/scenarios/berlin-v5.5-1pct/input/berlin-v5.5-network.xml.gz");
        //Path outputNetwork = Paths.get(args[1]);
        Path outputNetwork = Paths.get("/Users/haowu/Workspace/git/matsim-berlin-GruppeA/scenarios/berlin-v5.5-1pct/input/berlin-v5.5-network_LaneReducedAndSpeedLimited.xml.gz");
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputNetwork.toString());
        for(int i=0; i< linkIds.size(); i++){
            network.getLinks().get(Id.createLinkId(linkIds.get(i))).setNumberOfLanes(1);
            //network.getLinks().get(Id.createLinkId(linkIds.get(i).toString())).setNumberOfLanes(1);
            network.getLinks().get(Id.createLinkId(linkIds.get(i))).setFreespeed(30.0);
            //network.getLinks().get(Id.createLinkId(linkIds.get(i).toString())).setFreespeed(30.0);
        }
        new NetworkWriter(network).write(outputNetwork.toString());
    }
}
