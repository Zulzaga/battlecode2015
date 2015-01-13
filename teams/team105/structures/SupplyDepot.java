package team105.structures;

import team105.Structure;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;


public class SupplyDepot extends Structure {

    public SupplyDepot(RobotController rc) throws GameActionException {
        super(rc);
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_SupplyDepot;
        initChannelNum(); 
    }
}


