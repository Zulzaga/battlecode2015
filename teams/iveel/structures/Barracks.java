package iveel.structures;

import battlecode.common.*;
import iveel.Structure;

public class Barracks extends Structure{

    public Barracks(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {
        spawnUnit(RobotType.SOLDIER);
        
        transferSupplies();
        rc.yield();
    }

}
