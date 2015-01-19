package team105.units;

import team105.Unit;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Commander extends Unit{

    public Commander(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Commander;
        initChannelNum(); 
        supplyUpkeep = 5;
    }

}
