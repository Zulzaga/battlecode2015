package team105.units;

import team105.Unit;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Basher extends Unit {

    public int supplyUpkeep = 6;
    public Basher(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Basher;
        initChannelNum(); 
    }


}

