package hugo.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import hugo.Structure;

public class TankFactory extends Structure {

    public TankFactory(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException{
    	spawnUnit(RobotType.TANK);
    	transferSupplies();
    	rc.yield();
    }

}
