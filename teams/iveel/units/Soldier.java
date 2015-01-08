package iveel.units;

import battlecode.common.*;
import iveel.Unit;

public class Soldier extends Unit {

    public Soldier(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException {
        attackEnemyZero();
        moveAround();
        
        transferSupplies();
        rc.yield();
    }

}
