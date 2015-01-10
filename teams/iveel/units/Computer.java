package iveel.units;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import iveel.Unit;

public class Computer extends Unit{

    public Computer(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Computer;
        initChannelNum(); 
    }

}
