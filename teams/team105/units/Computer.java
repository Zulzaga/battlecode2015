package team105.units;

import team105.Unit;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Computer extends Unit{

    public Computer(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Computer;
        initChannelNum(); 
        supplyUpkeep = 2;
    }

}
