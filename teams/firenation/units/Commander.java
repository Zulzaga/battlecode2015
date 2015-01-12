package firenation.units;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import firenation.Unit;

public class Commander extends Unit{

    public Commander(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Commander;
        initChannelNum(); 
    }

}
