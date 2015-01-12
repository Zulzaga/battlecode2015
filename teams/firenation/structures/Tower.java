package firenation.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import firenation.Structure;

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
