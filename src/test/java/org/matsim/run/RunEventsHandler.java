package org.matsim.run;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

import java.io.IOException;

public class RunEventsHandler {
    public static void main(String args[]) throws IOException {
        String inputFile = "C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/output-berlin-v5.5-1pct_LaneReducedAndSpeedLimitedWithNoCarZone_AddWalk_TxtFileUpdated2_RideToBicycle_50Iterations/berlin-v5.5-1pct.output_events.xml.gz";

        EventsManager eventsManager = EventsUtils.createEventsManager();
        //LinkEventHandler eventHandler = new LinkEventHandler("C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/src/main/java/org/matsim/run/linkIDs.txt");
        AgentEntersVehicleHandler eventHandler = new AgentEntersVehicleHandler("C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/vehicleIDStreet.txt");
        eventsManager.addHandler(eventHandler);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        eventHandler.printResult();

    }
}
