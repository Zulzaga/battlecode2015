package iveel.structures;

import battlecode.common.*;
import iveel.Structure;

public class HQ extends Structure {

    public HQ(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException {
        attackEnemyZero();
        spawnUnit(RobotType.BEAVER);
        
        transferSupplies();
        rc.yield();
    }

}
