package firenation.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import firenation.Structure;

public class HandwashStation extends Structure{

    public HandwashStation(RobotController rc) throws GameActionException {
        super(rc);

        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_HandwashStation;
        initChannelNum(); 
    }

}
