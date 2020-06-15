package org.matsim.run;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class RunEventsHandler {
    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException {
        String inputFile = "C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/output-berlin-v5.5-1pct_LaneReducedAndSpeedLimitedWithNoCarZone_AddWalk_DeleteRoutes_BasedCase_100iterations/berlin-v5.5-1pct.output_events.xml.gz";

        EventsManager eventsManager = EventsUtils.createEventsManager();
        LinkEventHandler eventHandler = new LinkEventHandler("C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/src/main/java/org/matsim/run/linkIDs.txt");
        //AffectedAgentHandler eventHandler = new AffectedAgentHandler("C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/TXT_IMPORTANT_FILES/realAffectedAgents.txt", "C:/Users/javie/Desktop/TUB/MATSIMv2/Open-Berlin-Project--Javier-Frances-Martinez/TXT_IMPORTANT_FILES/vehicleIDs.txt");
        eventsManager.addHandler(eventHandler);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        eventHandler.printResult();

    }
}
