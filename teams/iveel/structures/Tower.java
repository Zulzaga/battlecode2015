package iveel.structures;

import battlecode.common.*;
import iveel.Structure;

public class Tower extends Structure{

    public Tower(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Tower;
        initChannelNum(); 
    }
    
    public void execute() throws GameActionException {
        attackEnemyZero();
        
        transferSupplies();
        rc.yield();
    }

}
