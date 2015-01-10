package hugo.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import hugo.Structure;

public class Tower extends Structure {

    public Tower(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException{
    	attackEnemyZero();
        
        transferSupplies();
        rc.yield();
    }

}
