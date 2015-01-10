package hugo.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import hugo.Structure;

public class Helipad extends Structure{

    public Helipad(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException{
    	spawnUnit(RobotType.DRONE);
    	rc.yield();
    }

}
